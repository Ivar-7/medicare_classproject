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

    private Prescription mapRow(ResultSet rs) throws SQLException {
        return new Prescription(
            rs.getInt("prescription_id"),
            rs.getInt("visit_id"),
            rs.getString("medicine_name"),
            rs.getString("dosage"),
            rs.getString("duration")
        );
    }

    public List<Prescription> findAll() throws SQLException {
        String sql = "SELECT * FROM prescriptions ORDER BY prescription_id DESC";
        List<Prescription> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<Prescription> findById(int id) throws SQLException {
        String sql = "SELECT * FROM prescriptions WHERE prescription_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Prescription> findByVisit(int visitId) throws SQLException {
        String sql = "SELECT * FROM prescriptions WHERE visit_id = ? ORDER BY prescription_id";
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

    public void save(Prescription p) throws SQLException {
        String sql = "INSERT INTO prescriptions (visit_id, medicine_name, dosage, duration) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    p.getVisitId());
            ps.setString(2, p.getMedicineName());
            ps.setString(3, p.getDosage());
            ps.setString(4, p.getDuration());
            ps.executeUpdate();
        }
    }

    public void update(Prescription p) throws SQLException {
        String sql = "UPDATE prescriptions SET visit_id=?, medicine_name=?, dosage=?, duration=? WHERE prescription_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    p.getVisitId());
            ps.setString(2, p.getMedicineName());
            ps.setString(3, p.getDosage());
            ps.setString(4, p.getDuration());
            ps.setInt(5,    p.getPrescriptionId());
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
