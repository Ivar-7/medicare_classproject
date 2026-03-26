package com.medicare.features.history.servlets;

import com.medicare.features.history.services.MedicalHistoryService;
import com.medicare.models.StudentMedicalHistory;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/medical-history")
public class MedicalHistoryServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(MedicalHistoryServlet.class.getName());

    private final MedicalHistoryService medicalHistoryService = new MedicalHistoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String regNumber = ServletRequestUtils.trim(request.getParameter("regNumber"));
        boolean includeDiseases = "true".equalsIgnoreCase(
            ServletRequestUtils.trim(request.getParameter("includeDiseases")));

        request.setAttribute("includeDiseases", includeDiseases);

        if (regNumber == null || regNumber.isBlank()) {
            request.getRequestDispatcher("/WEB-INF/views/history/print.jsp").forward(request, response);
            return;
        }

        request.setAttribute("regNumber", regNumber);

        try {
            Optional<StudentMedicalHistory> history = medicalHistoryService.getStudentHistory(regNumber, includeDiseases);
            if (history.isEmpty()) {
                request.setAttribute("error", "Student ID not found.");
                request.getRequestDispatcher("/WEB-INF/views/history/print.jsp").forward(request, response);
                return;
            }

            request.setAttribute("history", history.get());
            request.getRequestDispatcher("/WEB-INF/views/history/print.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "MedicalHistoryServlet GET error", e);
            request.setAttribute("error", "Failed to load medical history. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/history/print.jsp").forward(request, response);
        }
    }
}
