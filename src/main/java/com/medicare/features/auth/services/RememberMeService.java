package com.medicare.features.auth.services;

import com.medicare.features.auth.dao.AuthTokenDAO;
import com.medicare.models.User;
import com.medicare.shared.utils.PasswordUtils;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

public class RememberMeService {

    public static final String COOKIE_NAME = "medicare_remember";
    public static final int DEFAULT_DAYS = 30;

    private final SecureRandom secureRandom = new SecureRandom();
    private final AuthTokenDAO tokenDAO = new AuthTokenDAO();

    public String issueToken(int userId) throws SQLException {
        String rawToken = generateToken();
        String tokenHash = PasswordUtils.hash(rawToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(DEFAULT_DAYS);

        tokenDAO.deleteExpired(LocalDateTime.now());
        tokenDAO.createToken(userId, tokenHash, expiresAt);
        return rawToken;
    }

    public Optional<User> resolveUserFromToken(String rawToken) throws SQLException {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }

        String tokenHash = PasswordUtils.hash(rawToken.trim());
        tokenDAO.deleteExpired(LocalDateTime.now());
        return tokenDAO.findValidUserByTokenHash(tokenHash, LocalDateTime.now());
    }

    public void revokeToken(String rawToken) throws SQLException {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        String tokenHash = PasswordUtils.hash(rawToken.trim());
        tokenDAO.deleteByTokenHash(tokenHash);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
