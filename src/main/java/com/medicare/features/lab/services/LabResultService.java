package com.medicare.features.lab.services;

import com.medicare.features.lab.dao.LabResultDAO;
import com.medicare.models.LabResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LabResultService {

    private final LabResultDAO labResultDAO = new LabResultDAO();

    public List<LabResult> getAllLabResults() throws SQLException {
        return labResultDAO.findAll();
    }

    public List<LabResult> getLabResultsByRequestId(int requestId) throws SQLException {
        return labResultDAO.findByRequestId(requestId);
    }

    public List<LabResult> getLabResultsByTechnicianId(int technicianId) throws SQLException {
        return labResultDAO.findByTechnicianId(technicianId);
    }

    public Optional<LabResult> getLabResultById(int resultId) throws SQLException {
        return labResultDAO.findById(resultId);
    }

    public void createLabResult(LabResult result) throws SQLException {
        labResultDAO.save(result);
    }

    public void updateLabResult(LabResult result) throws SQLException {
        labResultDAO.update(result);
    }

    public void deleteLabResult(int resultId) throws SQLException {
        labResultDAO.delete(resultId);
    }
}
