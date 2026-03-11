package com.medicare.features.audit.services;

import com.medicare.features.audit.dao.AuditLogDAO;
import com.medicare.models.AuditLog;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class AuditLogService {

    private static final Logger logger = Logger.getLogger(AuditLogService.class.getName());

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public List<AuditLog> getAllLogs() throws SQLException {
        return auditLogDAO.findAll();
    }

    public List<AuditLog> getRecentLogs(int limit) throws SQLException {
        return auditLogDAO.findRecent(limit);
    }

    /** Non-throwing convenience method — call from servlets without try/catch. */
    public void logAction(int userId, String action, String ipAddress) {
        try {
            auditLogDAO.log(userId, action, ipAddress);
        } catch (SQLException e) {
            logger.warning("Failed to write audit log: " + e.getMessage());
        }
    }

    public void clearLogs() throws SQLException {
        auditLogDAO.deleteAll();
    }
}
