package com.medicare.features.notes.dao;

import com.medicare.models.TreatmentNote;
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

public class TreatmentNoteDAO {

    private TreatmentNote mapRow(ResultSet rs) throws SQLException {
        String fuStr = rs.getString("follow_up_date");
        return new TreatmentNote(
            rs.getInt("note_id"),
            rs.getInt("visit_id"),
            rs.getString("clinical_notes"),
            fuStr != null && !fuStr.isEmpty() ? LocalDate.parse(fuStr) : null
        );
    }

    public List<TreatmentNote> findAll() throws SQLException {
        String sql = "SELECT * FROM treatment_notes ORDER BY note_id DESC";
        List<TreatmentNote> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<TreatmentNote> findById(int id) throws SQLException {
        String sql = "SELECT * FROM treatment_notes WHERE note_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public boolean existsById(int id) throws SQLException {
        String sql = "SELECT 1 FROM treatment_notes WHERE note_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Optional<Integer> findVisitIdByNoteId(int id) throws SQLException {
        String sql = "SELECT visit_id FROM treatment_notes WHERE note_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("visit_id"));
                }
                return Optional.empty();
            }
        }
    }

    public List<TreatmentNote> findByVisit(int visitId) throws SQLException {
        String sql = "SELECT * FROM treatment_notes WHERE visit_id = ? ORDER BY note_id";
        List<TreatmentNote> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void save(TreatmentNote note) throws SQLException {
        String sql = "INSERT INTO treatment_notes (visit_id, clinical_notes, follow_up_date) VALUES (?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    note.getVisitId());
            ps.setString(2, note.getClinicalNotes());
            ps.setString(3, note.getFollowUpDate() != null ? note.getFollowUpDate().toString() : null);
            ps.executeUpdate();
        }
    }

    public void update(TreatmentNote note) throws SQLException {
        String sql = "UPDATE treatment_notes SET visit_id=?, clinical_notes=?, follow_up_date=? WHERE note_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    note.getVisitId());
            ps.setString(2, note.getClinicalNotes());
            ps.setString(3, note.getFollowUpDate() != null ? note.getFollowUpDate().toString() : null);
            ps.setInt(4,    note.getNoteId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM treatment_notes WHERE note_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM treatment_notes";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
