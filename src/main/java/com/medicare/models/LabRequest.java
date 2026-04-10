package com.medicare.models;

import java.time.LocalDate;

public class LabRequest {

  public enum Status {
    Pending, InProgress, Completed, Cancelled
  }

  public enum Priority {
    Routine, Urgent
  }

  private int requestId;
  private int visitId;
  private int requestedBy;
  private String testName;
  private String testDescription;
  private LocalDate requestDate;
  private Status status;
  private Priority priority;
  private LocalDate createdAt;
  private LocalDate updatedAt;

  // Transient display fields (populated by JOIN queries)
  private int studentRegNumber;
  private String studentName;

  public LabRequest() {
  }

  public LabRequest(int requestId, int visitId, int requestedBy, String testName, String testDescription,
      LocalDate requestDate, Status status, Priority priority, LocalDate createdAt, LocalDate updatedAt) {
    this.requestId = requestId;
    this.visitId = visitId;
    this.requestedBy = requestedBy;
    this.testName = testName;
    this.testDescription = testDescription;
    this.requestDate = requestDate;
    this.status = status;
    this.priority = priority;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ── Getters ──────────────────────────────────────────────────────────────

  public int getRequestId() {
    return requestId;
  }

  public int getVisitId() {
    return visitId;
  }

  public int getRequestedBy() {
    return requestedBy;
  }

  public String getTestName() {
    return testName;
  }

  public String getTestDescription() {
    return testDescription;
  }

  public LocalDate getRequestDate() {
    return requestDate;
  }

  public Status getStatus() {
    return status;
  }

  public Priority getPriority() {
    return priority;
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

  public String getStudentName() {
    return studentName;
  }

  /**
   * Convenience method for EL comparisons: ${labRequest.statusName == 'Pending'}
   */
  public String getStatusName() {
    return status != null ? status.name() : "";
  }

  // ── Setters ──────────────────────────────────────────────────────────────

  public void setRequestId(int requestId) {
    this.requestId = requestId;
  }

  public void setVisitId(int visitId) {
    this.visitId = visitId;
  }

  public void setRequestedBy(int requestedBy) {
    this.requestedBy = requestedBy;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public void setTestDescription(String testDescription) {
    this.testDescription = testDescription;
  }

  public void setRequestDate(LocalDate requestDate) {
    this.requestDate = requestDate;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
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

  public void setStudentName(String studentName) {
    this.studentName = studentName;
  }

  @Override
  public String toString() {
    return "LabRequest{requestId=" + requestId + ", visitId=" + visitId + ", testName='" + testName + "', status="
        + status + "}";
  }
}
