package com.medicare.features.auth.dao;

import com.medicare.models.User;
import com.medicare.shared.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AuthTokenDAO {

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("full_name"),
            User.Role.valueOf(rs.getString("role"))
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
