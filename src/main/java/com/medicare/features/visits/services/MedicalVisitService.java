package com.medicare.features.visits.services;

import com.medicare.features.visits.dao.MedicalVisitDAO;
import com.medicare.models.MedicalVisit;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MedicalVisitService {

  private final MedicalVisitDAO visitDAO = new MedicalVisitDAO();

  public List<MedicalVisit> getAllVisits() throws SQLException {
    return visitDAO.findAll();
  }

  public Optional<MedicalVisit> getVisitById(int id) throws SQLException {
    return visitDAO.findById(id);
  }

  public List<MedicalVisit> getVisitsByStudent(int regNumber) throws SQLException {
    return visitDAO.findByStudent(regNumber);
  }

  public List<MedicalVisit> getVisitsByDoctor(int doctorId) throws SQLException {
    return visitDAO.findByDoctor(doctorId);
  }

  public List<MedicalVisit> getPendingVisitsByDoctor(int doctorId) throws SQLException {
    return visitDAO.findPendingByDoctor(doctorId);
  }

  public List<MedicalVisit> getRecentVisits(int limit) throws SQLException {
    return visitDAO.findRecent(limit);
  }

  public int countTodayVisits() throws SQLException {
    return visitDAO.countToday();
  }

  public int countAllVisits() throws SQLException {
    return visitDAO.countTotal();
  }

  public int countVisitsByDoctor(int doctorId) throws SQLException {
    return visitDAO.countByDoctor(doctorId);
  }

  public int countPendingVisitsByDoctor(int doctorId) throws SQLException {
    return visitDAO.countPendingByDoctor(doctorId);
  }

  public int createVisit(MedicalVisit visit) throws SQLException {
    return visitDAO.save(visit);
  }

  public void updateVisit(MedicalVisit visit) throws SQLException {
    visitDAO.update(visit);
  }

  public void deleteVisit(int visitId) throws SQLException {
    visitDAO.delete(visitId);
  }
}
