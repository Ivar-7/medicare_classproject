package com.medicare.features.users.dao;

import com.medicare.models.User;
import com.medicare.shared.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    private User.Role parseRole(String dbRole) {
        if (dbRole == null) {
            return User.Role.Receptionist;
        }
        if ("Lab Technician".equalsIgnoreCase(dbRole)) {
            return User.Role.Technician;
        }
        return User.Role.valueOf(dbRole);
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
            rs.getDate("date_of_employment") != null ? rs.getDate("date_of_employment").toLocalDate() : null,
            rs.getDate("created_at") != null ? rs.getDate("created_at").toLocalDate() : null,
            rs.getDate("updated_at") != null ? rs.getDate("updated_at").toLocalDate() : null
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
            ps.setDate(8, user.getDateOfEmployment() != null ? java.sql.Date.valueOf(user.getDateOfEmployment()) : null);
            ps.setDate(9, user.getCreatedAt() != null ? java.sql.Date.valueOf(user.getCreatedAt()) : null);
            ps.setDate(10, user.getUpdatedAt() != null ? java.sql.Date.valueOf(user.getUpdatedAt()) : null);
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
            ps.setDate(7, user.getDateOfEmployment() != null ? java.sql.Date.valueOf(user.getDateOfEmployment()) : null);
            ps.setDate(8, java.sql.Date.valueOf(LocalDate.now()));
            ps.setInt(9, user.getUserId());
            ps.executeUpdate();
        }
    }

    public void updatePassword(int userId, String newHashedPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash=?, updated_at=? WHERE user_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
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
