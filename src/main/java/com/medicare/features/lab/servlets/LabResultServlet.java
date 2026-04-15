package com.medicare.features.lab.servlets;

import com.medicare.features.lab.services.LabRequestService;
import com.medicare.features.lab.services.LabResultService;
import com.medicare.models.LabRequest;
import com.medicare.models.LabResult;
import com.medicare.models.User;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/lab/results", "/lab/results/*"})
public class LabResultServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(LabResultServlet.class.getName());

  private final LabResultService labResultService = new LabResultService();
  private final LabRequestService labRequestService = new LabRequestService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String pathInfo = request.getPathInfo();
    ServletRequestUtils.exposeAlertsFromQuery(request);
    User currentUser = ServletRequestUtils.getCurrentUser(request);

    try {
      if (pathInfo == null || pathInfo.equals("/")) {
        if (currentUser != null && currentUser.getRole() == User.Role.Technician) {
          request.setAttribute("labResults", labResultService.getLabResultsByTechnicianId(currentUser.getUserId()));
        } else {
          request.setAttribute("labResults", labResultService.getAllLabResults());
        }
        request.getRequestDispatcher("/WEB-INF/views/lab/results/list.jsp").forward(request, response);
        return;
      }

      if (pathInfo.equals("/new")) {
        LabResult labResult = new LabResult();
        if (currentUser != null) {
          labResult.setTechnicianId(currentUser.getUserId());
        }

        String requestIdRaw = ServletRequestUtils.trim(request.getParameter("requestId"));
        if (requestIdRaw != null && requestIdRaw.matches("\\d+")) {
          int requestId = Integer.parseInt(requestIdRaw);
          labResult.setRequestId(requestId);
          LabRequest labRequest = labRequestService.getLabRequestById(requestId).orElse(null);
          if (labRequest != null && labRequest.getStudentRegNumber() > 0) {
            labResult.setPatientId(labRequest.getStudentRegNumber());
          }
        }

        request.setAttribute("labResult", labResult);
        loadFormLookups(request);
        request.getRequestDispatcher("/WEB-INF/views/lab/results/form.jsp").forward(request, response);
        return;
      }

      if (pathInfo.startsWith("/delete/")) {
        deleteLabResultByPath(request, response, currentUser);
        return;
      }

      int resultId = Integer.parseInt(pathInfo.substring(1));
      LabResult labResult = labResultService.getLabResultById(resultId).orElse(null);
      if (labResult == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      if (currentUser != null
          && currentUser.getRole() == User.Role.Technician
          && labResult.getTechnicianId() != currentUser.getUserId()) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      request.setAttribute("labResult", labResult);
      loadFormLookups(request);
      request.getRequestDispatcher("/WEB-INF/views/lab/results/form.jsp").forward(request, response);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "LabResultServlet GET error", e);
      if (pathInfo == null || pathInfo.equals("/")) {
        request.setAttribute("error", mapLabResultSqlError(e));
        request.setAttribute("labResults", Collections.emptyList());
        request.getRequestDispatcher("/WEB-INF/views/lab/results/list.jsp").forward(request, response);
        return;
      }
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          mapLabResultSqlError(e));
    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabResultServlet GET error", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String action = ServletRequestUtils.trim(request.getParameter("action"));
    User currentUser = ServletRequestUtils.getCurrentUser(request);

    if ("delete".equalsIgnoreCase(action)) {
      deleteLabResultByForm(request, response, currentUser);
      return;
    }

    saveLabResult(request, response, currentUser);
  }

  private void saveLabResult(HttpServletRequest request, HttpServletResponse response, User currentUser)
      throws ServletException, IOException {
    if (currentUser == null) {
      ServletRequestUtils.redirectWithMessage(request, response, "/login", "error", "Please log in first.");
      return;
    }

    if (currentUser.getRole() != User.Role.Technician && currentUser.getRole() != User.Role.Admin) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          "Only lab technicians and admins can create or edit lab results.");
      return;
    }

    String resultIdRaw = ServletRequestUtils.trim(request.getParameter("resultId"));
    String requestIdRaw = ServletRequestUtils.trim(request.getParameter("requestId"));
    String patientIdRaw = ServletRequestUtils.trim(request.getParameter("patientId"));
    String resultDetails = ServletRequestUtils.trim(request.getParameter("resultDetails"));
    String resultStatus = ServletRequestUtils.trim(request.getParameter("resultStatus"));
    String resultValue = ServletRequestUtils.trim(request.getParameter("resultValue"));
    String remarks = ServletRequestUtils.trim(request.getParameter("remarks"));

    int requestId;
    int patientId;
    try {
      requestId = Integer.parseInt(requestIdRaw);
      if (requestId <= 0) {
        throw new NumberFormatException("requestId must be positive");
      }
    } catch (NumberFormatException e) {
      forwardWithError(request, response, "A valid lab request ID is required.", resultIdRaw, requestIdRaw,
          patientIdRaw, resultDetails, resultStatus, resultValue, remarks, currentUser.getUserId());
      return;
    }

    try {
      patientId = Integer.parseInt(patientIdRaw);
      if (patientId <= 0) {
        throw new NumberFormatException("patientId must be positive");
      }
    } catch (NumberFormatException e) {
      forwardWithError(request, response, "A valid patient registration number is required.", resultIdRaw,
          requestIdRaw, patientIdRaw, resultDetails, resultStatus, resultValue, remarks,
          currentUser.getUserId());
      return;
    }

    if (resultDetails == null || resultDetails.isBlank()) {
      forwardWithError(request, response, "Result details are required.", resultIdRaw, requestIdRaw,
          patientIdRaw, resultDetails, resultStatus, resultValue, remarks, currentUser.getUserId());
      return;
    }

    if (resultStatus == null || resultStatus.isBlank()) {
      forwardWithError(request, response, "Result status is required.", resultIdRaw, requestIdRaw,
          patientIdRaw, resultDetails, resultStatus, resultValue, remarks, currentUser.getUserId());
      return;
    }

    try {
      LabRequest linkedRequest = labRequestService.getLabRequestById(requestId).orElse(null);
      if (linkedRequest == null) {
        forwardWithError(request, response, "Selected lab request does not exist.", resultIdRaw, requestIdRaw,
            patientIdRaw, resultDetails, resultStatus, resultValue, remarks, currentUser.getUserId());
        return;
      }

      if (linkedRequest.getStudentRegNumber() > 0 && linkedRequest.getStudentRegNumber() != patientId) {
        forwardWithError(request, response,
            "Patient registration number does not match the selected lab request.",
            resultIdRaw, requestIdRaw, patientIdRaw, resultDetails, resultStatus, resultValue, remarks,
            currentUser.getUserId());
        return;
      }

      LabResult labResult = new LabResult();
      labResult.setRequestId(requestId);
      labResult.setPatientId(patientId);
      labResult.setTechnicianId(currentUser.getUserId());
      labResult.setResultDetails(resultDetails);
      labResult.setResultStatus(resultStatus);
      labResult.setResultValue(resultValue);
      labResult.setRemarks(remarks);

      if (resultIdRaw == null || resultIdRaw.isBlank()) {
        labResultService.createLabResult(labResult);
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "success",
            "Lab result created successfully.");
        return;
      }

      int resultId = Integer.parseInt(resultIdRaw);
      LabResult existing = labResultService.getLabResultById(resultId).orElse(null);
      if (existing == null) {
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
            "Lab result was not found.");
        return;
      }

      if (currentUser.getRole() == User.Role.Technician && existing.getTechnicianId() != currentUser.getUserId()) {
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
            "You cannot edit another technician's lab result.");
        return;
      }

      labResult.setResultId(resultId);
      labResultService.updateLabResult(labResult);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "success",
          "Lab result updated successfully.");
    } catch (NumberFormatException e) {
      forwardWithError(request, response, "Invalid lab result ID.", resultIdRaw, requestIdRaw,
          patientIdRaw, resultDetails, resultStatus, resultValue, remarks, currentUser.getUserId());
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "LabResultServlet POST save error", e);
      forwardWithError(request, response, mapLabResultSqlError(e), resultIdRaw, requestIdRaw,
          patientIdRaw, resultDetails, resultStatus, resultValue, remarks, currentUser.getUserId());
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabResultServlet POST save error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          "A system error occurred while saving the lab result.");
    }
  }

  private void deleteLabResultByPath(HttpServletRequest request, HttpServletResponse response, User currentUser)
      throws IOException {
    if (currentUser == null) {
      ServletRequestUtils.redirectWithMessage(request, response, "/login", "error", "Please log in first.");
      return;
    }

    if (currentUser.getRole() != User.Role.Technician && currentUser.getRole() != User.Role.Admin) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          "Only lab technicians and admins can delete lab results.");
      return;
    }

    try {
      String pathInfo = request.getPathInfo();
      int resultId = Integer.parseInt(pathInfo.substring("/delete/".length()));

      LabResult existing = labResultService.getLabResultById(resultId).orElse(null);
      if (existing == null) {
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
            "Lab result was not found.");
        return;
      }

      if (currentUser.getRole() == User.Role.Technician && existing.getTechnicianId() != currentUser.getUserId()) {
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
            "You cannot delete another technician's lab result.");
        return;
      }

      labResultService.deleteLabResult(resultId);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "success",
          "Lab result deleted successfully.");
    } catch (NumberFormatException e) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error", "Invalid lab result ID.");
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "LabResultServlet GET delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          mapLabResultSqlError(e));
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabResultServlet GET delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          "A system error occurred while deleting the lab result.");
    }
  }

  private void deleteLabResultByForm(HttpServletRequest request, HttpServletResponse response, User currentUser)
      throws IOException {
    if (currentUser == null) {
      ServletRequestUtils.redirectWithMessage(request, response, "/login", "error", "Please log in first.");
      return;
    }

    if (currentUser.getRole() != User.Role.Technician && currentUser.getRole() != User.Role.Admin) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          "Only lab technicians and admins can delete lab results.");
      return;
    }

    String resultIdRaw = ServletRequestUtils.trim(request.getParameter("resultId"));
    if (resultIdRaw == null || resultIdRaw.isBlank()) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          "Lab result ID is required to delete.");
      return;
    }

    try {
      int resultId = Integer.parseInt(resultIdRaw);
      LabResult existing = labResultService.getLabResultById(resultId).orElse(null);
      if (existing == null) {
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
            "Lab result was not found.");
        return;
      }

      if (currentUser.getRole() == User.Role.Technician && existing.getTechnicianId() != currentUser.getUserId()) {
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
            "You cannot delete another technician's lab result.");
        return;
      }

      labResultService.deleteLabResult(resultId);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "success",
          "Lab result deleted successfully.");
    } catch (NumberFormatException e) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error", "Invalid lab result ID.");
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "LabResultServlet POST delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          mapLabResultSqlError(e));
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabResultServlet POST delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/results", "error",
          "A system error occurred while deleting the lab result.");
    }
  }

  private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
      String errorMessage, String resultIdRaw, String requestIdRaw, String patientIdRaw,
      String resultDetails, String resultStatus, String resultValue, String remarks,
      int technicianId) throws ServletException, IOException {
    LabResult labResult = new LabResult();

    if (resultIdRaw != null && resultIdRaw.matches("\\d+")) {
      labResult.setResultId(Integer.parseInt(resultIdRaw));
    }

    if (requestIdRaw != null && requestIdRaw.matches("\\d+")) {
      labResult.setRequestId(Integer.parseInt(requestIdRaw));
    }

    if (patientIdRaw != null && patientIdRaw.matches("\\d+")) {
      labResult.setPatientId(Integer.parseInt(patientIdRaw));
    }

    labResult.setTechnicianId(technicianId);
    labResult.setResultDetails(resultDetails);
    labResult.setResultStatus(resultStatus);
    labResult.setResultValue(resultValue);
    labResult.setRemarks(remarks);

    request.setAttribute("error", errorMessage);
    request.setAttribute("labResult", labResult);
    try {
      loadFormLookups(request);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabResultServlet lookup load error", e);
      request.setAttribute("warning", "Some lookup data could not be loaded.");
    }
    request.getRequestDispatcher("/WEB-INF/views/lab/results/form.jsp").forward(request, response);
  }

  private void loadFormLookups(HttpServletRequest request) throws Exception {
    request.setAttribute("labRequests", labRequestService.getAllLabRequests());
  }

  private String mapLabResultSqlError(SQLException e) {
    String sqlMessage = flattenSqlMessage(e).toLowerCase();
    if (sqlMessage.contains("foreign key constraint failed")) {
      return "Lab result references invalid request, patient, or technician records.";
    }
    if (sqlMessage.contains("not null constraint failed")) {
      return "Some required lab result fields are missing. Please complete all required fields.";
    }
    return "A database error occurred while processing the lab result.";
  }

  private String flattenSqlMessage(SQLException e) {
    StringBuilder sb = new StringBuilder();
    SQLException current = e;
    while (current != null) {
      if (current.getMessage() != null && !current.getMessage().isBlank()) {
        if (sb.length() > 0) {
          sb.append(" | ");
        }
        sb.append(current.getMessage());
      }
      current = current.getNextException();
    }
    return sb.toString();
  }
}
