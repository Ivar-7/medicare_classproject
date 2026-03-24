package com.medicare.features.students.servlets;

import com.medicare.features.students.services.StudentService;
import com.medicare.models.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/students", "/students/*"})
public class StudentServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(StudentServlet.class.getName());

    private final StudentService studentService = new StudentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        exposeAlertsFromQuery(request);
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String query = trim(request.getParameter("q"));
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
                Student student = studentService.getStudentByRegNumber(regNumber).orElse(null);
                if (student == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("student", student);
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
        String action = trim(request.getParameter("action"));
        if ("delete".equalsIgnoreCase(action)) {
            deleteStudent(request, response);
            return;
        }
        saveStudent(request, response);
    }

    private void saveStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String originalRegNumber = trim(request.getParameter("originalRegNumber"));
        String regNumber = trim(request.getParameter("regNumber"));
        String fullName = trim(request.getParameter("fullName"));
        String dobRaw = trim(request.getParameter("dob"));
        String gender = trim(request.getParameter("gender"));
        String faculty = trim(request.getParameter("faculty"));
        String contact = trim(request.getParameter("contact"));

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

        Student student = new Student();
        student.setRegNumber(regNumber);
        student.setFullName(fullName);
        student.setDob(dob);
        student.setGender(gender);
        student.setFaculty(faculty);
        student.setContact(contact);

        try {
            if (isCreate) {
                if (studentService.getStudentByRegNumber(regNumber).isPresent()) {
                    forwardWithError(request, response, "Registration number already exists.",
                                     originalRegNumber, regNumber, fullName, dobRaw, gender, faculty, contact);
                    return;
                }
                studentService.createStudent(student);
                response.sendRedirect(request.getContextPath() + "/students?success=" +
                                      encode("Student registered successfully."));
                return;
            }

            if (!originalRegNumber.equals(regNumber)) {
                forwardWithError(request, response, "Registration number cannot be changed.",
                                 originalRegNumber, originalRegNumber, fullName, dobRaw, gender, faculty, contact);
                return;
            }

            studentService.updateStudent(student);
            response.sendRedirect(request.getContextPath() + "/students?success=" +
                                  encode("Student updated successfully."));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "StudentServlet POST save error", e);
            response.sendRedirect(request.getContextPath() + "/students?error=" +
                                  encode("A system error occurred while saving the student."));
        }
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String regNumber = trim(request.getParameter("regNumber"));
        if (regNumber == null || regNumber.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/students?error=" +
                                  encode("Registration number is required to delete."));
            return;
        }

        try {
            studentService.deleteStudent(regNumber);
            response.sendRedirect(request.getContextPath() + "/students?success=" +
                                  encode("Student deleted successfully."));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "StudentServlet POST delete error", e);
            response.sendRedirect(request.getContextPath() + "/students?error=" +
                                  encode("A system error occurred while deleting the student."));
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
        student.setRegNumber(regNumber);
        student.setFullName(fullName);
        student.setGender(gender);
        student.setFaculty(faculty);
        student.setContact(contact);

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

    private void exposeAlertsFromQuery(HttpServletRequest request) {
        String success = trim(request.getParameter("success"));
        String error = trim(request.getParameter("error"));
        String warning = trim(request.getParameter("warning"));

        if (success != null && !success.isBlank()) {
            request.setAttribute("success", success);
        }
        if (error != null && !error.isBlank()) {
            request.setAttribute("error", error);
        }
        if (warning != null && !warning.isBlank()) {
            request.setAttribute("warning", warning);
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
