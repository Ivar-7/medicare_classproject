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
  private static final Pattern FULL_NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s'.-]*$");

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
    String regNumber = ServletRequestUtils.trim(request.getParameter("regNumber"));
    String fullName = ServletRequestUtils.trim(request.getParameter("fullName"));
    String dobRaw = ServletRequestUtils.trim(request.getParameter("dob"));
    String gender = ServletRequestUtils.trim(request.getParameter("gender"));
    String faculty = ServletRequestUtils.trim(request.getParameter("faculty"));
    String contact = ServletRequestUtils.trim(request.getParameter("contact"));

    boolean isCreate = originalRegNumber == null || originalRegNumber.isBlank();

    if (regNumber == null || regNumber.isBlank()) {
      forwardWithError(request, response, "Registration number is required.",
          originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
      return;
    }

    if (fullName == null || fullName.isBlank()) {
      forwardWithError(request, response, "Full name is required.",
          originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
      return;
    }

    if (!FULL_NAME_PATTERN.matcher(fullName).matches()) {
      forwardWithError(request, response, "Full name cannot contain numbers.",
          originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
      return;
    }

    if (gender == null || gender.isBlank()) {
      forwardWithError(request, response, "Gender is required.",
          originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
      return;
    }

    if (faculty == null || faculty.isBlank()) {
      forwardWithError(request, response, "Faculty is required.",
          originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
      return;
    }

    if (contact == null || contact.isBlank()) {
      forwardWithError(request, response, "Contact information is required.",
          originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
      return;
    }

    LocalDate dob;
    try {
      dob = LocalDate.parse(dobRaw);
    } catch (DateTimeParseException | NullPointerException e) {
      forwardWithError(request, response, "A valid date of birth is required.",
          originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
      return;
    }

    if (dob.isAfter(LocalDate.now())) {
      forwardWithError(request, response, "Date of birth cannot be in the future.",
          originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
      return;
    }

    Student student = new Student();
    String[] nameParts = fullName.trim().split("\\s+", 2);
    student.setRegNumber(Integer.parseInt(regNumber));
    student.setFirstName(nameParts[0]);
    student.setLastName(nameParts.length > 1 ? nameParts[1] : "");
    student.setDob(dob);
    student.setFaculty(faculty);
    student.setEmail("");
    student.setPhone("");
    student.setAddress("");
    student.setEmergencyContact("");

    try {
      if (isCreate) {
        if (studentService.getStudentByRegNumber(Integer.parseInt(regNumber)).isPresent()) {
          forwardWithError(request, response, "Registration number already exists.",
              originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
          return;
        }
        studentService.createStudent(student);
        ServletRequestUtils.redirectWithMessage(request, response, "/students", "success",
            "Student registered successfully.");
        return;
      }

      if (!originalRegNumber.equals(regNumber)) {
        forwardWithError(request, response, "Registration number cannot be changed.",
            originalRegNumber, originalRegNumber, fullName, dobRaw, gender, faculty, contact);
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
    String regNumber = ServletRequestUtils.trim(request.getParameter("regNumber"));
    if (regNumber == null || regNumber.isBlank()) {
      ServletRequestUtils.redirectWithMessage(request, response, "/students", "error",
          "Registration number is required to delete.");
      return;
    }

    try {
      studentService.deleteStudent(Integer.parseInt(regNumber));
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
      String regNumber,
      String fullName,
      String dobRaw,
      String gender,
      String faculty,
      String contact) throws ServletException, IOException {
    Student student = new Student();
    String[] nameParts = fullName.trim().split("\\s+", 2);
    student.setRegNumber(Integer.parseInt(regNumber));
    student.setFirstName(nameParts[0]);
    student.setLastName(nameParts.length > 1 ? nameParts[1] : "");
    student.setFaculty(faculty);

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
