package com.medicare.features.lab.dao;

import com.medicare.models.LabRequest;
import com.medicare.models.LabRequest.Status;
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

public class LabRequestDAO {

    private LabRequest mapRow(ResultSet rs) throws SQLException {
        LabRequest request = new LabRequest(
            rs.getInt("request_id"),
            rs.getInt("visit_id"),
            rs.getString("test_name"),
            LocalDate.parse(rs.getString("request_date")),
            Status.valueOf(rs.getString("status"))
        );

        // Set display fields if available from JOIN
        try {
            String regNumber = rs.getString("reg_number");
            if (regNumber != null) request.setStudentRegNumber(regNumber);
        } catch (SQLException ignored) { }

        try {
            String studentName = rs.getString("student_name");
            if (studentName != null) request.setStudentName(studentName);
        } catch (SQLException ignored) { }

        return request;
    }

    public List<LabRequest> findAll() throws SQLException {
        String sql = """
            SELECT lr.*, v.reg_number, s.full_name AS student_name
            FROM lab_requests lr
            LEFT JOIN medical_visits v ON lr.visit_id = v.visit_id
            LEFT JOIN students s ON v.reg_number = s.reg_number
            ORDER BY lr.request_date DESC
            """;
        List<LabRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) requests.add(mapRow(rs));
        }
        return requests;
    }

    public List<LabRequest> findByStatus(Status status) throws SQLException {
        String sql = """
            SELECT lr.*, v.reg_number, s.full_name AS student_name
            FROM lab_requests lr
            LEFT JOIN medical_visits v ON lr.visit_id = v.visit_id
            LEFT JOIN students s ON v.reg_number = s.reg_number
            WHERE lr.status = ?
            ORDER BY lr.request_date DESC
            """;
        List<LabRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(mapRow(rs));
            }
        }
        return requests;
    }

    public List<LabRequest> findByVisitId(int visitId) throws SQLException {
        String sql = """
            SELECT lr.*, v.reg_number, s.full_name AS student_name
            FROM lab_requests lr
            LEFT JOIN medical_visits v ON lr.visit_id = v.visit_id
            LEFT JOIN students s ON v.reg_number = s.reg_number
            WHERE lr.visit_id = ?
            ORDER BY lr.request_date DESC
            """;
        List<LabRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(mapRow(rs));
            }
        }
        return requests;
    }

    public Optional<LabRequest> findById(int requestId) throws SQLException {
        String sql = """
            SELECT lr.*, v.reg_number, s.full_name AS student_name
            FROM lab_requests lr
            LEFT JOIN medical_visits v ON lr.visit_id = v.visit_id
            LEFT JOIN students s ON v.reg_number = s.reg_number
            WHERE lr.request_id = ?
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public void save(LabRequest request) throws SQLException {
        String sql = "INSERT INTO lab_requests (visit_id, test_name, request_date, status) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getVisitId());
            ps.setString(2, request.getTestName());
            ps.setString(3, request.getRequestDate().toString());
            ps.setString(4, request.getStatus().name());
            ps.executeUpdate();
        }
    }

    public void update(LabRequest request) throws SQLException {
        String sql = "UPDATE lab_requests SET visit_id=?, test_name=?, request_date=?, status=? WHERE request_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getVisitId());
            ps.setString(2, request.getTestName());
            ps.setString(3, request.getRequestDate().toString());
            ps.setString(4, request.getStatus().name());
            ps.setInt(5, request.getRequestId());
            ps.executeUpdate();
        }
    }

    public void delete(int requestId) throws SQLException {
        String sql = "DELETE FROM lab_requests WHERE request_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
    }

    public int countByStatus(Status status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM lab_requests WHERE status = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
