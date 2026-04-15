package com.medicare.shared.config;

import com.medicare.shared.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.logging.Logger;

public class SchemaInit {

  private static final Logger logger = Logger.getLogger(SchemaInit.class.getName());

  public static void createTables() throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement()) {

      stmt.execute("PRAGMA foreign_keys = ON");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS users (" +
              "  user_id             INTEGER PRIMARY KEY AUTOINCREMENT," +
              "  username            TEXT NOT NULL UNIQUE," +
              "  password_hash       TEXT NOT NULL," +
              "  first_name          TEXT NOT NULL," +
              "  last_name           TEXT NOT NULL," +
              "  role                TEXT NOT NULL CHECK(role IN ('Admin','Doctor','Receptionist','Technician'))," +
              "  email               TEXT UNIQUE," +
              "  phone               TEXT," +
              "  date_of_employment  TEXT," +
              "  created_at          TEXT," +
              "  updated_at          TEXT" +
              ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS students (" +
              "  reg_number          INTEGER PRIMARY KEY," +
              "  first_name          TEXT NOT NULL," +
              "  last_name           TEXT NOT NULL," +
              "  dob                 TEXT," +
              "  faculty             TEXT," +
              "  email               TEXT UNIQUE," +
              "  phone               TEXT," +
              "  address             TEXT," +
              "  emergency_contact   TEXT," +
              "  created_at          TEXT," +
              "  updated_at          TEXT" +
              ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS medical_visits (" +
              "  visit_id            INTEGER PRIMARY KEY AUTOINCREMENT," +
              "  reg_number          INTEGER NOT NULL," +
              "  doctor_id           INTEGER NOT NULL," +
              "  visit_date          TEXT NOT NULL," +
              "  symptoms            TEXT," +
              "  diagnosis           TEXT," +
              "  notes               TEXT," +
              "  status              TEXT NOT NULL CHECK(status IN ('Scheduled','Ongoing','Completed'))," +
              "  created_at          TEXT," +
              "  updated_at          TEXT," +
              "  FOREIGN KEY (reg_number) REFERENCES students(reg_number)," +
              "  FOREIGN KEY (doctor_id) REFERENCES users(user_id)" +
              ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS prescriptions (" +
              "  prescription_id     INTEGER PRIMARY KEY AUTOINCREMENT," +
              "  visit_id            INTEGER NOT NULL," +
              "  medicine_name       TEXT NOT NULL," +
              "  dosage              TEXT," +
              "  frequency           TEXT," +
              "  duration            TEXT," +
              "  instructions        TEXT," +
              "  prescribed_date     TEXT," +
              "  created_at          TEXT," +
              "  updated_at          TEXT," +
              "  FOREIGN KEY (visit_id) REFERENCES medical_visits(visit_id)" +
              ")");

            stmt.execute(
              "CREATE TABLE IF NOT EXISTS treatment_notes (" +
                "  note_id             INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  visit_id            INTEGER NOT NULL," +
                "  clinical_notes      TEXT NOT NULL," +
                "  follow_up_date      TEXT," +
                "  created_at          TEXT," +
                "  updated_at          TEXT," +
                "  FOREIGN KEY (visit_id) REFERENCES medical_visits(visit_id)" +
                ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS queue (" +
              "  queue_id            INTEGER PRIMARY KEY AUTOINCREMENT," +
              "  reg_number          INTEGER NOT NULL," +
              "  visit_id            INTEGER," +
              "  queue_date          TEXT NOT NULL," +
              "  priority_level      TEXT NOT NULL CHECK(priority_level IN ('Low','Medium','High','Emergency'))," +
              "  status              TEXT NOT NULL CHECK(status IN ('Waiting','InConsultation','Completed','Cancelled'))," +
              "  position_number     INTEGER," +
              "  created_at          TEXT," +
              "  updated_at          TEXT," +
              "  FOREIGN KEY (reg_number) REFERENCES students(reg_number)," +
              "  FOREIGN KEY (visit_id) REFERENCES medical_visits(visit_id)" +
              ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS lab_requests (" +
              "  request_id          INTEGER PRIMARY KEY AUTOINCREMENT," +
              "  visit_id            INTEGER NOT NULL," +
              "  requested_by        INTEGER," +
              "  test_name           TEXT NOT NULL," +
              "  test_description    TEXT," +
              "  request_date        TEXT NOT NULL," +
              "  status              TEXT NOT NULL CHECK(status IN ('Pending','InProgress','Completed','Cancelled'))," +
              "  priority            TEXT NOT NULL CHECK(priority IN ('Routine','Urgent'))," +
              "  created_at          TEXT," +
              "  updated_at          TEXT," +
              "  FOREIGN KEY (visit_id) REFERENCES medical_visits(visit_id)," +
              "  FOREIGN KEY (requested_by) REFERENCES users(user_id)" +
              ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS lab_results (" +
              "  result_id           INTEGER PRIMARY KEY AUTOINCREMENT," +
              "  patient_id          INTEGER," +
              "  technician_id       INTEGER NOT NULL," +
              "  request_id          INTEGER NOT NULL," +
              "  result_details      TEXT," +
              "  result_status       TEXT," +
              "  result_value        TEXT," +
              "  remarks             TEXT," +
              "  created_at          TEXT," +
              "  updated_at          TEXT," +
              "  FOREIGN KEY (patient_id) REFERENCES students(reg_number)," +
              "  FOREIGN KEY (technician_id) REFERENCES users(user_id)," +
              "  FOREIGN KEY (request_id) REFERENCES lab_requests(request_id)" +
              ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS auth_tokens (" +
              "  token_id            INTEGER PRIMARY KEY AUTOINCREMENT," +
              "  user_id             INTEGER NOT NULL," +
              "  token_hash          TEXT NOT NULL UNIQUE," +
              "  issued_at           TEXT," +
              "  expires_at          TEXT NOT NULL," +
              "  created_at          TEXT," +
              "  updated_at          TEXT," +
              "  FOREIGN KEY (user_id) REFERENCES users(user_id)" +
              ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS audit_logs (" +
              "  log_id              INTEGER PRIMARY KEY AUTOINCREMENT," +
              "  user_id             INTEGER," +
              "  action              TEXT NOT NULL," +
              "  timestamp           TEXT NOT NULL," +
              "  ip_address          TEXT," +
              "  FOREIGN KEY (user_id) REFERENCES users(user_id)" +
              ")");

      logger.info("Database schema verified/created successfully.");
    }
  }

  public static void seedDefaultAdmin() throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {

      if (rs.next() && rs.getInt(1) == 0) {
        String sql = "INSERT INTO users (username, password_hash, first_name, last_name, role, email, phone, date_of_employment, created_at, updated_at) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
          String today = LocalDate.now().toString();
          ps.setString(1, "admin");
          ps.setString(2, PasswordUtils.hash("admin123"));
          ps.setString(3, "System");
          ps.setString(4, "Administrator");
          ps.setString(5, "Admin");
          ps.setString(6, "admin@medicare.local");
          ps.setString(7, "");
          ps.setNull(8, java.sql.Types.VARCHAR);
          ps.setString(9, today);
          ps.setString(10, today);
          ps.executeUpdate();
        }
        logger.info("Default admin created — username: admin  password: admin123");
      }
    }
  }
}
