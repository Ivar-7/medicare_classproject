package com.medicare.features.prescriptions.servlets;

import com.medicare.features.prescriptions.services.PrescriptionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/prescriptions", "/prescriptions/*"})
public class PrescriptionServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PrescriptionServlet.class.getName());

    private final PrescriptionService prescriptionService = new PrescriptionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                request.setAttribute("prescriptions", prescriptionService.getAllPrescriptions());
                request.getRequestDispatcher("/WEB-INF/views/prescriptions/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                request.getRequestDispatcher("/WEB-INF/views/prescriptions/form.jsp").forward(request, response);
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                prescriptionService.getPrescriptionById(id)
                                   .ifPresent(p -> request.setAttribute("prescription", p));
                request.getRequestDispatcher("/WEB-INF/views/prescriptions/form.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "PrescriptionServlet GET error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // implement create / update / delete prescription logic
        response.sendRedirect(request.getContextPath() + "/prescriptions");
    }
}
