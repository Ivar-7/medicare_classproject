package com.medicare.features.prescriptions.servlets;

import com.medicare.features.prescriptions.services.PrescriptionService;
import com.medicare.models.Prescription;

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
            
            //DELETE LOGIC START
            } else if (pathInfo.startsWith("/delete/")) {

                int id = Integer.parseInt(pathInfo.substring(8));
                prescriptionService.deletePrescription(id);
                
                // send user back to the list after deleting
                response.sendRedirect(request.getContextPath() + "/prescriptions");
                return; 
            //DELETE LOGIC END

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
        
        String visitIdStr = request.getParameter("visitId");
        String medicineName = request.getParameter("medicineName");
        String dosage = request.getParameter("dosage");
        String duration = request.getParameter("duration");
        String pIdStr = request.getParameter("prescriptionId"); // For updates

        // Validation
        String errorMsg = null;
        if (visitIdStr == null || !visitIdStr.matches("\\d+")) {
            errorMsg = "Error: Inappropriate Visit ID. It must be a positive number.";
        } else if (medicineName == null || medicineName.trim().isEmpty()) {
            errorMsg = "Error: Medicine name cannot be empty.";
        } else if (dosage == null || dosage.trim().isEmpty() || duration == null || duration.trim().isEmpty()) {
            errorMsg = "Error: Dosage and Duration are required.";
        }

        if (errorMsg != null) {
            request.setAttribute("error", errorMsg);
            request.getRequestDispatcher("/WEB-INF/views/prescriptions/form.jsp").forward(request, response);
            return;
        }

        try {
            int visitId = Integer.parseInt(visitIdStr);
            int pId = (pIdStr != null && !pIdStr.isEmpty()) ? Integer.parseInt(pIdStr) : 0;
            
            Prescription p = new Prescription(pId, visitId, medicineName, dosage, duration);
            
            if (pId > 0) {
                prescriptionService.updatePrescription(p);
            } else {
                prescriptionService.createPrescription(p); 
            }
            
            response.sendRedirect(request.getContextPath() + "/prescriptions");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "PrescriptionServlet POST error", e);
            request.setAttribute("error", "System Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/prescriptions/form.jsp").forward(request, response);
        }
    }
}