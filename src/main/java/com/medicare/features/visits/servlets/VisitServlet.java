package com.medicare.features.visits.servlets;

import com.medicare.features.students.services.StudentService;
import com.medicare.features.users.services.UserService;
import com.medicare.features.visits.services.MedicalVisitService;
import com.medicare.models.MedicalVisit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/visits", "/visits/*"})
public class VisitServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(VisitServlet.class.getName());

    private final MedicalVisitService visitService = new MedicalVisitService();
    private final StudentService      studentService = new StudentService();
    private final UserService         userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        exposeAlertsFromQuery(request);
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                request.setAttribute("visits", visitService.getAllVisits());
                request.getRequestDispatcher("/WEB-INF/views/visits/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                loadFormLookups(request);
                request.getRequestDispatcher("/WEB-INF/views/visits/form.jsp").forward(request, response);
            } else {
                int visitId = Integer.parseInt(pathInfo.substring(1));
                MedicalVisit visit = visitService.getVisitById(visitId).orElse(null);
                if (visit == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("visit", visit);
                loadFormLookups(request);
                request.getRequestDispatcher("/WEB-INF/views/visits/form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet GET error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            deleteVisit(request, response);
            return;
        }
        saveVisit(request, response);
    }

    private void saveVisit(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String visitIdRaw = trim(request.getParameter("visitId"));
        String regNumber = trim(request.getParameter("regNumber"));
        String doctorIdRaw = trim(request.getParameter("doctorId"));
        String visitDateRaw = trim(request.getParameter("visitDate"));
        String symptoms = trim(request.getParameter("symptoms"));
        String diagnosis = trim(request.getParameter("diagnosis"));

        int doctorId = 0;
        LocalDateTime visitDate = null;

        if (regNumber == null || regNumber.isBlank()) {
            forwardWithError(request, response, "Student is required.", visitIdRaw, regNumber, doctorIdRaw,
                             visitDateRaw, symptoms, diagnosis);
            return;
        }

        try {
            doctorId = Integer.parseInt(doctorIdRaw);
            if (doctorId <= 0) {
                throw new NumberFormatException("doctorId must be positive");
            }
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "A valid doctor is required.", visitIdRaw, regNumber, doctorIdRaw,
                             visitDateRaw, symptoms, diagnosis);
            return;
        }

        try {
            visitDate = LocalDateTime.parse(visitDateRaw);
        } catch (DateTimeParseException e) {
            forwardWithError(request, response, "A valid visit date and time is required.", visitIdRaw,
                             regNumber, doctorIdRaw, visitDateRaw, symptoms, diagnosis);
            return;
        }

        if (symptoms == null || symptoms.isBlank() || diagnosis == null || diagnosis.isBlank()) {
            forwardWithError(request, response, "Symptoms and diagnosis are required.", visitIdRaw, regNumber,
                             doctorIdRaw, visitDateRaw, symptoms, diagnosis);
            return;
        }

        MedicalVisit visit = new MedicalVisit();
        visit.setRegNumber(regNumber);
        visit.setDoctorId(doctorId);
        visit.setVisitDate(visitDate);
        visit.setSymptoms(symptoms);
        visit.setDiagnosis(diagnosis);

        try {
            if (visitIdRaw == null || visitIdRaw.isBlank()) {
                visitService.createVisit(visit);
                response.sendRedirect(request.getContextPath() + "/visits?success=" +
                                      encode("Medical visit recorded successfully."));
                return;
            }

            int visitId = Integer.parseInt(visitIdRaw);
            visit.setVisitId(visitId);
            visitService.updateVisit(visit);
            response.sendRedirect(request.getContextPath() + "/visits?success=" +
                                  encode("Medical visit updated successfully."));
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "Invalid visit ID.", visitIdRaw, regNumber, doctorIdRaw,
                             visitDateRaw, symptoms, diagnosis);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet POST save error", e);
            response.sendRedirect(request.getContextPath() + "/visits?error=" +
                                  encode("A system error occurred while saving the visit."));
        }
    }

    private void deleteVisit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String visitIdRaw = trim(request.getParameter("visitId"));
        if (visitIdRaw == null || visitIdRaw.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/visits?error=" +
                                  encode("Visit ID is required to delete."));
            return;
        }

        try {
            int visitId = Integer.parseInt(visitIdRaw);
            visitService.deleteVisit(visitId);
            response.sendRedirect(request.getContextPath() + "/visits?success=" +
                                  encode("Medical visit deleted successfully."));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/visits?error=" +
                                  encode("Invalid visit ID."));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet POST delete error", e);
            response.sendRedirect(request.getContextPath() + "/visits?error=" +
                                  encode("A system error occurred while deleting the visit."));
        }
    }

    private void forwardWithError(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String errorMessage,
                                  String visitIdRaw,
                                  String regNumber,
                                  String doctorIdRaw,
                                  String visitDateRaw,
                                  String symptoms,
                                  String diagnosis) throws ServletException, IOException {
        MedicalVisit visit = new MedicalVisit();
        if (visitIdRaw != null && !visitIdRaw.isBlank()) {
            try {
                visit.setVisitId(Integer.parseInt(visitIdRaw));
            } catch (NumberFormatException ignored) {
                // Keep ID unset if malformed; validation message is enough for the user.
            }
        }

        if (doctorIdRaw != null && !doctorIdRaw.isBlank()) {
            try {
                visit.setDoctorId(Integer.parseInt(doctorIdRaw));
            } catch (NumberFormatException ignored) {
                // Keep doctor ID unset so the user can re-select.
            }
        }

        if (visitDateRaw != null && !visitDateRaw.isBlank()) {
            try {
                visit.setVisitDate(LocalDateTime.parse(visitDateRaw));
            } catch (DateTimeParseException ignored) {
                // Keep date unset so the user can correct it.
            }
        }

        visit.setRegNumber(regNumber);
        visit.setSymptoms(symptoms);
        visit.setDiagnosis(diagnosis);

        request.setAttribute("error", errorMessage);
        request.setAttribute("visit", visit);
        try {
            loadFormLookups(request);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet lookup load error", e);
            request.setAttribute("warning", "Some lookup data could not be loaded.");
        }
        request.getRequestDispatcher("/WEB-INF/views/visits/form.jsp").forward(request, response);
    }

    private void loadFormLookups(HttpServletRequest request) throws Exception {
        request.setAttribute("students", studentService.getAllStudents());
        request.setAttribute("doctors", userService.getDoctors());
    }

    private void exposeAlertsFromQuery(HttpServletRequest request) {
        String success = trim(request.getParameter("success"));
        String error = trim(request.getParameter("error"));
        String warning = trim(request.getParameter("warning"));

        if (success != null && !success.isBlank()) {
            request.setAttribute("success", success);
        }
        if (error != null && !error.isBlank()) {
            request.setAttribute("error", error);
        }
        if (warning != null && !warning.isBlank()) {
            request.setAttribute("warning", warning);
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
