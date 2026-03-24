package com.medicare.features.audit.servlets;

import com.medicare.features.audit.services.AuditLogService;
import com.medicare.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/audit")
public class AuditLogServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AuditLogServlet.class.getName());

    private final AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        exposeAlertsFromQuery(request);
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
        String action = trim(request.getParameter("action"));
        if (!"clear".equalsIgnoreCase(action)) {
            response.sendRedirect(request.getContextPath() + "/audit?warning=" +
                                  encode("Unsupported action."));
            return;
        }

        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null || currentUser.getRole() != User.Role.Admin) {
            response.sendRedirect(request.getContextPath() + "/audit?error=" +
                                  encode("Only Admin users can clear audit logs."));
            return;
        }

        try {
            auditLogService.clearLogs();
            auditLogService.logAction(currentUser.getUserId(), "Cleared all audit logs", request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/audit?success=" +
                                  encode("Audit logs cleared successfully."));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AuditLogServlet POST clear error", e);
            response.sendRedirect(request.getContextPath() + "/audit?error=" +
                                  encode("A system error occurred while clearing audit logs."));
        }
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
