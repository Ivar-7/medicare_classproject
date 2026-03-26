package com.medicare.models;

import java.time.LocalDate;

public class Queue {

    public enum Status { Waiting, InConsultation, Completed }
    public enum PriorityLevel { Low, Medium, High }

    private int           queueId;
    private String        regNumber;
    private LocalDate     visitDate;
    private PriorityLevel priorityLevel;
    private Status        status;

    // Transient display field (populated from JOIN queries)
    private String studentName;

    public Queue() { }

    public Queue(int queueId, String regNumber, LocalDate visitDate,
                 PriorityLevel priorityLevel, Status status) {
        this.queueId       = queueId;
        this.regNumber     = regNumber;
        this.visitDate     = visitDate;
        this.priorityLevel = priorityLevel;
        this.status        = status;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int           getQueueId()       { return queueId;       }
    public String        getRegNumber()     { return regNumber;     }
    public LocalDate     getVisitDate()     { return visitDate;     }
    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public Status        getStatus()        { return status;        }
    public String        getStudentName()   { return studentName;   }

    /** Convenience method for EL comparisons: ${queue.statusName == 'Waiting'} */
    public String getStatusName() { return status != null ? status.name() : ""; }

    /** Convenience method for EL comparisons: ${queue.priorityLevelName == 'High'} */
    public String getPriorityLevelName() { return priorityLevel != null ? priorityLevel.name() : ""; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setQueueId(int queueId)                     { this.queueId       = queueId;       }
    public void setRegNumber(String regNumber)              { this.regNumber     = regNumber;     }
    public void setVisitDate(LocalDate visitDate)           { this.visitDate     = visitDate;     }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }
    public void setStatus(Status status)                    { this.status        = status;        }
    public void setStudentName(String studentName)          { this.studentName   = studentName;   }

    @Override
    public String toString() {
        return "Queue{queueId=" + queueId + ", regNumber='" + regNumber + "', status=" + status + "}";
    }
}
