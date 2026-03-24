package com.medicare.features.home.servlets;

import com.medicare.models.User;
import com.medicare.features.students.services.StudentService;
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

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DashboardServlet.class.getName());
    private final StudentService studentService = new StudentService();
    private final MedicalVisitService visitService = new MedicalVisitService();

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
                default: // Technician or other roles
                    loadTechnicianDashboard(request);
                    viewPath = "/WEB-INF/views/dashboard/technician.jsp";
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading dashboard data", e);
            request.setAttribute("error", "Unable to load dashboard. Please try again.");
            viewPath = "/WEB-INF/views/error/500.jsp";
        }

        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void loadReceptionistDashboard(HttpServletRequest request) throws Exception {
        request.setAttribute("studentCount", studentService.countStudents());
        request.setAttribute("visitCount", visitService.countAllVisits());
        request.setAttribute("recentVisits", visitService.getRecentVisits(10));
    }

    private void loadDoctorDashboard(HttpServletRequest request, int doctorId) throws Exception {
        request.setAttribute("pendingVisits", visitService.getVisitsByDoctor(doctorId));
        request.setAttribute("recentVisits", visitService.getVisitsByDoctor(doctorId));
        request.setAttribute("pendingVisitsCount", visitService.countVisitsByDoctor(doctorId));
        request.setAttribute("totalVisitsCount", visitService.countVisitsByDoctor(doctorId));
    }

    private void loadTechnicianDashboard(HttpServletRequest request) throws Exception {
        request.setAttribute("studentCount", studentService.countStudents());
        request.setAttribute("recentStudents", studentService.getAllStudents());
        request.setAttribute("notesCount", 0); // Could track notes count if needed
    }
}
