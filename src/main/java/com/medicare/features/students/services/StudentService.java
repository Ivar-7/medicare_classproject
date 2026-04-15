package com.medicare.features.students.services;

import com.medicare.features.students.dao.StudentDAO;
import com.medicare.models.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StudentService {

  private final StudentDAO studentDAO = new StudentDAO();

  public List<Student> getAllStudents() throws SQLException {
    return studentDAO.findAll();
  }

  public Optional<Student> getStudentByRegNumber(int regNumber) throws SQLException {
    return studentDAO.findByRegNumber(regNumber);
  }

  public boolean regNumberExists(int regNumber) throws SQLException {
    return studentDAO.existsByRegNumber(regNumber);
  }

  public boolean emailExists(String email) throws SQLException {
    return studentDAO.existsByEmail(email);
  }

  public boolean emailExistsForOtherStudent(String email, int regNumber) throws SQLException {
    return studentDAO.existsByEmailForOtherStudent(email, regNumber);
  }

  public List<Student> searchStudents(String query) throws SQLException {
    return studentDAO.search(query);
  }

  public void createStudent(Student student) throws SQLException {
    studentDAO.save(student);
  }

  public void updateStudent(Student student) throws SQLException {
    studentDAO.update(student);
  }

  public void deleteStudent(int regNumber) throws SQLException {
    studentDAO.delete(regNumber);
  }

  public int countStudents() throws SQLException {
    return studentDAO.count();
  }
}
