package com.medicare.models;

import java.time.LocalDate;

public class LabRequest {

    public enum Status { Pending, InProgress, Completed }

    private int       requestId;
    private int       visitId;
    private String    testName;
    private LocalDate requestDate;
    private Status    status;

    // Transient display fields (populated by JOIN queries)
    private String studentRegNumber;
    private String studentName;

    public LabRequest() { }

    public LabRequest(int requestId, int visitId, String testName,
                      LocalDate requestDate, Status status) {
        this.requestId   = requestId;
        this.visitId     = visitId;
        this.testName    = testName;
        this.requestDate = requestDate;
        this.status      = status;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int       getRequestId()        { return requestId;        }
    public int       getVisitId()          { return visitId;          }
    public String    getTestName()         { return testName;         }
    public LocalDate getRequestDate()      { return requestDate;      }
    public Status    getStatus()           { return status;           }
    public String    getStudentRegNumber() { return studentRegNumber; }
    public String    getStudentName()      { return studentName;      }

    /** Convenience method for EL comparisons: ${labRequest.statusName == 'Pending'} */
    public String getStatusName() { return status != null ? status.name() : ""; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setRequestId(int requestId)                 { this.requestId        = requestId;        }
    public void setVisitId(int visitId)                     { this.visitId          = visitId;          }
    public void setTestName(String testName)                { this.testName         = testName;         }
    public void setRequestDate(LocalDate requestDate)       { this.requestDate      = requestDate;      }
    public void setStatus(Status status)                    { this.status           = status;           }
    public void setStudentRegNumber(String studentRegNumber){ this.studentRegNumber = studentRegNumber; }
    public void setStudentName(String studentName)          { this.studentName      = studentName;      }

    @Override
    public String toString() {
        return "LabRequest{requestId=" + requestId + ", visitId=" + visitId + ", testName='" + testName + "', status=" + status + "}";
    }
}
