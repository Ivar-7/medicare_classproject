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
                "  role      TEXT    NOT NULL CHECK(role IN ('Admin','Doctor','Receptionist','Technician'))" +
                ")"
            );

            migrateUsersRoleConstraint(conn);

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
                "CREATE TABLE IF NOT EXISTS queue (" +
                "  queue_id       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  reg_number     TEXT    NOT NULL," +
                "  visit_date     TEXT    NOT NULL," +
                "  priority_level TEXT    NOT NULL CHECK(priority_level IN ('Low','Medium','High'))," +
                "  status         TEXT    NOT NULL CHECK(status IN ('Waiting','InConsultation','Completed'))," +
                "  FOREIGN KEY (reg_number) REFERENCES students(reg_number)" +
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
                "  is_completed INTEGER NOT NULL DEFAULT 0," +
                "  FOREIGN KEY (reg_number) REFERENCES students(reg_number)," +
                "  FOREIGN KEY (doctor_id)  REFERENCES users(user_id)" +
                ")"
            );

            ensureMedicalVisitsCompletedColumn(conn);

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS medical_history (" +
                "  history_id     INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  reg_number     TEXT    NOT NULL," +
                "  condition_name TEXT    NOT NULL," +
                "  diagnosis_date TEXT    NOT NULL," +
                "  doctor_id      INTEGER NOT NULL," +
                "  FOREIGN KEY (reg_number) REFERENCES students(reg_number)," +
                "  FOREIGN KEY (doctor_id)  REFERENCES users(user_id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS prescriptions (" +
                "  prescription_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  visit_id        INTEGER NOT NULL," +
                "  medicine_name   TEXT    NOT NULL," +
                "  diagnosis       TEXT," +
                "  dosage          TEXT," +
                "  duration        TEXT," +
                "  FOREIGN KEY (visit_id) REFERENCES medical_visits(visit_id)" +
                ")"
            );

            ensurePrescriptionsDiagnosisColumn(conn);

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS lab_requests (" +
                "  request_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  visit_id     INTEGER NOT NULL," +
                "  test_name    TEXT    NOT NULL," +
                "  request_date TEXT    NOT NULL," +
                "  status       TEXT    NOT NULL CHECK(status IN ('Pending','InProgress','Completed'))," +
                "  FOREIGN KEY (visit_id) REFERENCES medical_visits(visit_id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS lab_results (" +
                "  result_id       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  request_id      INTEGER NOT NULL," +
                "  technician_id   INTEGER NOT NULL," +
                "  result_details  TEXT," +
                "  result_date     TEXT    NOT NULL," +
                "  FOREIGN KEY (request_id)    REFERENCES lab_requests(request_id)," +
                "  FOREIGN KEY (technician_id) REFERENCES users(user_id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS auth_tokens (" +
                "  token_id    INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  user_id     INTEGER NOT NULL," +
                "  token_hash  TEXT    NOT NULL UNIQUE," +
                "  expires_at  TEXT    NOT NULL," +
                "  FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")"
            );

            logger.info("Database schema verified/created successfully.");
        }
    }

    private static void migrateUsersRoleConstraint(Connection conn) throws SQLException {
        // Check if the existing users table still has the old CHECK constraint (missing 'Technician')
        try (ResultSet rs = conn.createStatement().executeQuery("SELECT sql FROM sqlite_master WHERE type='table' AND name='users'")) {
            if (!rs.next()) return;
            String tableSql = rs.getString("sql");
            if (tableSql == null || tableSql.contains("Technician")) return; // already up to date
        }

        logger.info("Migrating users table to add Technician role support...");
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF");
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users_new (" +
                "  user_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username  TEXT    NOT NULL UNIQUE," +
                "  password  TEXT    NOT NULL," +
                "  full_name TEXT    NOT NULL," +
                "  role      TEXT    NOT NULL CHECK(role IN ('Admin','Doctor','Receptionist','Technician'))" +
                ")"
            );
            stmt.execute("INSERT INTO users_new SELECT user_id, username, password, full_name, role FROM users");
            stmt.execute("DROP TABLE users");
            stmt.execute("ALTER TABLE users_new RENAME TO users");
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        logger.info("Users table migration complete.");
    }

    private static void ensurePrescriptionsDiagnosisColumn(Connection conn) throws SQLException {
        boolean hasDiagnosis = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(prescriptions)")) {
            while (rs.next()) {
                if ("diagnosis".equalsIgnoreCase(rs.getString("name"))) {
                    hasDiagnosis = true;
                    break;
                }
            }
        }

        if (!hasDiagnosis) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE prescriptions ADD COLUMN diagnosis TEXT");
            }
            logger.info("Schema migration applied: added prescriptions.diagnosis column");
        }
    }

    private static void ensureMedicalVisitsCompletedColumn(Connection conn) throws SQLException {
        boolean hasCompleted = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(medical_visits)")) {
            while (rs.next()) {
                if ("is_completed".equalsIgnoreCase(rs.getString("name"))) {
                    hasCompleted = true;
                    break;
                }
            }
        }

        if (!hasCompleted) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE medical_visits ADD COLUMN is_completed INTEGER NOT NULL DEFAULT 0");
            }
            logger.info("Schema migration applied: added medical_visits.is_completed column");
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
