package com.medicare.features.audit.servlets;

import com.medicare.features.audit.services.AuditLogService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/audit")
public class AuditLogServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AuditLogServlet.class.getName());

    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("logs", auditLogService.getAllLogs());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AuditLogServlet GET error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/audit/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // implement clear-logs action (Admin only)
        response.sendRedirect(request.getContextPath() + "/audit");
    }
}
