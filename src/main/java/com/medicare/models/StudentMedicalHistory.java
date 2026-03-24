package com.medicare.models;

import java.util.ArrayList;
import java.util.List;

public class StudentMedicalHistory {

    private Student student;
    private List<MedicalVisit> visits = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    private List<TreatmentNote> labHistory = new ArrayList<>();
    private List<String> diseases = new ArrayList<>();

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<MedicalVisit> getVisits() {
        return visits;
    }

    public void setVisits(List<MedicalVisit> visits) {
        this.visits = visits;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public List<TreatmentNote> getLabHistory() {
        return labHistory;
    }

    public void setLabHistory(List<TreatmentNote> labHistory) {
        this.labHistory = labHistory;
    }

    public List<String> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<String> diseases) {
        this.diseases = diseases;
    }
}
