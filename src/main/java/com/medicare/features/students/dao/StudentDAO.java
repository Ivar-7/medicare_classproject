package com.medicare.features.students.dao;

import com.medicare.models.Student;
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

public class StudentDAO {

  private boolean hasColumn(Connection conn, String table, String column) throws SQLException {
    String sql = "PRAGMA table_info(" + table + ")";
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        if (column.equalsIgnoreCase(rs.getString("name"))) {
          return true;
        }
      }
      return false;
    }
  }

  private String buildFullName(String firstName, String lastName) {
    String first = firstName == null ? "" : firstName.trim();
    String last = lastName == null ? "" : lastName.trim();
    String fullName = (first + " " + last).trim();
    return fullName.isEmpty() ? "Unknown Student" : fullName;
  }

  private Student mapRow(ResultSet rs) throws SQLException {
    String dobStr = rs.getString("dob");
    return new Student(
        rs.getInt("reg_number"),
        rs.getString("first_name"),
        rs.getString("last_name"),
        dobStr != null && !dobStr.isEmpty() ? LocalDate.parse(dobStr) : null,
        rs.getString("faculty"),
        rs.getString("email"),
        rs.getString("phone"),
        rs.getString("address"),
        rs.getString("emergency_contact"),
        rs.getObject("created_at") != null ? LocalDate.parse(rs.getString("created_at")) : null,
        rs.getObject("updated_at") != null ? LocalDate.parse(rs.getString("updated_at")) : null);
  }

  public List<Student> findAll() throws SQLException {
    String sql = "SELECT * FROM students ORDER BY first_name, last_name";
    List<Student> students = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next())
        students.add(mapRow(rs));
    }
    return students;
  }

  public Optional<Student> findByRegNumber(int regNumber) throws SQLException {
    String sql = "SELECT * FROM students WHERE reg_number = ?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, regNumber);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
      }
    }
  }

  public boolean existsByRegNumber(int regNumber) throws SQLException {
    String sql = "SELECT 1 FROM students WHERE reg_number = ? LIMIT 1";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, regNumber);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  public boolean existsByEmail(String email) throws SQLException {
    String sql = "SELECT 1 FROM students WHERE email = ? LIMIT 1";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, email);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  public boolean existsByEmailForOtherStudent(String email, int regNumber) throws SQLException {
    String sql = "SELECT 1 FROM students WHERE email = ? AND reg_number <> ? LIMIT 1";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, email);
      ps.setInt(2, regNumber);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  public List<Student> search(String query) throws SQLException {
    String sql = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ? OR reg_number LIKE ? ORDER BY first_name, last_name";
    String term = "%" + query + "%";
    List<Student> students = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, term);
      ps.setString(2, term);
      ps.setString(3, term);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          students.add(mapRow(rs));
      }
    }
    return students;
  }

  public void save(Student student) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection()) {
      boolean hasLegacyFullName = hasColumn(conn, "students", "full_name");
      String sql = hasLegacyFullName
          ? "INSERT INTO students (reg_number, first_name, last_name, full_name, dob, faculty, email, phone, address, emergency_contact, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
          : "INSERT INTO students (reg_number, first_name, last_name, dob, faculty, email, phone, address, emergency_contact, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

      try (PreparedStatement ps = conn.prepareStatement(sql)) {
      int idx = 1;
      ps.setInt(idx++, student.getRegNumber());
      ps.setString(idx++, student.getFirstName());
      ps.setString(idx++, student.getLastName());
      if (hasLegacyFullName) {
        ps.setString(idx++, buildFullName(student.getFirstName(), student.getLastName()));
      }
      ps.setString(idx++, student.getDob() != null ? student.getDob().toString() : null);
      ps.setString(idx++, student.getFaculty());
      ps.setString(idx++, student.getEmail());
      ps.setString(idx++, student.getPhone());
      ps.setString(idx++, student.getAddress());
      ps.setString(idx++, student.getEmergencyContact());
      ps.setString(idx++, LocalDate.now().toString());
      ps.setString(idx, LocalDate.now().toString());
      ps.executeUpdate();
      }
    }
  }

  public void update(Student student) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection()) {
      boolean hasLegacyFullName = hasColumn(conn, "students", "full_name");
      String sql = hasLegacyFullName
          ? "UPDATE students SET first_name=?, last_name=?, full_name=?, dob=?, faculty=?, email=?, phone=?, address=?, emergency_contact=?, updated_at=? WHERE reg_number=?"
          : "UPDATE students SET first_name=?, last_name=?, dob=?, faculty=?, email=?, phone=?, address=?, emergency_contact=?, updated_at=? WHERE reg_number=?";

      try (PreparedStatement ps = conn.prepareStatement(sql)) {
      int idx = 1;
      ps.setString(idx++, student.getFirstName());
      ps.setString(idx++, student.getLastName());
      if (hasLegacyFullName) {
        ps.setString(idx++, buildFullName(student.getFirstName(), student.getLastName()));
      }
      ps.setString(idx++, student.getDob() != null ? student.getDob().toString() : null);
      ps.setString(idx++, student.getFaculty());
      ps.setString(idx++, student.getEmail());
      ps.setString(idx++, student.getPhone());
      ps.setString(idx++, student.getAddress());
      ps.setString(idx++, student.getEmergencyContact());
      ps.setString(idx++, LocalDate.now().toString());
      ps.setInt(idx, student.getRegNumber());
      ps.executeUpdate();
      }
    }
  }

  public void delete(int regNumber) throws SQLException {
    String sql = "DELETE FROM students WHERE reg_number=?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, regNumber);
      ps.executeUpdate();
    }
  }

  public int count() throws SQLException {
    String sql = "SELECT COUNT(*) FROM students";
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      return rs.next() ? rs.getInt(1) : 0;
    }
  }
}
