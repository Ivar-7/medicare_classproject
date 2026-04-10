package com.medicare.features.prescriptions.dao;

import com.medicare.models.Prescription;
import com.medicare.shared.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public class PrescriptionDAO {

  private static final String SELECT_WITH_VISIT = "SELECT p.*, v.reg_number AS student_reg_number " +
      "FROM prescriptions p " +
      "LEFT JOIN medical_visits v ON p.visit_id = v.visit_id ";

  private Prescription mapRow(ResultSet rs) throws SQLException {
    String prescribedDateStr = rs.getString("prescribed_date");
    Prescription prescription = new Prescription(
        rs.getInt("prescription_id"),
        rs.getInt("visit_id"),
        rs.getString("medicine_name"),
        rs.getString("dosage"),
        rs.getString("frequency"),
        rs.getString("duration"),
        rs.getString("instructions"),
        prescribedDateStr != null && !prescribedDateStr.isEmpty() ? LocalDate.parse(prescribedDateStr) : null,
        rs.getObject("created_at") != null ? LocalDate.parse(rs.getString("created_at")) : null,
        rs.getObject("updated_at") != null ? LocalDate.parse(rs.getString("updated_at")) : null);
    return prescription;
  }

  public List<Prescription> findAll() throws SQLException {
    String sql = SELECT_WITH_VISIT + "ORDER BY p.prescription_id DESC";
    List<Prescription> list = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next())
        list.add(mapRow(rs));
    }
    return list;
  }

  public Optional<Prescription> findById(int id) throws SQLException {
    String sql = SELECT_WITH_VISIT + "WHERE p.prescription_id = ?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
      }
    }
  }

  public List<Prescription> findByVisit(int visitId) throws SQLException {
    String sql = SELECT_WITH_VISIT + "WHERE p.visit_id = ? ORDER BY p.prescription_id";
    List<Prescription> list = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, visitId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          list.add(mapRow(rs));
      }
    }
    return list;
  }

  public List<Prescription> findByDoctor(int doctorId) throws SQLException {
    String sql = SELECT_WITH_VISIT +
        "WHERE v.doctor_id = ? ORDER BY p.prescription_id DESC";
    List<Prescription> list = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          list.add(mapRow(rs));
      }
    }
    return list;
  }

  public void save(Prescription p) throws SQLException {
    String sql = "INSERT INTO prescriptions (visit_id, medicine_name, dosage, frequency, duration, instructions, prescribed_date, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?)";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, p.getVisitId());
      ps.setString(2, p.getMedicineName());
      ps.setString(3, p.getDosage());
      ps.setString(4, p.getFrequency());
      ps.setString(5, p.getDuration());
      ps.setString(6, p.getInstructions());
      ps.setString(7, p.getPrescribedDate() != null ? p.getPrescribedDate().toString() : LocalDate.now().toString());
      ps.setString(8, LocalDate.now().toString());
      ps.setString(9, LocalDate.now().toString());
      ps.executeUpdate();
    }
  }

  public void update(Prescription p) throws SQLException {
    String sql = "UPDATE prescriptions SET visit_id=?, medicine_name=?, dosage=?, frequency=?, duration=?, instructions=?, prescribed_date=?, updated_at=? WHERE prescription_id=?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, p.getVisitId());
      ps.setString(2, p.getMedicineName());
      ps.setString(3, p.getDosage());
      ps.setString(4, p.getFrequency());
      ps.setString(5, p.getDuration());
      ps.setString(6, p.getInstructions());
      ps.setString(7, p.getPrescribedDate() != null ? p.getPrescribedDate().toString() : LocalDate.now().toString());
      ps.setString(8, LocalDate.now().toString());
      ps.setInt(9, p.getPrescriptionId());
      ps.setInt(6, p.getPrescriptionId());
      ps.executeUpdate();
    }
  }

  public void delete(int id) throws SQLException {
    String sql = "DELETE FROM prescriptions WHERE prescription_id=?";
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }

  public int count() throws SQLException {
    String sql = "SELECT COUNT(*) FROM prescriptions";
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      return rs.next() ? rs.getInt(1) : 0;
    }
  }
}
