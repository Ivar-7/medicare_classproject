package com.medicare.features.history.services;

import com.medicare.features.notes.services.TreatmentNoteService;
import com.medicare.features.prescriptions.services.PrescriptionService;
import com.medicare.features.students.services.StudentService;
import com.medicare.features.visits.services.MedicalVisitService;
import com.medicare.models.MedicalVisit;
import com.medicare.models.Prescription;
import com.medicare.models.Student;
import com.medicare.models.TreatmentNote;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for retrieving aggregated medical history for students.
 * Student history is derived from related models: MedicalVisit, Prescription, and TreatmentNote.
 */
public class MedicalHistoryService {

  private final StudentService studentService = new StudentService();
  private final MedicalVisitService visitService = new MedicalVisitService();
  private final PrescriptionService prescriptionService = new PrescriptionService();
  private final TreatmentNoteService noteService = new TreatmentNoteService();

  /**
   * Retrieves aggregated medical history for a student.
   * 
   * @param regNumber Student registration number
   * @param includeDiseases Unused parameter for future enhancement
   * @return Optional containing a map with keys: "student", "visits", "prescriptions", "labHistory"
   * @throws SQLException if database access error occurs
   */
  public Optional<Map<String, Object>> getStudentHistory(String regNumber, boolean includeDiseases)
      throws SQLException {
    int regNum = Integer.parseInt(regNumber);
    Optional<Student> studentOpt = studentService.getStudentByRegNumber(regNum);
    if (studentOpt.isEmpty()) {
      return Optional.empty();
    }

    Map<String, Object> history = new HashMap<>();
    history.put("student", studentOpt.get());

    List<MedicalVisit> visits = visitService.getVisitsByStudent(regNum);
    history.put("visits", visits);

    List<Prescription> allPrescriptions = new ArrayList<>();
    List<TreatmentNote> allLabNotes = new ArrayList<>();

    for (MedicalVisit visit : visits) {
      List<Prescription> visitPrescriptions = prescriptionService.getPrescriptionsByVisit(visit.getVisitId());
      allPrescriptions.addAll(visitPrescriptions);
      allLabNotes.addAll(noteService.getNotesByVisit(visit.getVisitId()));
    }

    history.put("prescriptions", allPrescriptions);
    history.put("labHistory", allLabNotes);

    return Optional.of(history);
  }
}
