package com.medicare.features.visits.servlets;

import com.medicare.features.students.services.StudentService;
import com.medicare.features.users.services.UserService;
import com.medicare.features.visits.services.MedicalVisitService;
import com.medicare.models.MedicalVisit;
import com.medicare.models.User;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
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
        ServletRequestUtils.exposeAlertsFromQuery(request);
        User currentUser = ServletRequestUtils.getCurrentUser(request);
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                if (currentUser != null && currentUser.getRole() == User.Role.Doctor) {
                    request.setAttribute("visits", visitService.getVisitsByDoctor(currentUser.getUserId()));
                } else {
                    request.setAttribute("visits", visitService.getAllVisits());
                }
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
                if (currentUser != null
                    && currentUser.getRole() == User.Role.Doctor
                    && visit.getDoctorId() != currentUser.getUserId()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
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
        String action = ServletRequestUtils.trim(request.getParameter("action"));
        User currentUser = ServletRequestUtils.getCurrentUser(request);
        if ("delete".equalsIgnoreCase(action)) {
            deleteVisit(request, response, currentUser);
            return;
        }
        saveVisit(request, response, currentUser);
    }

    private void saveVisit(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws IOException, ServletException {
        String visitIdRaw = ServletRequestUtils.trim(request.getParameter("visitId"));
        String regNumber = ServletRequestUtils.trim(request.getParameter("regNumber"));
        String doctorIdRaw = ServletRequestUtils.trim(request.getParameter("doctorId"));
        String symptoms = ServletRequestUtils.trim(request.getParameter("symptoms"));
        boolean completed = "true".equalsIgnoreCase(ServletRequestUtils.trim(request.getParameter("completed")));

        int doctorId = 0;
        LocalDateTime visitDate = LocalDateTime.now();

        if (regNumber == null || regNumber.isBlank()) {
            forwardWithError(request, response, "Student is required.", visitIdRaw, regNumber, doctorIdRaw,
                             symptoms);
            return;
        }

        try {
            if (studentService.getStudentByRegNumber(regNumber).isEmpty()) {
                forwardWithError(request, response,
                                 "Cannot record visit: the student registration number does not exist.",
                                 visitIdRaw, regNumber, doctorIdRaw, symptoms);
                return;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet student lookup error", e);
            forwardWithError(request, response,
                             "Unable to validate student registration right now. Please try again.",
                             visitIdRaw, regNumber, doctorIdRaw, symptoms);
            return;
        }

        try {
            doctorId = Integer.parseInt(doctorIdRaw);
            if (doctorId <= 0) {
                throw new NumberFormatException("doctorId must be positive");
            }
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "A valid doctor is required.", visitIdRaw, regNumber, doctorIdRaw,
                             symptoms);
            return;
        }

        try {
            User doctor = userService.getUserById(doctorId).orElse(null);
            if (doctor == null || doctor.getRole() != User.Role.Doctor) {
                forwardWithError(request, response,
                                 "Selected doctor ID is invalid. Please choose an existing doctor.",
                                 visitIdRaw, regNumber, doctorIdRaw, symptoms);
                return;
            }
            if (currentUser != null
                && currentUser.getRole() == User.Role.Doctor
                && doctorId != currentUser.getUserId()) {
                forwardWithError(request, response,
                                 "You can only create or edit visits assigned to your own doctor account.",
                                 visitIdRaw, regNumber, doctorIdRaw, symptoms);
                return;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet doctor lookup error", e);
            forwardWithError(request, response,
                             "Unable to validate doctor information right now. Please try again.",
                             visitIdRaw, regNumber, doctorIdRaw, symptoms);
            return;
        }

        if (symptoms == null || symptoms.isBlank()) {
            forwardWithError(request, response, "Symptoms are required.", visitIdRaw, regNumber,
                             doctorIdRaw, symptoms);
            return;
        }

        MedicalVisit visit = new MedicalVisit();
        visit.setRegNumber(regNumber);
        visit.setDoctorId(doctorId);
        visit.setVisitDate(visitDate);
        visit.setSymptoms(symptoms);
        visit.setDiagnosis(null);
        visit.setCompleted(completed);

        try {
            if (visitIdRaw == null || visitIdRaw.isBlank()) {
                visitService.createVisit(visit);
                ServletRequestUtils.redirectWithMessage(request, response, "/visits", "success",
                                                       "Medical visit recorded successfully.");
                return;
            }

            int visitId = Integer.parseInt(visitIdRaw);
            MedicalVisit existingVisit = visitService.getVisitById(visitId).orElse(null);
            if (existingVisit == null) {
                ServletRequestUtils.redirectWithMessage(request, response, "/visits", "error",
                                                       "Visit record was not found.");
                return;
            }
            if (currentUser != null
                && currentUser.getRole() == User.Role.Doctor
                && existingVisit.getDoctorId() != currentUser.getUserId()) {
                ServletRequestUtils.redirectWithMessage(request, response, "/visits", "error",
                                                       "You cannot edit another doctor's visit.");
                return;
            }
            visit.setVisitId(visitId);
            visitService.updateVisit(visit);
            ServletRequestUtils.redirectWithMessage(request, response, "/visits", "success",
                                                   "Medical visit updated successfully.");
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "Invalid visit ID.", visitIdRaw, regNumber, doctorIdRaw,
                             symptoms);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet POST save error", e);
            ServletRequestUtils.redirectWithMessage(request, response, "/visits", "error",
                                                   "A system error occurred while saving the visit.");
        }
    }

    private void deleteVisit(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws IOException {
        String visitIdRaw = ServletRequestUtils.trim(request.getParameter("visitId"));
        if (visitIdRaw == null || visitIdRaw.isBlank()) {
            ServletRequestUtils.redirectWithMessage(request, response, "/visits", "error",
                                                   "Visit ID is required to delete.");
            return;
        }

        try {
            int visitId = Integer.parseInt(visitIdRaw);
            MedicalVisit existingVisit = visitService.getVisitById(visitId).orElse(null);
            if (existingVisit == null) {
                ServletRequestUtils.redirectWithMessage(request, response, "/visits", "error",
                                                       "Visit record was not found.");
                return;
            }
            if (currentUser != null
                && currentUser.getRole() == User.Role.Doctor
                && existingVisit.getDoctorId() != currentUser.getUserId()) {
                ServletRequestUtils.redirectWithMessage(request, response, "/visits", "error",
                                                       "You cannot delete another doctor's visit.");
                return;
            }
            visitService.deleteVisit(visitId);
            ServletRequestUtils.redirectWithMessage(request, response, "/visits", "success",
                                                   "Medical visit deleted successfully.");
        } catch (NumberFormatException e) {
            ServletRequestUtils.redirectWithMessage(request, response, "/visits", "error", "Invalid visit ID.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "VisitServlet POST delete error", e);
            ServletRequestUtils.redirectWithMessage(request, response, "/visits", "error",
                                                   "A system error occurred while deleting the visit.");
        }
    }

    private void forwardWithError(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String errorMessage,
                                  String visitIdRaw,
                                  String regNumber,
                                  String doctorIdRaw,
                                  String symptoms) throws ServletException, IOException {
        MedicalVisit visit = new MedicalVisit();
        boolean completed = "true".equalsIgnoreCase(ServletRequestUtils.trim(request.getParameter("completed")));
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

        // Visit date is controlled server-side and always set to today.
        visit.setVisitDate(LocalDateTime.now());

        visit.setRegNumber(regNumber);
        visit.setSymptoms(symptoms);
        visit.setDiagnosis(null);
        visit.setCompleted(completed);

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
}
