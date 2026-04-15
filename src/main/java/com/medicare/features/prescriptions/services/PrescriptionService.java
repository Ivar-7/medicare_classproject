package com.medicare.features.prescriptions.services;

import com.medicare.features.prescriptions.dao.PrescriptionDAO;
import com.medicare.models.Prescription;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PrescriptionService {

    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    public List<Prescription> getAllPrescriptions() throws SQLException {
        return prescriptionDAO.findAll();
    }

    public Optional<Prescription> getPrescriptionById(int id) throws SQLException {
        return prescriptionDAO.findById(id);
    }

    public boolean prescriptionExists(int id) throws SQLException {
        return prescriptionDAO.existsById(id);
    }

    public Optional<Integer> getPrescriptionVisitId(int id) throws SQLException {
        return prescriptionDAO.findVisitIdByPrescriptionId(id);
    }

    public List<Prescription> getPrescriptionsByVisit(int visitId) throws SQLException {
        return prescriptionDAO.findByVisit(visitId);
    }

    public List<Prescription> getPrescriptionsByDoctor(int doctorId) throws SQLException {
        return prescriptionDAO.findByDoctor(doctorId);
    }

    public void createPrescription(Prescription prescription) throws SQLException {
        prescriptionDAO.save(prescription);
    }

    public void updatePrescription(Prescription prescription) throws SQLException {
        prescriptionDAO.update(prescription);
    }

    public void deletePrescription(int id) throws SQLException {
        prescriptionDAO.delete(id);
    }

    public int countPrescriptions() throws SQLException {
        return prescriptionDAO.count();
    }
}
