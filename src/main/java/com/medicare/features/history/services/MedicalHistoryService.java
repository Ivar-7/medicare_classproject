package com.medicare.features.history.services;

import com.medicare.features.notes.services.TreatmentNoteService;
import com.medicare.features.prescriptions.services.PrescriptionService;
import com.medicare.features.students.services.StudentService;
import com.medicare.features.visits.services.MedicalVisitService;
import com.medicare.models.MedicalVisit;
import com.medicare.models.Prescription;
import com.medicare.models.Student;
import com.medicare.models.StudentMedicalHistory;
import com.medicare.models.TreatmentNote;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MedicalHistoryService {

    private final StudentService studentService = new StudentService();
    private final MedicalVisitService visitService = new MedicalVisitService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final TreatmentNoteService noteService = new TreatmentNoteService();

    public Optional<StudentMedicalHistory> getStudentHistory(String regNumber, boolean includeDiseases) throws SQLException {
        Optional<Student> studentOpt = studentService.getStudentByRegNumber(regNumber);
        if (studentOpt.isEmpty()) {
            return Optional.empty();
        }

        StudentMedicalHistory history = new StudentMedicalHistory();
        history.setStudent(studentOpt.get());

        List<MedicalVisit> visits = visitService.getVisitsByStudent(regNumber);
        history.setVisits(visits);

        List<Prescription> allPrescriptions = new ArrayList<>();
        List<TreatmentNote> allLabNotes = new ArrayList<>();

        Set<String> diseases = new LinkedHashSet<>();

        for (MedicalVisit visit : visits) {
            List<Prescription> visitPrescriptions = prescriptionService.getPrescriptionsByVisit(visit.getVisitId());
            allPrescriptions.addAll(visitPrescriptions);
            allLabNotes.addAll(noteService.getNotesByVisit(visit.getVisitId()));

            if (includeDiseases) {
                for (Prescription prescription : visitPrescriptions) {
                    if (prescription.getDisease() != null && !prescription.getDisease().isBlank()) {
                        diseases.add(prescription.getDisease().trim());
                    }
                }
            }
        }

        history.setPrescriptions(allPrescriptions);
        history.setLabHistory(allLabNotes);
        history.setDiseases(new ArrayList<>(diseases));

        return Optional.of(history);
    }
}
