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

public class PrescriptionDAO {

    private static final String SELECT_WITH_VISIT =
        "SELECT p.*, v.reg_number AS student_reg_number " +
        "FROM prescriptions p " +
        "LEFT JOIN medical_visits v ON p.visit_id = v.visit_id ";

    private Prescription mapRow(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription(
            rs.getInt("prescription_id"),
            rs.getInt("visit_id"),
            rs.getString("medicine_name"),
            rs.getString("diagnosis"),
            rs.getString("dosage"),
            rs.getString("duration")
        );
        try { prescription.setStudentRegNumber(rs.getString("student_reg_number")); } catch (SQLException ignored) { }
        return prescription;
    }

    public List<Prescription> findAll() throws SQLException {
        String sql = SELECT_WITH_VISIT + "ORDER BY p.prescription_id DESC";
        List<Prescription> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
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
                while (rs.next()) list.add(mapRow(rs));
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
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void save(Prescription p) throws SQLException {
        String sql = "INSERT INTO prescriptions (visit_id, medicine_name, diagnosis, dosage, duration) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    p.getVisitId());
            ps.setString(2, p.getMedicineName());
            ps.setString(3, p.getDiagnosis());
            ps.setString(4, p.getDosage());
            ps.setString(5, p.getDuration());
            ps.executeUpdate();
        }
    }

    public void update(Prescription p) throws SQLException {
        String sql = "UPDATE prescriptions SET visit_id=?, medicine_name=?, diagnosis=?, dosage=?, duration=? WHERE prescription_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    p.getVisitId());
            ps.setString(2, p.getMedicineName());
            ps.setString(3, p.getDiagnosis());
            ps.setString(4, p.getDosage());
            ps.setString(5, p.getDuration());
            ps.setInt(6,    p.getPrescriptionId());
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
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
