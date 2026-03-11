package com.medicare.features.visits.servlets;

import com.medicare.features.visits.services.MedicalVisitService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/visits", "/visits/*"})
public class VisitServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(VisitServlet.class.getName());

    private final MedicalVisitService visitService = new MedicalVisitService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                request.setAttribute("visits", visitService.getAllVisits());
                request.getRequestDispatcher("/WEB-INF/views/visits/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                request.getRequestDispatcher("/WEB-INF/views/visits/form.jsp").forward(request, response);
            } else {
                int visitId = Integer.parseInt(pathInfo.substring(1));
                visitService.getVisitById(visitId)
                            .ifPresent(v -> request.setAttribute("visit", v));
                request.getRequestDispatcher("/WEB-INF/views/visits/form.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet GET error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // implement create / update / delete visit logic
        response.sendRedirect(request.getContextPath() + "/visits");
    }
}
