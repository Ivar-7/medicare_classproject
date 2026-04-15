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

      migrateLegacyColumns(conn);
      logger.info("Database schema verified/created successfully.");
    }
  }

  private static void migrateLegacyColumns(Connection conn) throws SQLException {
    ensureUsersColumns(conn);
    ensureStudentsColumns(conn);
    ensureMedicalVisitsColumns(conn);
    ensureQueueColumns(conn);
    ensurePrescriptionsColumns(conn);
    ensureLabRequestsColumns(conn);
    ensureLabResultsColumns(conn);
    ensureAuthTokensColumns(conn);
  }

  private static void ensureUsersColumns(Connection conn) throws SQLException {
    ensureColumn(conn, "users", "password_hash TEXT");
    ensureColumn(conn, "users", "first_name TEXT");
    ensureColumn(conn, "users", "last_name TEXT");
    ensureColumn(conn, "users", "email TEXT");
    ensureColumn(conn, "users", "phone TEXT");
    ensureColumn(conn, "users", "date_of_employment TEXT");
    ensureColumn(conn, "users", "created_at TEXT");
    ensureColumn(conn, "users", "updated_at TEXT");

    if (hasColumn(conn, "users", "password")) {
      executeUpdate(conn,
          "UPDATE users SET password_hash = password WHERE password_hash IS NULL OR password_hash = ''");
    }
    if (hasColumn(conn, "users", "full_name")) {
      executeUpdate(conn,
          "UPDATE users SET first_name = CASE " +
              "WHEN instr(full_name, ' ') > 0 THEN substr(full_name, 1, instr(full_name, ' ') - 1) " +
              "ELSE full_name END " +
              "WHERE first_name IS NULL OR first_name = ''");
      executeUpdate(conn,
          "UPDATE users SET last_name = CASE " +
              "WHEN instr(full_name, ' ') > 0 THEN substr(full_name, instr(full_name, ' ') + 1) " +
              "ELSE '' END " +
              "WHERE last_name IS NULL");
    }

    executeUpdate(conn, "UPDATE users SET created_at = DATE('now') WHERE created_at IS NULL OR created_at = ''");
    executeUpdate(conn, "UPDATE users SET updated_at = DATE('now') WHERE updated_at IS NULL OR updated_at = ''");
    executeUpdate(conn,
        "UPDATE users SET date_of_employment = DATE('now') WHERE date_of_employment IS NULL OR date_of_employment = ''");
  }

  private static void ensureStudentsColumns(Connection conn) throws SQLException {
    ensureColumn(conn, "students", "first_name TEXT");
    ensureColumn(conn, "students", "last_name TEXT");
    ensureColumn(conn, "students", "email TEXT");
    ensureColumn(conn, "students", "phone TEXT");
    ensureColumn(conn, "students", "address TEXT");
    ensureColumn(conn, "students", "emergency_contact TEXT");
    ensureColumn(conn, "students", "created_at TEXT");
    ensureColumn(conn, "students", "updated_at TEXT");

    if (hasColumn(conn, "students", "full_name")) {
      executeUpdate(conn,
          "UPDATE students SET first_name = CASE " +
              "WHEN instr(full_name, ' ') > 0 THEN substr(full_name, 1, instr(full_name, ' ') - 1) " +
              "ELSE full_name END " +
              "WHERE first_name IS NULL OR first_name = ''");
      executeUpdate(conn,
          "UPDATE students SET last_name = CASE " +
              "WHEN instr(full_name, ' ') > 0 THEN substr(full_name, instr(full_name, ' ') + 1) " +
              "ELSE '' END " +
              "WHERE last_name IS NULL");
    }
    if (hasColumn(conn, "students", "contact")) {
      executeUpdate(conn,
          "UPDATE students SET phone = contact WHERE (phone IS NULL OR phone = '') AND contact IS NOT NULL");
    }

    executeUpdate(conn,
        "UPDATE students SET created_at = DATE('now') WHERE created_at IS NULL OR created_at = ''");
    executeUpdate(conn,
        "UPDATE students SET updated_at = DATE('now') WHERE updated_at IS NULL OR updated_at = ''");
  }

  private static void ensureMedicalVisitsColumns(Connection conn) throws SQLException {
    ensureColumn(conn, "medical_visits", "notes TEXT");
    ensureColumn(conn, "medical_visits", "status TEXT");
    ensureColumn(conn, "medical_visits", "created_at TEXT");
    ensureColumn(conn, "medical_visits", "updated_at TEXT");

    if (hasColumn(conn, "medical_visits", "is_completed")) {
      executeUpdate(conn,
          "UPDATE medical_visits SET status = CASE " +
              "WHEN is_completed = 1 THEN 'Completed' ELSE 'Scheduled' END " +
              "WHERE status IS NULL OR status = ''");
    }

    executeUpdate(conn, "UPDATE medical_visits SET status = 'Scheduled' WHERE status IS NULL OR status = ''");
    executeUpdate(conn,
        "UPDATE medical_visits SET created_at = DATE('now') WHERE created_at IS NULL OR created_at = ''");
    executeUpdate(conn,
        "UPDATE medical_visits SET updated_at = DATE('now') WHERE updated_at IS NULL OR updated_at = ''");
  }

  private static void ensureQueueColumns(Connection conn) throws SQLException {
    ensureColumn(conn, "queue", "visit_id INTEGER");
    ensureColumn(conn, "queue", "queue_date TEXT");
    ensureColumn(conn, "queue", "position_number INTEGER");
    ensureColumn(conn, "queue", "created_at TEXT");
    ensureColumn(conn, "queue", "updated_at TEXT");

    if (hasColumn(conn, "queue", "visit_date")) {
      executeUpdate(conn,
          "UPDATE queue SET queue_date = visit_date WHERE queue_date IS NULL OR queue_date = ''");
    }

    executeUpdate(conn, "UPDATE queue SET queue_date = DATE('now') WHERE queue_date IS NULL OR queue_date = ''");
    executeUpdate(conn,
        "UPDATE queue SET position_number = queue_id WHERE position_number IS NULL OR position_number = 0");
    executeUpdate(conn, "UPDATE queue SET created_at = DATE('now') WHERE created_at IS NULL OR created_at = ''");
    executeUpdate(conn, "UPDATE queue SET updated_at = DATE('now') WHERE updated_at IS NULL OR updated_at = ''");
  }

  private static void ensurePrescriptionsColumns(Connection conn) throws SQLException {
    ensureColumn(conn, "prescriptions", "frequency TEXT");
    ensureColumn(conn, "prescriptions", "instructions TEXT");
    ensureColumn(conn, "prescriptions", "prescribed_date TEXT");
    ensureColumn(conn, "prescriptions", "created_at TEXT");
    ensureColumn(conn, "prescriptions", "updated_at TEXT");

    executeUpdate(conn,
        "UPDATE prescriptions SET prescribed_date = DATE('now') WHERE prescribed_date IS NULL OR prescribed_date = ''");
    executeUpdate(conn,
        "UPDATE prescriptions SET created_at = DATE('now') WHERE created_at IS NULL OR created_at = ''");
    executeUpdate(conn,
        "UPDATE prescriptions SET updated_at = DATE('now') WHERE updated_at IS NULL OR updated_at = ''");
  }

  private static void ensureLabRequestsColumns(Connection conn) throws SQLException {
    ensureColumn(conn, "lab_requests", "requested_by INTEGER");
    ensureColumn(conn, "lab_requests", "test_description TEXT");
    ensureColumn(conn, "lab_requests", "priority TEXT");
    ensureColumn(conn, "lab_requests", "created_at TEXT");
    ensureColumn(conn, "lab_requests", "updated_at TEXT");

    executeUpdate(conn, "UPDATE lab_requests SET priority = 'Routine' WHERE priority IS NULL OR priority = ''");
    executeUpdate(conn,
        "UPDATE lab_requests SET created_at = request_date WHERE created_at IS NULL OR created_at = ''");
    executeUpdate(conn,
        "UPDATE lab_requests SET updated_at = DATE('now') WHERE updated_at IS NULL OR updated_at = ''");
  }

  private static void ensureLabResultsColumns(Connection conn) throws SQLException {
    ensureColumn(conn, "lab_results", "patient_id INTEGER");
    ensureColumn(conn, "lab_results", "result_status TEXT");
    ensureColumn(conn, "lab_results", "result_value TEXT");
    ensureColumn(conn, "lab_results", "remarks TEXT");
    ensureColumn(conn, "lab_results", "created_at TEXT");
    ensureColumn(conn, "lab_results", "updated_at TEXT");

    if (hasColumn(conn, "lab_results", "result_date")) {
      executeUpdate(conn,
          "UPDATE lab_results SET created_at = result_date WHERE created_at IS NULL OR created_at = ''");
    }

    executeUpdate(conn,
        "UPDATE lab_results SET patient_id = (" +
            "SELECT v.reg_number FROM lab_requests r JOIN medical_visits v ON r.visit_id = v.visit_id " +
            "WHERE r.request_id = lab_results.request_id) WHERE patient_id IS NULL");
    executeUpdate(conn,
        "UPDATE lab_results SET result_status = 'Completed' WHERE result_status IS NULL OR result_status = ''");
    executeUpdate(conn,
        "UPDATE lab_results SET updated_at = DATE('now') WHERE updated_at IS NULL OR updated_at = ''");
    executeUpdate(conn,
        "UPDATE lab_results SET created_at = DATE('now') WHERE created_at IS NULL OR created_at = ''");
  }

  private static void ensureAuthTokensColumns(Connection conn) throws SQLException {
    ensureColumn(conn, "auth_tokens", "issued_at TEXT");
    ensureColumn(conn, "auth_tokens", "created_at TEXT");
    ensureColumn(conn, "auth_tokens", "updated_at TEXT");

    executeUpdate(conn,
        "UPDATE auth_tokens SET created_at = DATE('now') WHERE created_at IS NULL OR created_at = ''");
    executeUpdate(conn,
        "UPDATE auth_tokens SET updated_at = DATE('now') WHERE updated_at IS NULL OR updated_at = ''");
    executeUpdate(conn,
        "UPDATE auth_tokens SET issued_at = created_at WHERE issued_at IS NULL OR issued_at = ''");
  }

  private static boolean hasColumn(Connection conn, String table, String column) throws SQLException {
    String sql = "PRAGMA table_info(" + table + ")";
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        if (column.equalsIgnoreCase(rs.getString("name"))) {
          return true;
        }
      }
    }
    return false;
  }

  private static void ensureColumn(Connection conn, String table, String columnDefinition) throws SQLException {
    String columnName = columnDefinition.trim().split("\\s+")[0];
    if (hasColumn(conn, table, columnName)) {
      return;
    }
    try (Statement stmt = conn.createStatement()) {
      stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + columnDefinition);
    }
    logger.info("Schema migration applied: added " + table + "." + columnName);
  }

  private static void executeUpdate(Connection conn, String sql) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      stmt.executeUpdate(sql);
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
          ps.setString(8, today);
          ps.setString(9, today);
          ps.setString(10, today);
          ps.executeUpdate();
        }
        logger.info("Default admin created — username: admin  password: admin123");
      }
    }
  }
}
