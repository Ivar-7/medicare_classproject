package com.medicare.features.home.servlets;

import com.medicare.features.students.services.StudentService;
import com.medicare.features.users.services.UserService;
import com.medicare.features.visits.services.MedicalVisitService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(HomeServlet.class.getName());

    private final StudentService       studentService = new StudentService();
    private final MedicalVisitService  visitService   = new MedicalVisitService();
    private final UserService          userService    = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("totalStudents", studentService.countStudents());
            request.setAttribute("totalStaff",    userService.countUsers());
            request.setAttribute("todayVisits",   visitService.countTodayVisits());
            request.setAttribute("totalVisits",   visitService.countAllVisits());
            request.setAttribute("recentVisits",  visitService.getRecentVisits(5));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load dashboard stats", e);
        }
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
