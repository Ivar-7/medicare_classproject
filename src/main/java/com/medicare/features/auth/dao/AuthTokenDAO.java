package com.medicare.features.auth.dao;

import com.medicare.models.User;
import com.medicare.shared.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class AuthTokenDAO {

    private LocalDate parseLocalDate(ResultSet rs, String column) throws SQLException {
        String raw = rs.getString(column);
        if (raw == null) {
            return null;
        }
        raw = raw.trim();
        if (raw.isEmpty()) {
            return null;
        }

        // Accept full ISO date values directly.
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException ignored) {
            // Continue with tolerant parsing below.
        }

        // Accept ISO date-time by using only the date portion.
        if (raw.length() >= 10 && raw.charAt(4) == '-' && raw.charAt(7) == '-') {
            try {
                return LocalDate.parse(raw.substring(0, 10));
            } catch (DateTimeParseException ignored) {
                // Fall through to strict error below.
            }
        }

        throw new SQLException("Invalid date value in column '" + column + "': " + raw);
    }

    private User.Role parseRole(String dbRole) throws SQLException {
        if (dbRole == null || dbRole.isBlank()) {
            throw new SQLException("Invalid role value in column 'role': " + dbRole);
        }
        try {
            return User.Role.valueOf(dbRole);
        } catch (IllegalArgumentException ex) {
            throw new SQLException("Unsupported role value in column 'role': " + dbRole, ex);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        LocalDate dateOfEmployment = parseLocalDate(rs, "date_of_employment");
        LocalDate createdAt = parseLocalDate(rs, "created_at");
        LocalDate updatedAt = parseLocalDate(rs, "updated_at");

        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            parseRole(rs.getString("role")),
            rs.getString("email"),
            rs.getString("phone"),
            dateOfEmployment,
            createdAt,
            updatedAt
        );
    }

    public void createToken(int userId, String tokenHash, LocalDateTime expiresAt) throws SQLException {
        String sql = "INSERT INTO auth_tokens (user_id, token_hash, expires_at, created_at) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, tokenHash);
            ps.setString(3, expiresAt.toString());
            ps.setString(4, LocalDateTime.now().toString());
            ps.executeUpdate();
        }
    }

    public Optional<User> findValidUserByTokenHash(String tokenHash, LocalDateTime now) throws SQLException {
        String sql =
            "SELECT u.* " +
            "FROM auth_tokens t " +
            "JOIN users u ON u.user_id = t.user_id " +
            "WHERE t.token_hash = ? AND t.expires_at > ? " +
            "ORDER BY t.token_id DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            ps.setString(2, now.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapUser(rs)) : Optional.empty();
            }
        }
    }

    public void deleteByTokenHash(String tokenHash) throws SQLException {
        String sql = "DELETE FROM auth_tokens WHERE token_hash = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            ps.executeUpdate();
        }
    }

    public void deleteExpired(LocalDateTime now) throws SQLException {
        String sql = "DELETE FROM auth_tokens WHERE expires_at <= ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, now.toString());
            ps.executeUpdate();
        }
    }
}
