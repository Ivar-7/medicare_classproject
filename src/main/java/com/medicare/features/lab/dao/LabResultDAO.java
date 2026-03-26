package com.medicare.features.lab.dao;

import com.medicare.models.LabResult;
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

public class LabResultDAO {

    private LabResult mapRow(ResultSet rs) throws SQLException {
        LabResult result = new LabResult(
            rs.getInt("result_id"),
            rs.getInt("request_id"),
            rs.getInt("technician_id"),
            rs.getString("result_details"),
            LocalDate.parse(rs.getString("result_date"))
        );

        // Set display fields if available from JOIN
        try {
            String technicianName = rs.getString("technician_name");
            if (technicianName != null) result.setTechnicianName(technicianName);
        } catch (SQLException ignored) { }

        try {
            String testName = rs.getString("test_name");
            if (testName != null) result.setTestName(testName);
        } catch (SQLException ignored) { }

        try {
            String studentName = rs.getString("student_name");
            if (studentName != null) result.setStudentName(studentName);
        } catch (SQLException ignored) { }

        return result;
    }

    public List<LabResult> findAll() throws SQLException {
        String sql = """
            SELECT lr.*, u.full_name AS technician_name, lreq.test_name,
                   v.reg_number, s.full_name AS student_name
            FROM lab_results lr
            LEFT JOIN users u ON lr.technician_id = u.user_id
            LEFT JOIN lab_requests lreq ON lr.request_id = lreq.request_id
            LEFT JOIN medical_visits v ON lreq.visit_id = v.visit_id
            LEFT JOIN students s ON v.reg_number = s.reg_number
            ORDER BY lr.result_date DESC
            """;
        List<LabResult> results = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) results.add(mapRow(rs));
        }
        return results;
    }

    public List<LabResult> findByRequestId(int requestId) throws SQLException {
        String sql = """
            SELECT lr.*, u.full_name AS technician_name, lreq.test_name,
                   v.reg_number, s.full_name AS student_name
            FROM lab_results lr
            LEFT JOIN users u ON lr.technician_id = u.user_id
            LEFT JOIN lab_requests lreq ON lr.request_id = lreq.request_id
            LEFT JOIN medical_visits v ON lreq.visit_id = v.visit_id
            LEFT JOIN students s ON v.reg_number = s.reg_number
            WHERE lr.request_id = ?
            ORDER BY lr.result_date DESC
            """;
        List<LabResult> results = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        }
        return results;
    }

    public List<LabResult> findByTechnicianId(int technicianId) throws SQLException {
        String sql = """
            SELECT lr.*, u.full_name AS technician_name, lreq.test_name,
                   v.reg_number, s.full_name AS student_name
            FROM lab_results lr
            LEFT JOIN users u ON lr.technician_id = u.user_id
            LEFT JOIN lab_requests lreq ON lr.request_id = lreq.request_id
            LEFT JOIN medical_visits v ON lreq.visit_id = v.visit_id
            LEFT JOIN students s ON v.reg_number = s.reg_number
            WHERE lr.technician_id = ?
            ORDER BY lr.result_date DESC
            """;
        List<LabResult> results = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        }
        return results;
    }

    public Optional<LabResult> findById(int resultId) throws SQLException {
        String sql = """
            SELECT lr.*, u.full_name AS technician_name, lreq.test_name,
                   v.reg_number, s.full_name AS student_name
            FROM lab_results lr
            LEFT JOIN users u ON lr.technician_id = u.user_id
            LEFT JOIN lab_requests lreq ON lr.request_id = lreq.request_id
            LEFT JOIN medical_visits v ON lreq.visit_id = v.visit_id
            LEFT JOIN students s ON v.reg_number = s.reg_number
            WHERE lr.result_id = ?
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resultId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public void save(LabResult result) throws SQLException {
        String sql = "INSERT INTO lab_results (request_id, technician_id, result_details, result_date) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, result.getRequestId());
            ps.setInt(2, result.getTechnicianId());
            ps.setString(3, result.getResultDetails());
            ps.setString(4, result.getResultDate().toString());
            ps.executeUpdate();
        }
    }

    public void update(LabResult result) throws SQLException {
        String sql = "UPDATE lab_results SET request_id=?, technician_id=?, result_details=?, result_date=? WHERE result_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, result.getRequestId());
            ps.setInt(2, result.getTechnicianId());
            ps.setString(3, result.getResultDetails());
            ps.setString(4, result.getResultDate().toString());
            ps.setInt(5, result.getResultId());
            ps.executeUpdate();
        }
    }

    public void delete(int resultId) throws SQLException {
        String sql = "DELETE FROM lab_results WHERE result_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resultId);
            ps.executeUpdate();
        }
    }
}
