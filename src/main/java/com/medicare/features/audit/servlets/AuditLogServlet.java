package com.medicare.features.audit.servlets;

import com.medicare.features.audit.services.AuditLogService;
import com.medicare.models.User;
import com.medicare.shared.utils.ServletRequestUtils;

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
        ServletRequestUtils.exposeAlertsFromQuery(request);
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
        String action = ServletRequestUtils.trim(request.getParameter("action"));
        if (!"clear".equalsIgnoreCase(action)) {
            ServletRequestUtils.redirectWithMessage(request, response, "/audit", "warning", "Unsupported action.");
            return;
        }

        User currentUser = ServletRequestUtils.getCurrentUser(request);

        if (currentUser == null || currentUser.getRole() != User.Role.Admin) {
            ServletRequestUtils.redirectWithMessage(request, response, "/audit", "error",
                                                   "Only Admin users can clear audit logs.");
            return;
        }

        try {
            auditLogService.clearLogs();
            auditLogService.logAction(currentUser.getUserId(), "Cleared all audit logs", request.getRemoteAddr());
            ServletRequestUtils.redirectWithMessage(request, response, "/audit", "success",
                                                   "Audit logs cleared successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AuditLogServlet POST clear error", e);
            ServletRequestUtils.redirectWithMessage(request, response, "/audit", "error",
                                                   "A system error occurred while clearing audit logs.");
        }
    }
}
