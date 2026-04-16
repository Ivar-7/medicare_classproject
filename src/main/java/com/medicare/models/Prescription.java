package com.medicare.models;

import java.time.LocalDate;
import java.io.Serializable;

public class Prescription implements Serializable {

  private int prescriptionId;
  private int visitId;
  private String medicineName;
  private String dosage;
  private String frequency;
  private String duration;
  private String instructions;
  private LocalDate prescribedDate;
  private LocalDate createdAt;
  private LocalDate updatedAt;

  // Transient display/validation field populated from visit JOIN
  private int studentRegNumber;

  public Prescription() {
  }

  public Prescription(int prescriptionId, int visitId,
      String medicineName, String dosage, String frequency, String duration,
      String instructions, LocalDate prescribedDate, LocalDate createdAt, LocalDate updatedAt) {
    this.prescriptionId = prescriptionId;
    this.visitId = visitId;
    this.medicineName = medicineName;
    this.dosage = dosage;
    this.frequency = frequency;
    this.duration = duration;
    this.instructions = instructions;
    this.prescribedDate = prescribedDate;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ── Getters ──────────────────────────────────────────────────────────────

  public int getPrescriptionId() {
    return prescriptionId;
  }

  public int getVisitId() {
    return visitId;
  }

  public String getMedicineName() {
    return medicineName;
  }

  public String getDosage() {
    return dosage;
  }

  public String getFrequency() {
    return frequency;
  }

  public String getDuration() {
    return duration;
  }

  public String getInstructions() {
    return instructions;
  }

  public LocalDate getPrescribedDate() {
    return prescribedDate;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public LocalDate getUpdatedAt() {
    return updatedAt;
  }

  public int getStudentRegNumber() {
    return studentRegNumber;
  }

  // ── Setters ──────────────────────────────────────────────────────────────

  public void setPrescriptionId(int prescriptionId) {
    this.prescriptionId = prescriptionId;
  }

  public void setVisitId(int visitId) {
    this.visitId = visitId;
  }

  public void setMedicineName(String medicineName) {
    this.medicineName = medicineName;
  }

  public void setDosage(String dosage) {
    this.dosage = dosage;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public void setPrescribedDate(LocalDate prescribedDate) {
    this.prescribedDate = prescribedDate;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDate updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setStudentRegNumber(int studentRegNumber) {
    this.studentRegNumber = studentRegNumber;
  }

  @Override
  public String toString() {
    return "Prescription{prescriptionId=" + prescriptionId + ", visitId=" + visitId
        + ", medicineName='" + medicineName + "'}";
  }
}
