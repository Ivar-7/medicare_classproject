package com.medicare.features.home.servlets;

import com.medicare.models.User;
import com.medicare.features.students.services.StudentService;
import com.medicare.features.users.services.UserService;
import com.medicare.features.visits.services.MedicalVisitService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet({"/home", "/dashboard"})
public class HomeServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(HomeServlet.class.getName());

    private final StudentService       studentService = new StudentService();
    private final MedicalVisitService  visitService   = new MedicalVisitService();
    private final UserService          userService    = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String viewPath;
        try {
            switch (currentUser.getRole()) {
                case Doctor:
                    loadDoctorDashboard(request, currentUser.getUserId());
                    viewPath = "/WEB-INF/views/dashboard/doctor.jsp";
                    break;
                case Receptionist:
                    loadReceptionistDashboard(request);
                    viewPath = "/WEB-INF/views/dashboard/receptionist.jsp";
                    break;
                case Technician:
                    loadTechnicianDashboard(request);
                    viewPath = "/WEB-INF/views/dashboard/technician.jsp";
                    break;
                case Admin:
                default:
                    loadAdminDashboard(request);
                    viewPath = "/WEB-INF/views/home.jsp";
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load dashboard stats", e);
            request.setAttribute("error", "Unable to load dashboard. Please try again.");
            viewPath = "/WEB-INF/views/error/500.jsp";
        }

        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void loadAdminDashboard(HttpServletRequest request) throws Exception {
        request.setAttribute("totalStudents", studentService.countStudents());
        request.setAttribute("totalStaff", userService.countUsers());
        request.setAttribute("todayVisits", visitService.countTodayVisits());
        request.setAttribute("totalVisits", visitService.countAllVisits());
        request.setAttribute("recentVisits", visitService.getRecentVisits(5));
    }

    private void loadReceptionistDashboard(HttpServletRequest request) throws Exception {
        request.setAttribute("studentCount", studentService.countStudents());
        request.setAttribute("visitCount", visitService.countAllVisits());
        request.setAttribute("recentVisits", visitService.getRecentVisits(10));
        request.setAttribute("receptionistUsers", userService.getReceptionists());
    }

    private void loadDoctorDashboard(HttpServletRequest request, int doctorId) throws Exception {
        request.setAttribute("pendingVisits", visitService.getPendingVisitsByDoctor(doctorId));
        request.setAttribute("recentVisits", visitService.getVisitsByDoctor(doctorId));
        request.setAttribute("pendingVisitsCount", visitService.countPendingVisitsByDoctor(doctorId));
        request.setAttribute("totalVisitsCount", visitService.countVisitsByDoctor(doctorId));
        request.setAttribute("doctorUsers", userService.getDoctors());
    }

    private void loadTechnicianDashboard(HttpServletRequest request) throws Exception {
        request.setAttribute("studentCount", studentService.countStudents());
        request.setAttribute("recentStudents", studentService.getRecentStudents(10));
        request.setAttribute("notesCount", 0);
        request.setAttribute("technicianUsers", userService.getTechnicians());
    }
}
