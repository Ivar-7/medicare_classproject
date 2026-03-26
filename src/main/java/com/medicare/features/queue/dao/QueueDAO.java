package com.medicare.features.queue.dao;

import com.medicare.models.Queue;
import com.medicare.models.Queue.PriorityLevel;
import com.medicare.models.Queue.Status;
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

public class QueueDAO {

    private Queue mapRow(ResultSet rs) throws SQLException {
        Queue queue = new Queue(
            rs.getInt("queue_id"),
            rs.getString("reg_number"),
            LocalDate.parse(rs.getString("visit_date")),
            PriorityLevel.valueOf(rs.getString("priority_level")),
            Status.valueOf(rs.getString("status"))
        );

        // Set student name if available from JOIN
        try {
            String studentName = rs.getString("student_name");
            if (studentName != null) queue.setStudentName(studentName);
        } catch (SQLException ignored) { }

        return queue;
    }

    public List<Queue> findAll() throws SQLException {
        String sql = """
            SELECT q.*, s.full_name AS student_name
            FROM queue q
            LEFT JOIN students s ON q.reg_number = s.reg_number
            ORDER BY q.visit_date DESC, q.priority_level DESC
            """;
        List<Queue> queues = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) queues.add(mapRow(rs));
        }
        return queues;
    }

    public List<Queue> findByStatus(Status status) throws SQLException {
        String sql = """
            SELECT q.*, s.full_name AS student_name
            FROM queue q
            LEFT JOIN students s ON q.reg_number = s.reg_number
            WHERE q.status = ?
            ORDER BY q.priority_level DESC, q.visit_date
            """;
        List<Queue> queues = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) queues.add(mapRow(rs));
            }
        }
        return queues;
    }

    public Optional<Queue> findById(int queueId) throws SQLException {
        String sql = """
            SELECT q.*, s.full_name AS student_name
            FROM queue q
            LEFT JOIN students s ON q.reg_number = s.reg_number
            WHERE q.queue_id = ?
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, queueId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public void save(Queue queue) throws SQLException {
        String sql = "INSERT INTO queue (reg_number, visit_date, priority_level, status) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, queue.getRegNumber());
            ps.setString(2, queue.getVisitDate().toString());
            ps.setString(3, queue.getPriorityLevel().name());
            ps.setString(4, queue.getStatus().name());
            ps.executeUpdate();
        }
    }

    public void update(Queue queue) throws SQLException {
        String sql = "UPDATE queue SET reg_number=?, visit_date=?, priority_level=?, status=? WHERE queue_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, queue.getRegNumber());
            ps.setString(2, queue.getVisitDate().toString());
            ps.setString(3, queue.getPriorityLevel().name());
            ps.setString(4, queue.getStatus().name());
            ps.setInt(5, queue.getQueueId());
            ps.executeUpdate();
        }
    }

    public void delete(int queueId) throws SQLException {
        String sql = "DELETE FROM queue WHERE queue_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, queueId);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM queue";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int countByStatus(Status status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM queue WHERE status = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
