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
    String sql = "INSERT INTO students (reg_number, first_name, last_name, dob, faculty, email, phone, address, emergency_contact, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, student.getRegNumber());
      ps.setString(2, student.getFirstName());
      ps.setString(3, student.getLastName());
      ps.setString(4, student.getDob() != null ? student.getDob().toString() : null);
      ps.setString(5, student.getFaculty());
      ps.setString(6, student.getEmail());
      ps.setString(7, student.getPhone());
      ps.setString(8, student.getAddress());
      ps.setString(9, student.getEmergencyContact());
      ps.setString(10, LocalDate.now().toString());
      ps.setString(11, LocalDate.now().toString());
      ps.executeUpdate();
    }
  }

  public void update(Student student) throws SQLException {
    String sql = "UPDATE students SET first_name=?, last_name=?, dob=?, faculty=?, email=?, phone=?, address=?, emergency_contact=?, updated_at=? WHERE reg_number=?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, student.getFirstName());
      ps.setString(2, student.getLastName());
      ps.setString(3, student.getDob() != null ? student.getDob().toString() : null);
      ps.setString(4, student.getFaculty());
      ps.setString(5, student.getEmail());
      ps.setString(6, student.getPhone());
      ps.setString(7, student.getAddress());
      ps.setString(8, student.getEmergencyContact());
      ps.setString(9, LocalDate.now().toString());
      ps.setInt(10, student.getRegNumber());
      ps.executeUpdate();
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
