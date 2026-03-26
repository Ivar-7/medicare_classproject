package com.medicare.features.prescriptions.servlets;

import com.medicare.features.prescriptions.services.PrescriptionService;
import com.medicare.features.visits.services.MedicalVisitService;
import com.medicare.models.MedicalVisit;
import com.medicare.models.Prescription;
import com.medicare.models.User;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/prescriptions", "/prescriptions/*"})
public class PrescriptionServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PrescriptionServlet.class.getName());
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final MedicalVisitService visitService = new MedicalVisitService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        ServletRequestUtils.exposeAlertsFromQuery(request);
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                User currentUser = ServletRequestUtils.getCurrentUser(request);
                if (currentUser != null && currentUser.getRole() == User.Role.Doctor) {
                    request.setAttribute("prescriptions",
                                         prescriptionService.getPrescriptionsByDoctor(currentUser.getUserId()));
                } else {
                    request.setAttribute("prescriptions", prescriptionService.getAllPrescriptions());
                }
                request.getRequestDispatcher("/WEB-INF/views/prescriptions/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                Prescription prescription = new Prescription();
                String visitIdRaw = ServletRequestUtils.trim(request.getParameter("visitId"));
                if (visitIdRaw != null && visitIdRaw.matches("\\d+")) {
                    int visitId = Integer.parseInt(visitIdRaw);
                    prescription.setVisitId(visitId);
                    MedicalVisit visit = visitService.getVisitById(Integer.parseInt(visitIdRaw)).orElse(null);
                    if (visit != null) {
                        prescription.setStudentRegNumber(visit.getRegNumber());
                    }
                }
                request.setAttribute("prescription", prescription);
                request.getRequestDispatcher("/WEB-INF/views/prescriptions/form.jsp").forward(request, response);
            } else if (pathInfo.startsWith("/delete/")) {
                int id = Integer.parseInt(pathInfo.substring(8));
                User currentUser = ServletRequestUtils.getCurrentUser(request);
                Prescription existing = prescriptionService.getPrescriptionById(id).orElse(null);
                if (existing == null) {
                    ServletRequestUtils.redirectWithMessage(request, response, "/prescriptions", "error",
                                                           "Prescription was not found.");
                    return;
                }
                if (!canManagePrescription(currentUser, existing.getVisitId())) {
                    ServletRequestUtils.redirectWithMessage(request, response, "/prescriptions", "error",
                                                           "You cannot delete prescriptions for other doctors' visits.");
                    return;
                }
                prescriptionService.deletePrescription(id);
                ServletRequestUtils.redirectWithMessage(request, response, "/prescriptions", "success",
                                                       "Prescription deleted successfully.");
                return;
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Prescription prescription = prescriptionService.getPrescriptionById(id).orElse(null);
                if (prescription == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                User currentUser = ServletRequestUtils.getCurrentUser(request);
                if (!canManagePrescription(currentUser, prescription.getVisitId())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                request.setAttribute("prescription", prescription);
                request.getRequestDispatcher("/WEB-INF/views/prescriptions/form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "PrescriptionServlet GET error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String visitIdRaw = ServletRequestUtils.trim(request.getParameter("visitId"));
        String prescriptionIdRaw = ServletRequestUtils.trim(request.getParameter("prescriptionId"));
        String studentRegNumber = ServletRequestUtils.trim(request.getParameter("studentRegNumber"));
        String medicineName = ServletRequestUtils.trim(request.getParameter("medicineName"));
        String diagnosis = ServletRequestUtils.trim(request.getParameter("diagnosis"));
        String dosage = ServletRequestUtils.trim(request.getParameter("dosage"));
        String duration = ServletRequestUtils.trim(request.getParameter("duration"));

        if (visitIdRaw == null || !visitIdRaw.matches("\\d+")) {
            forwardWithError(request, response, "Visit ID must be a positive number.",
                             prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                             duration);
            return;
        }
        if (studentRegNumber == null || studentRegNumber.isBlank()) {
            forwardWithError(request, response, "Student ID is required.",
                             prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                             duration);
            return;
        }
        if (medicineName == null || medicineName.isBlank()) {
            forwardWithError(request, response, "Medicine name is required.",
                             prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                             duration);
            return;
        }
        if (diagnosis == null || diagnosis.isBlank()) {
            forwardWithError(request, response, "Diagnosis is required.",
                             prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                             duration);
            return;
        }
        if (dosage == null || dosage.isBlank()) {
            forwardWithError(request, response, "Dosage is required.",
                             prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                             duration);
            return;
        }
        if (duration == null || duration.isBlank()) {
            forwardWithError(request, response, "Duration is required.",
                             prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                             duration);
            return;
        }

        try {
            int visitId = Integer.parseInt(visitIdRaw);
            MedicalVisit visit = visitService.getVisitById(visitId).orElse(null);
            if (visit == null) {
                forwardWithError(request, response, "Visit ID does not exist.",
                                 prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                                 duration);
                return;
            }
            if (!studentRegNumber.equals(visit.getRegNumber())) {
                forwardWithError(request, response,
                                 "Student ID does not match the selected visit.",
                                 prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                                 duration);
                return;
            }

            User currentUser = ServletRequestUtils.getCurrentUser(request);
            if (!canManagePrescription(currentUser, visitId)) {
                ServletRequestUtils.redirectWithMessage(request, response, "/prescriptions", "error",
                                                       "You can only prescribe for your own visits.");
                return;
            }

            int prescriptionId = (prescriptionIdRaw != null && prescriptionIdRaw.matches("\\d+"))
                                 ? Integer.parseInt(prescriptionIdRaw)
                                 : 0;

            Prescription prescription = new Prescription(prescriptionId, visitId, medicineName, diagnosis, dosage,
                                                         duration);

            if (prescriptionId > 0) {
                Prescription existing = prescriptionService.getPrescriptionById(prescriptionId).orElse(null);
                if (existing == null) {
                    ServletRequestUtils.redirectWithMessage(request, response, "/prescriptions", "error",
                                                           "Prescription was not found.");
                    return;
                }
                if (!canManagePrescription(currentUser, existing.getVisitId())) {
                    ServletRequestUtils.redirectWithMessage(request, response, "/prescriptions", "error",
                                                           "You cannot update prescriptions for other doctors' visits.");
                    return;
                }
                prescriptionService.updatePrescription(prescription);
                ServletRequestUtils.redirectWithMessage(request, response, "/prescriptions", "success",
                                                       "Prescription updated successfully.");
                return;
            }

            prescriptionService.createPrescription(prescription);
            ServletRequestUtils.redirectWithMessage(request, response, "/prescriptions", "success",
                                                   "Prescription created successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "PrescriptionServlet POST error", e);
            forwardWithError(request, response,
                             "A system error occurred while saving the prescription.",
                             prescriptionIdRaw, visitIdRaw, studentRegNumber, medicineName, diagnosis, dosage,
                             duration);
        }
    }

    private void forwardWithError(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String errorMessage,
                                  String prescriptionIdRaw,
                                  String visitIdRaw,
                                  String studentRegNumber,
                                  String medicineName,
                                  String diagnosis,
                                  String dosage,
                                  String duration) throws ServletException, IOException {
        Prescription prescription = new Prescription();
        if (prescriptionIdRaw != null && prescriptionIdRaw.matches("\\d+")) {
            prescription.setPrescriptionId(Integer.parseInt(prescriptionIdRaw));
        }
        if (visitIdRaw != null && visitIdRaw.matches("\\d+")) {
            prescription.setVisitId(Integer.parseInt(visitIdRaw));
        }
        prescription.setStudentRegNumber(studentRegNumber);
        prescription.setMedicineName(medicineName);
        prescription.setDiagnosis(diagnosis);
        prescription.setDosage(dosage);
        prescription.setDuration(duration);

        request.setAttribute("error", errorMessage);
        request.setAttribute("prescription", prescription);
        request.getRequestDispatcher("/WEB-INF/views/prescriptions/form.jsp").forward(request, response);
    }

    private boolean canManagePrescription(User currentUser, int visitId) throws Exception {
        if (currentUser == null) {
            return false;
        }
        if (currentUser.getRole() == User.Role.Admin) {
            return true;
        }
        MedicalVisit visit = visitService.getVisitById(visitId).orElse(null);
        return visit != null && visit.getDoctorId() == currentUser.getUserId();
    }
}