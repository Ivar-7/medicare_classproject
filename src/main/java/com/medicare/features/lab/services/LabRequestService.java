package com.medicare.features.lab.services;

import com.medicare.features.lab.dao.LabRequestDAO;
import com.medicare.models.LabRequest;
import com.medicare.models.LabRequest.Status;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LabRequestService {

    private final LabRequestDAO labRequestDAO = new LabRequestDAO();

    public List<LabRequest> getAllLabRequests() throws SQLException {
        return labRequestDAO.findAll();
    }

    public List<LabRequest> getLabRequestsByStatus(Status status) throws SQLException {
        return labRequestDAO.findByStatus(status);
    }

    public List<LabRequest> getLabRequestsByVisitId(int visitId) throws SQLException {
        return labRequestDAO.findByVisitId(visitId);
    }

    public Optional<LabRequest> getLabRequestById(int requestId) throws SQLException {
        return labRequestDAO.findById(requestId);
    }

    public void createLabRequest(LabRequest request) throws SQLException {
        labRequestDAO.save(request);
    }

    public void updateLabRequest(LabRequest request) throws SQLException {
        labRequestDAO.update(request);
    }

    public void deleteLabRequest(int requestId) throws SQLException {
        labRequestDAO.delete(requestId);
    }

    public int countByStatus(Status status) throws SQLException {
        return labRequestDAO.countByStatus(status);
    }
}
