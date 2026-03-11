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
            rs.getString("reg_number"),
            rs.getString("full_name"),
            dobStr != null && !dobStr.isEmpty() ? LocalDate.parse(dobStr) : null,
            rs.getString("gender"),
            rs.getString("faculty"),
            rs.getString("contact")
        );
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT * FROM students ORDER BY full_name";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) students.add(mapRow(rs));
        }
        return students;
    }

    public Optional<Student> findByRegNumber(String regNumber) throws SQLException {
        String sql = "SELECT * FROM students WHERE reg_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Student> search(String query) throws SQLException {
        String sql  = "SELECT * FROM students WHERE full_name LIKE ? OR reg_number LIKE ? ORDER BY full_name";
        String term = "%" + query + "%";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, term);
            ps.setString(2, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) students.add(mapRow(rs));
            }
        }
        return students;
    }

    public void save(Student student) throws SQLException {
        String sql = "INSERT INTO students (reg_number, full_name, dob, gender, faculty, contact) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getRegNumber());
            ps.setString(2, student.getFullName());
            ps.setString(3, student.getDob() != null ? student.getDob().toString() : null);
            ps.setString(4, student.getGender());
            ps.setString(5, student.getFaculty());
            ps.setString(6, student.getContact());
            ps.executeUpdate();
        }
    }

    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET full_name=?, dob=?, gender=?, faculty=?, contact=? WHERE reg_number=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getDob() != null ? student.getDob().toString() : null);
            ps.setString(3, student.getGender());
            ps.setString(4, student.getFaculty());
            ps.setString(5, student.getContact());
            ps.setString(6, student.getRegNumber());
            ps.executeUpdate();
        }
    }

    public void delete(String regNumber) throws SQLException {
        String sql = "DELETE FROM students WHERE reg_number=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNumber);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
