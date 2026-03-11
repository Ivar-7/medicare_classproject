package com.medicare.shared.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static String jdbcUrl;

    public static void initialize(String appDataPath) {
        try {
            Files.createDirectories(Paths.get(appDataPath));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create database directory: " + appDataPath, e);
        }
        jdbcUrl = "jdbc:sqlite:" + appDataPath + "/medicare.db";
    }

    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) {
            throw new IllegalStateException("Database not initialized. AppContextListener may not have run.");
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found on classpath", e);
        }
        return DriverManager.getConnection(jdbcUrl);
    }
}
