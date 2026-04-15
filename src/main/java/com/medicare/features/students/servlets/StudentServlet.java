package com.medicare.features.students.servlets;

import com.medicare.features.students.services.StudentService;
import com.medicare.models.Student;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/students", "/students/*" })
public class StudentServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(StudentServlet.class.getName());
  private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s'.-]*$");

  private final StudentService studentService = new StudentService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String pathInfo = request.getPathInfo();
    ServletRequestUtils.exposeAlertsFromQuery(request);
    try {
      if (pathInfo == null || pathInfo.equals("/")) {
        String query = ServletRequestUtils.trim(request.getParameter("q"));
        if (query != null && !query.isBlank()) {
          request.setAttribute("students", studentService.searchStudents(query));
          request.setAttribute("query", query);
        } else {
          request.setAttribute("students", studentService.getAllStudents());
        }
        request.getRequestDispatcher("/WEB-INF/views/students/list.jsp").forward(request, response);
      } else if (pathInfo.equals("/new")) {
        request.getRequestDispatcher("/WEB-INF/views/students/form.jsp").forward(request, response);
      } else {
        String regNumber = pathInfo.substring(1);
        Student student = studentService.getStudentByRegNumber(Integer.parseInt(regNumber)).orElse(null);
        if (student == null) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND);
          return;
        }
        request.setAttribute("student", student);
        request.setAttribute("originalRegNumber", student.getRegNumber());
        request.getRequestDispatcher("/WEB-INF/views/students/form.jsp").forward(request, response);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "StudentServlet GET error", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String action = ServletRequestUtils.trim(request.getParameter("action"));
    if ("delete".equalsIgnoreCase(action)) {
      deleteStudent(request, response);
      return;
    }
    saveStudent(request, response);
  }

  private void saveStudent(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String originalRegNumber = ServletRequestUtils.trim(request.getParameter("originalRegNumber"));
    String regNumberRaw = ServletRequestUtils.trim(request.getParameter("regNumber"));
    String firstName = ServletRequestUtils.trim(request.getParameter("firstName"));
    String lastName = ServletRequestUtils.trim(request.getParameter("lastName"));
    String dobRaw = ServletRequestUtils.trim(request.getParameter("dob"));
    String faculty = ServletRequestUtils.trim(request.getParameter("faculty"));
    String email = ServletRequestUtils.trim(request.getParameter("email"));
    String phone = ServletRequestUtils.trim(request.getParameter("phone"));
    String address = ServletRequestUtils.trim(request.getParameter("address"));
    String emergencyContact = ServletRequestUtils.trim(request.getParameter("emergencyContact"));

    boolean isCreate = originalRegNumber == null || originalRegNumber.isBlank();

    if (regNumberRaw == null || regNumberRaw.isBlank() || !regNumberRaw.matches("\\d+")) {
      forwardWithError(request, response, "Registration number is required.",
          originalRegNumber, regNumberRaw, firstName, lastName, dobRaw, faculty, email, phone, address,
          emergencyContact);
      return;
    }
    int regNumber = Integer.parseInt(regNumberRaw);

    if (firstName == null || firstName.isBlank()) {
      forwardWithError(request, response, "First name is required.",
          originalRegNumber, regNumberRaw, firstName, lastName, dobRaw, faculty, email, phone, address,
          emergencyContact);
      return;
    }

    if (lastName == null || lastName.isBlank()) {
      forwardWithError(request, response, "Last name is required.",
          originalRegNumber, regNumberRaw, firstName, lastName, dobRaw, faculty, email, phone, address,
          emergencyContact);
      return;
    }

    if (!NAME_PATTERN.matcher(firstName).matches() || !NAME_PATTERN.matcher(lastName).matches()) {
      forwardWithError(request, response, "Names cannot contain numbers.",
          originalRegNumber, regNumberRaw, firstName, lastName, dobRaw, faculty, email, phone, address,
          emergencyContact);
      return;
    }

    if (faculty == null || faculty.isBlank()) {
      forwardWithError(request, response, "Faculty is required.",
          originalRegNumber, regNumberRaw, firstName, lastName, dobRaw, faculty, email, phone, address,
          emergencyContact);
      return;
    }

    LocalDate dob;
    try {
      dob = LocalDate.parse(dobRaw);
    } catch (DateTimeParseException | NullPointerException e) {
      forwardWithError(request, response, "A valid date of birth is required.",
          originalRegNumber, regNumberRaw, firstName, lastName, dobRaw, faculty, email, phone, address,
          emergencyContact);
      return;
    }

    if (dob.isAfter(LocalDate.now())) {
      forwardWithError(request, response, "Date of birth cannot be in the future.",
          originalRegNumber, regNumberRaw, firstName, lastName, dobRaw, faculty, email, phone, address,
          emergencyContact);
      return;
    }

    Student student = new Student();
    student.setRegNumber(regNumber);
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setDob(dob);
    student.setFaculty(faculty);
    student.setEmail(email);
    student.setPhone(phone);
    student.setAddress(address);
    student.setEmergencyContact(emergencyContact);

    try {
      if (isCreate) {
        if (studentService.getStudentByRegNumber(regNumber).isPresent()) {
          forwardWithError(request, response, "Registration number already exists.",
              originalRegNumber, regNumberRaw, firstName, lastName, dobRaw, faculty, email, phone, address,
              emergencyContact);
          return;
        }
        studentService.createStudent(student);
        ServletRequestUtils.redirectWithMessage(request, response, "/students", "success",
            "Student registered successfully.");
        return;
      }

      if (!originalRegNumber.equals(regNumberRaw)) {
        forwardWithError(request, response, "Registration number cannot be changed.",
        originalRegNumber, originalRegNumber, firstName, lastName, dobRaw, faculty, email, phone, address,
        emergencyContact);
        return;
      }

      studentService.updateStudent(student);
      ServletRequestUtils.redirectWithMessage(request, response, "/students", "success",
          "Student updated successfully.");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "StudentServlet POST save error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/students", "error",
          "A system error occurred while saving the student.");
    }
  }

  private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String regNumberRaw = ServletRequestUtils.trim(request.getParameter("regNumber"));
    if (regNumberRaw == null || regNumberRaw.isBlank() || !regNumberRaw.matches("\\d+")) {
      ServletRequestUtils.redirectWithMessage(request, response, "/students", "error",
          "Registration number is required to delete.");
      return;
    }

    try {
      studentService.deleteStudent(Integer.parseInt(regNumberRaw));
      ServletRequestUtils.redirectWithMessage(request, response, "/students", "success",
          "Student deleted successfully.");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "StudentServlet POST delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/students", "error",
          "A system error occurred while deleting the student.");
    }
  }

  private void forwardWithError(HttpServletRequest request,
      HttpServletResponse response,
      String errorMessage,
      String originalRegNumber,
      String regNumberRaw,
      String firstName,
      String lastName,
      String dobRaw,
      String faculty,
      String email,
      String phone,
      String address,
      String emergencyContact) throws ServletException, IOException {
    Student student = new Student();
    if (regNumberRaw != null && regNumberRaw.matches("\\d+")) {
      student.setRegNumber(Integer.parseInt(regNumberRaw));
    }
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setFaculty(faculty);
    student.setEmail(email);
    student.setPhone(phone);
    student.setAddress(address);
    student.setEmergencyContact(emergencyContact);

    if (dobRaw != null && !dobRaw.isBlank()) {
      try {
        student.setDob(LocalDate.parse(dobRaw));
      } catch (DateTimeParseException ignored) {
        // Keep DOB unset if malformed so user can correct it.
      }
    }

    request.setAttribute("error", errorMessage);
    request.setAttribute("student", student);
    request.setAttribute("originalRegNumber", originalRegNumber);
    request.getRequestDispatcher("/WEB-INF/views/students/form.jsp").forward(request, response);
  }
}
