package com.medicare.features.users.dao;

import com.medicare.models.User;
import com.medicare.shared.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

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

    private String toDbDate(LocalDate date) {
        return date != null ? date.toString() : null;
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

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            parseRole(rs.getString("role")),
            rs.getString("email"),
            rs.getString("phone"),
            parseLocalDate(rs, "date_of_employment"),
            parseLocalDate(rs, "created_at"),
            parseLocalDate(rs, "updated_at")
        );
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY first_name, last_name";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) users.add(mapRow(rs));
        }
        return users;
    }

    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY first_name, last_name";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapRow(rs));
            }
        }
        return users;
    }

    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, first_name, last_name, role, email, phone, date_of_employment, created_at, updated_at) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPhone());
            ps.setString(8, toDbDate(user.getDateOfEmployment()));
            ps.setString(9, toDbDate(user.getCreatedAt()));
            ps.setString(10, toDbDate(user.getUpdatedAt()));
            ps.executeUpdate();
        }
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET username=?, first_name=?, last_name=?, role=?, email=?, phone=?, date_of_employment=?, updated_at=? WHERE user_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, toDbDate(user.getDateOfEmployment()));
            ps.setString(8, LocalDate.now().toString());
            ps.setInt(9, user.getUserId());
            ps.executeUpdate();
        }
    }

    public void updatePassword(int userId, String newHashedPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash=?, updated_at=? WHERE user_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setString(2, LocalDate.now().toString());
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
