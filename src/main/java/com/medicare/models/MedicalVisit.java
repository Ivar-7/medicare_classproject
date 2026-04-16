package com.medicare.models;

import java.time.LocalDate;
import java.io.Serializable;

public class MedicalVisit implements Serializable {

  public enum Status {
    Scheduled, Ongoing, Completed
  }

  private int visitId;
  private int regNumber;
  private int doctorId;
  private LocalDate visitDate;
  private String symptoms;
  private String diagnosis;
  private String notes;
  private Status status;
  private LocalDate createdAt;
  private LocalDate updatedAt;

  // Transient display fields (populated by JOIN queries)
  private String studentName;
  private String doctorName;

  public MedicalVisit() {
  }

  public MedicalVisit(int visitId, int regNumber, int doctorId,
      LocalDate visitDate, String symptoms, String diagnosis, String notes,
      Status status, LocalDate createdAt, LocalDate updatedAt) {
    this.visitId = visitId;
    this.regNumber = regNumber;
    this.doctorId = doctorId;
    this.visitDate = visitDate;
    this.symptoms = symptoms;
    this.diagnosis = diagnosis;
    this.notes = notes;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ── Getters ──────────────────────────────────────────────────────────────

  public int getVisitId() {
    return visitId;
  }

  public int getRegNumber() {
    return regNumber;
  }

  public int getDoctorId() {
    return doctorId;
  }

  public LocalDate getVisitDate() {
    return visitDate;
  }

  public String getSymptoms() {
    return symptoms;
  }

  public String getDiagnosis() {
    return diagnosis;
  }

  public String getNotes() {
    return notes;
  }

  public Status getStatus() {
    return status;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public LocalDate getUpdatedAt() {
    return updatedAt;
  }

  public String getStudentName() {
    return studentName;
  }

  public String getDoctorName() {
    return doctorName;
  }

  public String getStatusName() {
    return status != null ? status.name() : "";
  }

  public String getVisitDateInput() {
    if (visitDate == null) {
      return "";
    }
    return visitDate.toString();
  }

  // ── Setters ──────────────────────────────────────────────────────────────

  public void setVisitId(int visitId) {
    this.visitId = visitId;
  }

  public void setRegNumber(int regNumber) {
    this.regNumber = regNumber;
  }

  public void setDoctorId(int doctorId) {
    this.doctorId = doctorId;
  }

  public void setVisitDate(LocalDate visitDate) {
    this.visitDate = visitDate;
  }

  public void setSymptoms(String symptoms) {
    this.symptoms = symptoms;
  }

  public void setDiagnosis(String diagnosis) {
    this.diagnosis = diagnosis;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDate updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setStudentName(String studentName) {
    this.studentName = studentName;
  }

  public void setDoctorName(String doctorName) {
    this.doctorName = doctorName;
  }

  @Override
  public String toString() {
    return "MedicalVisit{visitId=" + visitId + ", regNumber=" + regNumber + ", visitDate=" + visitDate + "}";
  }
}
