package com.medicare.features.students.servlets;

import com.medicare.features.students.services.StudentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String query = request.getParameter("q");
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
                studentService.getStudentByRegNumber(regNumber)
                              .ifPresent(s -> request.setAttribute("student", s));
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
        // implement create / update / delete student logic
        response.sendRedirect(request.getContextPath() + "/students");
    }
}
