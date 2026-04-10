package com.medicare.features.visits.dao;

import com.medicare.models.MedicalVisit;
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

public class MedicalVisitDAO {

  private static final String SELECT_WITH_JOINS = "SELECT v.*, (s.first_name || ' ' || s.last_name) AS student_name, (u.first_name || ' ' || u.last_name) AS doctor_name "
      +
      "FROM medical_visits v " +
      "LEFT JOIN students s ON v.reg_number = s.reg_number " +
      "LEFT JOIN users    u ON v.doctor_id  = u.user_id ";

  private MedicalVisit mapRow(ResultSet rs) throws SQLException {
    String visitDateRaw = rs.getString("visit_date");
    LocalDate visitDate = LocalDate.parse(visitDateRaw);

    MedicalVisit visit = new MedicalVisit(
        rs.getInt("visit_id"),
        rs.getInt("reg_number"),
        rs.getInt("doctor_id"),
        visitDate,
        rs.getString("symptoms"),
        rs.getString("diagnosis"),
        rs.getString("notes"),
        MedicalVisit.Status.valueOf(rs.getString("status")),
        rs.getObject("created_at") != null ? LocalDate.parse(rs.getString("created_at")) : null,
        rs.getObject("updated_at") != null ? LocalDate.parse(rs.getString("updated_at")) : null);
    try {
      visit.setStudentName(rs.getString("student_name"));
    } catch (SQLException ignored) {
    }
    try {
      visit.setDoctorName(rs.getString("doctor_name"));
    } catch (SQLException ignored) {
    }
    return visit;
  }

  // private LocalDate parseVisitDate(String value) {
  // if (value == null || value.isBlank()) {
  // return LocalDate.now();
  // }
  // try {
  // return LocalDate.parse(value);
  // } catch (Exception ignored) {
  // return LocalDate.now();
  // }
  // }

  public List<MedicalVisit> findAll() throws SQLException {
    String sql = SELECT_WITH_JOINS + "ORDER BY v.visit_date DESC";
    List<MedicalVisit> visits = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next())
        visits.add(mapRow(rs));
    }
    return visits;
  }

  public Optional<MedicalVisit> findById(int visitId) throws SQLException {
    String sql = SELECT_WITH_JOINS + "WHERE v.visit_id = ?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, visitId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
      }
    }
  }

  public List<MedicalVisit> findByStudent(int regNumber) throws SQLException {
    String sql = SELECT_WITH_JOINS + "WHERE v.reg_number = ? ORDER BY v.visit_date DESC";
    List<MedicalVisit> visits = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, regNumber);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          visits.add(mapRow(rs));
      }
    }
    return visits;
  }

  public List<MedicalVisit> findByDoctor(int doctorId) throws SQLException {
    String sql = SELECT_WITH_JOINS + "WHERE v.doctor_id = ? ORDER BY v.visit_date DESC";
    List<MedicalVisit> visits = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          visits.add(mapRow(rs));
      }
    }
    return visits;
  }

  public List<MedicalVisit> findPendingByDoctor(int doctorId) throws SQLException {
    String sql = SELECT_WITH_JOINS +
        "WHERE v.doctor_id = ? AND v.status != 'Completed' ORDER BY v.visit_date DESC";
    List<MedicalVisit> visits = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          visits.add(mapRow(rs));
      }
    }
    return visits;
  }

  public List<MedicalVisit> findRecent(int limit) throws SQLException {
    String sql = SELECT_WITH_JOINS + "ORDER BY v.visit_date DESC LIMIT ?";
    List<MedicalVisit> visits = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, limit);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          visits.add(mapRow(rs));
      }
    }
    return visits;
  }

  public int countToday() throws SQLException {
    String sql = "SELECT COUNT(*) FROM medical_visits WHERE DATE(visit_date) = DATE('now')";
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      return rs.next() ? rs.getInt(1) : 0;
    }
  }

  public int countTotal() throws SQLException {
    String sql = "SELECT COUNT(*) FROM medical_visits";
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      return rs.next() ? rs.getInt(1) : 0;
    }
  }

  public int countByDoctor(int doctorId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM medical_visits WHERE doctor_id = ?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    }
  }

  public int countPendingByDoctor(int doctorId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM medical_visits WHERE doctor_id = ? AND status != 'Completed'";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    }
  }

  public int save(MedicalVisit visit) throws SQLException {
    String sql = "INSERT INTO medical_visits (reg_number, doctor_id, visit_date, symptoms, diagnosis, notes, status, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?)";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, visit.getRegNumber());
      ps.setInt(2, visit.getDoctorId());
      ps.setString(3, visit.getVisitDate().toString());
      ps.setString(4, visit.getSymptoms());
      ps.setString(5, visit.getDiagnosis());
      ps.setString(6, visit.getNotes());
      ps.setString(7, visit.getStatus().toString());
      ps.setString(8, LocalDate.now().toString());
      ps.setString(9, LocalDate.now().toString());
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        return keys.next() ? keys.getInt(1) : -1;
      }
    }
  }

  public void update(MedicalVisit visit) throws SQLException {
    String sql = "UPDATE medical_visits SET reg_number=?, doctor_id=?, visit_date=?, symptoms=?, diagnosis=?, notes=?, status=?, updated_at=? WHERE visit_id=?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, visit.getRegNumber());
      ps.setInt(2, visit.getDoctorId());
      ps.setString(3, visit.getVisitDate().toString());
      ps.setString(4, visit.getSymptoms());
      ps.setString(5, visit.getDiagnosis());
      ps.setString(6, visit.getNotes());
      ps.setString(7, visit.getStatus().toString());
      ps.setString(8, LocalDate.now().toString());
      ps.setInt(9, visit.getVisitId());
      ps.executeUpdate();
    }
  }

  public void delete(int visitId) throws SQLException {
    String sql = "DELETE FROM medical_visits WHERE visit_id=?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, visitId);
      ps.executeUpdate();
    }
  }
}
