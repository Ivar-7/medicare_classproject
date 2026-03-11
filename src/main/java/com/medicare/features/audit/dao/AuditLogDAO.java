package com.medicare.features.audit.dao;

import com.medicare.models.AuditLog;
import com.medicare.shared.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog(
            rs.getInt("log_id"),
            rs.getInt("user_id"),
            rs.getString("action"),
            LocalDateTime.parse(rs.getString("timestamp")),
            rs.getString("ip_address")
        );
        try { log.setUsername(rs.getString("username")); } catch (SQLException ignored) { }
        return log;
    }

    public List<AuditLog> findAll() throws SQLException {
        String sql =
            "SELECT a.*, u.username FROM audit_logs a " +
            "LEFT JOIN users u ON a.user_id = u.user_id " +
            "ORDER BY a.timestamp DESC";
        List<AuditLog> logs = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) logs.add(mapRow(rs));
        }
        return logs;
    }

    public List<AuditLog> findRecent(int limit) throws SQLException {
        String sql =
            "SELECT a.*, u.username FROM audit_logs a " +
            "LEFT JOIN users u ON a.user_id = u.user_id " +
            "ORDER BY a.timestamp DESC LIMIT ?";
        List<AuditLog> logs = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) logs.add(mapRow(rs));
            }
        }
        return logs;
    }

    public void log(int userId, String action, String ipAddress) throws SQLException {
        String sql = "INSERT INTO audit_logs (user_id, action, timestamp, ip_address) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    userId);
            ps.setString(2, action);
            ps.setString(3, LocalDateTime.now().toString());
            ps.setString(4, ipAddress);
            ps.executeUpdate();
        }
    }

    public void deleteAll() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM audit_logs");
        }
    }
}
