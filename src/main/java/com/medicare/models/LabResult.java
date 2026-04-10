package com.medicare.models;

import java.time.LocalDate;

public class LabResult {

  private int resultId;
  private int patientId;
  private int technicianId;
  private int requestId;
  private String resultDetails;
  private String resultStatus;
  private String resultValue;
  private String remarks;
  private LocalDate createdAt;
  private LocalDate updatedAt;

  // Transient display fields (populated by JOIN queries)
  private String technicianName;
  private String testName;
  private String studentName;

  public LabResult() {
  }

  public LabResult(int resultId, int patientId, int technicianId, int requestId,
      String resultDetails, String resultStatus, String resultValue, String remarks,
      LocalDate createdAt, LocalDate updatedAt) {
    this.resultId = resultId;
    this.patientId = patientId;
    this.technicianId = technicianId;
    this.requestId = requestId;
    this.resultDetails = resultDetails;
    this.resultStatus = resultStatus;
    this.resultValue = resultValue;
    this.remarks = remarks;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ── Getters ──────────────────────────────────────────────────────────────

  public int getResultId() {
    return resultId;
  }

  public int getPatientId() {
    return patientId;
  }

  public int getTechnicianId() {
    return technicianId;
  }

  public int getRequestId() {
    return requestId;
  }

  public String getResultDetails() {
    return resultDetails;
  }

  public String getResultStatus() {
    return resultStatus;
  }

  public String getResultValue() {
    return resultValue;
  }

  public String getRemarks() {
    return remarks;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public LocalDate getUpdatedAt() {
    return updatedAt;
  }

  public String getTechnicianName() {
    return technicianName;
  }

  public String getTestName() {
    return testName;
  }

  public String getStudentName() {
    return studentName;
  }

  // ── Setters ──────────────────────────────────────────────────────────────

  public void setResultId(int resultId) {
    this.resultId = resultId;
  }

  public void setPatientId(int patientId) {
    this.patientId = patientId;
  }

  public void setTechnicianId(int technicianId) {
    this.technicianId = technicianId;
  }

  public void setRequestId(int requestId) {
    this.requestId = requestId;
  }

  public void setResultDetails(String resultDetails) {
    this.resultDetails = resultDetails;
  }

  public void setResultStatus(String resultStatus) {
    this.resultStatus = resultStatus;
  }

  public void setResultValue(String resultValue) {
    this.resultValue = resultValue;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDate updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setTechnicianName(String technicianName) {
    this.technicianName = technicianName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public void setStudentName(String studentName) {
    this.studentName = studentName;
  }

  @Override
  public String toString() {
    return "LabResult{resultId=" + resultId + ", patientId=" + patientId + ", createdAt=" + createdAt + "}";
  }
}
