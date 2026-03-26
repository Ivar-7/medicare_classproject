package com.medicare.features.history.dao;

import com.medicare.models.MedicalHistory;
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

public class MedicalHistoryDAO {

    private MedicalHistory mapRow(ResultSet rs) throws SQLException {
        MedicalHistory history = new MedicalHistory(
            rs.getInt("history_id"),
            rs.getString("reg_number"),
            rs.getString("condition_name"),
            LocalDate.parse(rs.getString("diagnosis_date")),
            rs.getInt("doctor_id")
        );

        // Set display fields if available from JOIN
        try {
            String studentName = rs.getString("student_name");
            if (studentName != null) history.setStudentName(studentName);
        } catch (SQLException ignored) { }

        try {
            String doctorName = rs.getString("doctor_name");
            if (doctorName != null) history.setDoctorName(doctorName);
        } catch (SQLException ignored) { }

        return history;
    }

    public List<MedicalHistory> findAll() throws SQLException {
        String sql = """
            SELECT h.*, s.full_name AS student_name, u.full_name AS doctor_name
            FROM medical_history h
            LEFT JOIN students s ON h.reg_number = s.reg_number
            LEFT JOIN users u ON h.doctor_id = u.user_id
            ORDER BY h.diagnosis_date DESC
            """;
        List<MedicalHistory> histories = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) histories.add(mapRow(rs));
        }
        return histories;
    }

    public List<MedicalHistory> findByRegNumber(String regNumber) throws SQLException {
        String sql = """
            SELECT h.*, s.full_name AS student_name, u.full_name AS doctor_name
            FROM medical_history h
            LEFT JOIN students s ON h.reg_number = s.reg_number
            LEFT JOIN users u ON h.doctor_id = u.user_id
            WHERE h.reg_number = ?
            ORDER BY h.diagnosis_date DESC
            """;
        List<MedicalHistory> histories = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) histories.add(mapRow(rs));
            }
        }
        return histories;
    }

    public Optional<MedicalHistory> findById(int historyId) throws SQLException {
        String sql = """
            SELECT h.*, s.full_name AS student_name, u.full_name AS doctor_name
            FROM medical_history h
            LEFT JOIN students s ON h.reg_number = s.reg_number
            LEFT JOIN users u ON h.doctor_id = u.user_id
            WHERE h.history_id = ?
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, historyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public void save(MedicalHistory history) throws SQLException {
        String sql = "INSERT INTO medical_history (reg_number, condition_name, diagnosis_date, doctor_id) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, history.getRegNumber());
            ps.setString(2, history.getConditionName());
            ps.setString(3, history.getDiagnosisDate().toString());
            ps.setInt(4, history.getDoctorId());
            ps.executeUpdate();
        }
    }

    public void update(MedicalHistory history) throws SQLException {
        String sql = "UPDATE medical_history SET reg_number=?, condition_name=?, diagnosis_date=?, doctor_id=? WHERE history_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, history.getRegNumber());
            ps.setString(2, history.getConditionName());
            ps.setString(3, history.getDiagnosisDate().toString());
            ps.setInt(4, history.getDoctorId());
            ps.setInt(5, history.getHistoryId());
            ps.executeUpdate();
        }
    }

    public void delete(int historyId) throws SQLException {
        String sql = "DELETE FROM medical_history WHERE history_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, historyId);
            ps.executeUpdate();
        }
    }
}
