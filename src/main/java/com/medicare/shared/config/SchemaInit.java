package com.medicare.shared.config;

import com.medicare.shared.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SchemaInit {

    private static final Logger logger = Logger.getLogger(SchemaInit.class.getName());

    public static void createTables() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  user_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username  TEXT    NOT NULL UNIQUE," +
                "  password  TEXT    NOT NULL," +
                "  full_name TEXT    NOT NULL," +
                "  role      TEXT    NOT NULL CHECK(role IN ('Admin','Doctor','Receptionist'))" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS students (" +
                "  reg_number TEXT PRIMARY KEY," +
                "  full_name  TEXT NOT NULL," +
                "  dob        TEXT," +
                "  gender     TEXT," +
                "  faculty    TEXT," +
                "  contact    TEXT" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS medical_visits (" +
                "  visit_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  reg_number TEXT    NOT NULL," +
                "  doctor_id  INTEGER NOT NULL," +
                "  visit_date TEXT    NOT NULL," +
                "  symptoms   TEXT," +
                "  diagnosis  TEXT," +
                "  FOREIGN KEY (reg_number) REFERENCES students(reg_number)," +
                "  FOREIGN KEY (doctor_id)  REFERENCES users(user_id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS prescriptions (" +
                "  prescription_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  visit_id        INTEGER NOT NULL," +
                "  medicine_name   TEXT    NOT NULL," +
                "  dosage          TEXT," +
                "  duration        TEXT," +
                "  FOREIGN KEY (visit_id) REFERENCES medical_visits(visit_id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS treatment_notes (" +
                "  note_id        INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  visit_id       INTEGER NOT NULL," +
                "  clinical_notes TEXT," +
                "  follow_up_date TEXT," +
                "  FOREIGN KEY (visit_id) REFERENCES medical_visits(visit_id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS audit_logs (" +
                "  log_id     INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  user_id    INTEGER NOT NULL," +
                "  action     TEXT    NOT NULL," +
                "  timestamp  TEXT    NOT NULL," +
                "  ip_address TEXT," +
                "  FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")"
            );

            logger.info("Database schema verified/created successfully.");
        }
    }

    public static void seedDefaultAdmin() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {

            if (rs.next() && rs.getInt(1) == 0) {
                String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "admin");
                    ps.setString(2, PasswordUtils.hash("admin123"));
                    ps.setString(3, "System Administrator");
                    ps.setString(4, "Admin");
                    ps.executeUpdate();
                }
                logger.info("Default admin created — username: admin  password: admin123");
            }
        }
    }
}
