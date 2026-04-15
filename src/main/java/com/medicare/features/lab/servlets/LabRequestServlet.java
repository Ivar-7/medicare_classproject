package com.medicare.features.lab.servlets;

import com.medicare.features.lab.services.LabRequestService;
import com.medicare.features.visits.services.MedicalVisitService;
import com.medicare.models.LabRequest;
import com.medicare.models.User;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/lab/requests", "/lab/requests/*"})
public class LabRequestServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(LabRequestServlet.class.getName());

  private final LabRequestService labRequestService = new LabRequestService();
  private final MedicalVisitService visitService = new MedicalVisitService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String pathInfo = request.getPathInfo();
    ServletRequestUtils.exposeAlertsFromQuery(request);

    try {
      if (pathInfo == null || pathInfo.equals("/")) {
        request.setAttribute("labRequests", labRequestService.getAllLabRequests());
        request.getRequestDispatcher("/WEB-INF/views/lab/requests/list.jsp").forward(request, response);
        return;
      }

      if (pathInfo.equals("/new")) {
        LabRequest labRequest = new LabRequest();
        labRequest.setRequestDate(LocalDate.now());
        labRequest.setStatus(LabRequest.Status.Pending);
        labRequest.setPriority(LabRequest.Priority.Routine);
        User currentUser = ServletRequestUtils.getCurrentUser(request);
        if (currentUser != null) {
          labRequest.setRequestedBy(currentUser.getUserId());
        }

        request.setAttribute("labRequest", labRequest);
        loadFormLookups(request);
        request.getRequestDispatcher("/WEB-INF/views/lab/requests/form.jsp").forward(request, response);
        return;
      }

      if (pathInfo.startsWith("/delete/")) {
        deleteLabRequestByPath(request, response);
        return;
      }

      int requestId = Integer.parseInt(pathInfo.substring(1));
      LabRequest labRequest = labRequestService.getLabRequestById(requestId).orElse(null);
      if (labRequest == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      request.setAttribute("labRequest", labRequest);
      loadFormLookups(request);
      request.getRequestDispatcher("/WEB-INF/views/lab/requests/form.jsp").forward(request, response);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "LabRequestServlet GET error", e);
      if (pathInfo == null || pathInfo.equals("/")) {
        request.setAttribute("error", mapLabRequestSqlError(e));
        request.setAttribute("labRequests", Collections.emptyList());
        request.getRequestDispatcher("/WEB-INF/views/lab/requests/list.jsp").forward(request, response);
        return;
      }
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          mapLabRequestSqlError(e));
    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabRequestServlet GET error", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String action = ServletRequestUtils.trim(request.getParameter("action"));

    if ("delete".equalsIgnoreCase(action)) {
      deleteLabRequestByForm(request, response);
      return;
    }

    saveLabRequest(request, response);
  }

  private void saveLabRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    User currentUser = ServletRequestUtils.getCurrentUser(request);
    if (currentUser == null) {
      ServletRequestUtils.redirectWithMessage(request, response, "/login", "error", "Please log in first.");
      return;
    }

    if (currentUser.getRole() == User.Role.Technician) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "Lab technicians can view requests but cannot create or edit them.");
      return;
    }

    String requestIdRaw = ServletRequestUtils.trim(request.getParameter("requestId"));
    String visitIdRaw = ServletRequestUtils.trim(request.getParameter("visitId"));
    String testName = ServletRequestUtils.trim(request.getParameter("testName"));
    String testDescription = ServletRequestUtils.trim(request.getParameter("testDescription"));
    String priorityRaw = ServletRequestUtils.trim(request.getParameter("priority"));
    String statusRaw = ServletRequestUtils.trim(request.getParameter("status"));

    int visitId;
    try {
      visitId = Integer.parseInt(visitIdRaw);
      if (visitId <= 0) {
        throw new NumberFormatException("visitId must be positive");
      }
    } catch (NumberFormatException e) {
      forwardWithError(request, response, "A valid visit ID is required.", requestIdRaw, visitIdRaw,
          testName, testDescription, priorityRaw, statusRaw);
      return;
    }

    if (testName == null || testName.isBlank()) {
      forwardWithError(request, response, "Test name is required.", requestIdRaw, visitIdRaw,
          testName, testDescription, priorityRaw, statusRaw);
      return;
    }

    LabRequest.Priority priority;
    try {
      priority = parsePriority(priorityRaw);
    } catch (IllegalArgumentException e) {
      forwardWithError(request, response, "Priority must be Routine or Urgent.", requestIdRaw, visitIdRaw,
          testName, testDescription, priorityRaw, statusRaw);
      return;
    }

    LabRequest.Status status;
    try {
      status = parseStatus(statusRaw);
    } catch (IllegalArgumentException e) {
      forwardWithError(request, response, "Invalid lab request status value.", requestIdRaw, visitIdRaw,
          testName, testDescription, priorityRaw, statusRaw);
      return;
    }

    try {
      LabRequest labRequest = new LabRequest();
      labRequest.setVisitId(visitId);
      labRequest.setRequestedBy(currentUser.getUserId());
      labRequest.setTestName(testName);
      labRequest.setTestDescription(testDescription);
      labRequest.setRequestDate(LocalDate.now());
      labRequest.setPriority(priority);
      labRequest.setStatus(status);

      if (requestIdRaw == null || requestIdRaw.isBlank()) {
        labRequestService.createLabRequest(labRequest);
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "success",
            "Lab request created successfully.");
        return;
      }

      int requestId = Integer.parseInt(requestIdRaw);
      if (labRequestService.getLabRequestById(requestId).isEmpty()) {
        ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
            "Lab request was not found.");
        return;
      }

      labRequest.setRequestId(requestId);
      labRequestService.updateLabRequest(labRequest);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "success",
          "Lab request updated successfully.");
    } catch (NumberFormatException e) {
      forwardWithError(request, response, "Invalid request ID.", requestIdRaw, visitIdRaw,
          testName, testDescription, priorityRaw, statusRaw);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "LabRequestServlet POST save error", e);
      forwardWithError(request, response, mapLabRequestSqlError(e), requestIdRaw, visitIdRaw,
          testName, testDescription, priorityRaw, statusRaw);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabRequestServlet POST save error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "A system error occurred while saving the lab request.");
    }
  }

  private void deleteLabRequestByPath(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    User currentUser = ServletRequestUtils.getCurrentUser(request);
    if (currentUser == null) {
      ServletRequestUtils.redirectWithMessage(request, response, "/login", "error", "Please log in first.");
      return;
    }

    if (currentUser.getRole() == User.Role.Technician) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "Lab technicians can view requests but cannot delete them.");
      return;
    }

    String pathInfo = request.getPathInfo();
    try {
      int requestId = Integer.parseInt(pathInfo.substring("/delete/".length()));
      labRequestService.deleteLabRequest(requestId);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "success",
          "Lab request deleted successfully.");
    } catch (NumberFormatException e) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "Invalid lab request ID.");
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "LabRequestServlet GET delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          mapLabRequestSqlError(e));
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabRequestServlet GET delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "A system error occurred while deleting the lab request.");
    }
  }

  private void deleteLabRequestByForm(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    User currentUser = ServletRequestUtils.getCurrentUser(request);
    if (currentUser == null) {
      ServletRequestUtils.redirectWithMessage(request, response, "/login", "error", "Please log in first.");
      return;
    }

    if (currentUser.getRole() == User.Role.Technician) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "Lab technicians can view requests but cannot delete them.");
      return;
    }

    String requestIdRaw = ServletRequestUtils.trim(request.getParameter("requestId"));
    if (requestIdRaw == null || requestIdRaw.isBlank()) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "Lab request ID is required to delete.");
      return;
    }

    try {
      int requestId = Integer.parseInt(requestIdRaw);
      labRequestService.deleteLabRequest(requestId);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "success",
          "Lab request deleted successfully.");
    } catch (NumberFormatException e) {
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "Invalid lab request ID.");
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "LabRequestServlet POST delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          mapLabRequestSqlError(e));
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabRequestServlet POST delete error", e);
      ServletRequestUtils.redirectWithMessage(request, response, "/lab/requests", "error",
          "A system error occurred while deleting the lab request.");
    }
  }

  private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
      String errorMessage, String requestIdRaw, String visitIdRaw,
      String testName, String testDescription, String priorityRaw, String statusRaw)
      throws ServletException, IOException {
    LabRequest labRequest = new LabRequest();

    if (requestIdRaw != null && requestIdRaw.matches("\\d+")) {
      labRequest.setRequestId(Integer.parseInt(requestIdRaw));
    }

    if (visitIdRaw != null && visitIdRaw.matches("\\d+")) {
      labRequest.setVisitId(Integer.parseInt(visitIdRaw));
    }

    labRequest.setRequestedBy(ServletRequestUtils.getCurrentUser(request).getUserId());
    labRequest.setRequestDate(LocalDate.now());
    labRequest.setTestName(testName);
    labRequest.setTestDescription(testDescription);

    try {
      labRequest.setPriority(parsePriority(priorityRaw));
    } catch (IllegalArgumentException ignored) {
      labRequest.setPriority(LabRequest.Priority.Routine);
    }

    try {
      labRequest.setStatus(parseStatus(statusRaw));
    } catch (IllegalArgumentException ignored) {
      labRequest.setStatus(LabRequest.Status.Pending);
    }

    request.setAttribute("error", errorMessage);
    request.setAttribute("labRequest", labRequest);
    try {
      loadFormLookups(request);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "LabRequestServlet lookup load error", e);
      request.setAttribute("warning", "Some lookup data could not be loaded.");
    }
    request.getRequestDispatcher("/WEB-INF/views/lab/requests/form.jsp").forward(request, response);
  }

  private void loadFormLookups(HttpServletRequest request) throws Exception {
    request.setAttribute("visits", visitService.getAllVisits());
  }

  private LabRequest.Priority parsePriority(String value) {
    if (value == null || value.isBlank()) {
      return LabRequest.Priority.Routine;
    }
    return LabRequest.Priority.valueOf(value);
  }

  private LabRequest.Status parseStatus(String value) {
    if (value == null || value.isBlank()) {
      return LabRequest.Status.Pending;
    }
    return LabRequest.Status.valueOf(value);
  }

  private String mapLabRequestSqlError(SQLException e) {
    String sqlMessage = flattenSqlMessage(e).toLowerCase();
    if (sqlMessage.contains("foreign key constraint failed")) {
      return "Lab request references invalid visit or user records. Please verify input.";
    }
    if (sqlMessage.contains("not null constraint failed")) {
      return "Some required lab request fields are missing. Please complete all required fields.";
    }
    return "A database error occurred while processing the lab request.";
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
