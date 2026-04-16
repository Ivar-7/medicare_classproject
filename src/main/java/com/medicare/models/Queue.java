package com.medicare.models;

import java.time.LocalDate;
import java.io.Serializable;

public class Queue implements Serializable {

    public enum Status { Waiting, InConsultation, Completed, Cancelled }
    public enum PriorityLevel { Low, Medium, High, Emergency }

    private int           queueId;
    private int           regNumber;
    private int           visitId;
    private LocalDate     queueDate;
    private PriorityLevel priorityLevel;
    private Status        status;
    private int           positionNumber;
    private LocalDate     createdAt;
    private LocalDate     updatedAt;

    private String studentName;

    public Queue() { }

    public Queue(int queueId, int regNumber, int visitId, LocalDate queueDate,
                 PriorityLevel priorityLevel, Status status, int positionNumber,
                 LocalDate createdAt, LocalDate updatedAt) {
        this.queueId = queueId;
        this.regNumber = regNumber;
        this.visitId = visitId;
        this.queueDate = queueDate;
        this.priorityLevel = priorityLevel;
        this.status = status;
        this.positionNumber = positionNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int           getQueueId()       { return queueId; }
    public int           getRegNumber()     { return regNumber; }
    public int           getVisitId()       { return visitId; }
    public LocalDate     getQueueDate()     { return queueDate; }
    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public Status        getStatus()        { return status; }
    public int           getPositionNumber() { return positionNumber; }
    public LocalDate     getCreatedAt()     { return createdAt; }
    public LocalDate     getUpdatedAt()     { return updatedAt; }
    public String        getStudentName()   { return studentName; }

    public String getStatusName() { return status != null ? status.name() : ""; }
    public String getPriorityLevelName() { return priorityLevel != null ? priorityLevel.name() : ""; }

    public void setQueueId(int queueId)                     { this.queueId = queueId; }
    public void setRegNumber(int regNumber)                 { this.regNumber = regNumber; }
    public void setVisitId(int visitId)                     { this.visitId = visitId; }
    public void setQueueDate(LocalDate queueDate)           { this.queueDate = queueDate; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }
    public void setStatus(Status status)                    { this.status = status; }
    public void setPositionNumber(int positionNumber)       { this.positionNumber = positionNumber; }
    public void setCreatedAt(LocalDate createdAt)           { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDate updatedAt)           { this.updatedAt = updatedAt; }
    public void setStudentName(String studentName)          { this.studentName = studentName; }

    @Override
    public String toString() {
        return "Queue{queueId=" + queueId + ", regNumber=" + regNumber + ", status=" + status + "}";
    }
}
