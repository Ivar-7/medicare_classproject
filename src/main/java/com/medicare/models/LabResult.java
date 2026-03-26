package com.medicare.models;

import java.time.LocalDate;

public class LabResult {

    private int       resultId;
    private int       requestId;
    private int       technicianId;
    private String    resultDetails;
    private LocalDate resultDate;

    // Transient display fields (populated by JOIN queries)
    private String technicianName;
    private String testName;
    private String studentName;

    public LabResult() { }

    public LabResult(int resultId, int requestId, int technicianId,
                     String resultDetails, LocalDate resultDate) {
        this.resultId      = resultId;
        this.requestId     = requestId;
        this.technicianId  = technicianId;
        this.resultDetails = resultDetails;
        this.resultDate    = resultDate;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int       getResultId()       { return resultId;       }
    public int       getRequestId()      { return requestId;      }
    public int       getTechnicianId()   { return technicianId;   }
    public String    getResultDetails()  { return resultDetails;  }
    public LocalDate getResultDate()     { return resultDate;     }
    public String    getTechnicianName() { return technicianName; }
    public String    getTestName()       { return testName;       }
    public String    getStudentName()    { return studentName;    }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setResultId(int resultId)                 { this.resultId       = resultId;       }
    public void setRequestId(int requestId)               { this.requestId      = requestId;      }
    public void setTechnicianId(int technicianId)         { this.technicianId   = technicianId;   }
    public void setResultDetails(String resultDetails)    { this.resultDetails  = resultDetails;  }
    public void setResultDate(LocalDate resultDate)       { this.resultDate     = resultDate;     }
    public void setTechnicianName(String technicianName)  { this.technicianName = technicianName; }
    public void setTestName(String testName)              { this.testName       = testName;       }
    public void setStudentName(String studentName)        { this.studentName    = studentName;    }

    @Override
    public String toString() {
        return "LabResult{resultId=" + resultId + ", requestId=" + requestId + ", resultDate=" + resultDate + "}";
    }
}
