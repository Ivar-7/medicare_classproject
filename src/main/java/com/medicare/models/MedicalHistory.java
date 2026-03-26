package com.medicare.models;

import java.time.LocalDate;

public class MedicalHistory {

    private int       historyId;
    private String    regNumber;
    private String    conditionName;
    private LocalDate diagnosisDate;
    private int       doctorId;

    // Transient display fields (populated by JOIN queries)
    private String studentName;
    private String doctorName;

    public MedicalHistory() { }

    public MedicalHistory(int historyId, String regNumber, String conditionName,
                          LocalDate diagnosisDate, int doctorId) {
        this.historyId     = historyId;
        this.regNumber     = regNumber;
        this.conditionName = conditionName;
        this.diagnosisDate = diagnosisDate;
        this.doctorId      = doctorId;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int       getHistoryId()     { return historyId;     }
    public String    getRegNumber()     { return regNumber;     }
    public String    getConditionName() { return conditionName; }
    public LocalDate getDiagnosisDate() { return diagnosisDate; }
    public int       getDoctorId()      { return doctorId;      }
    public String    getStudentName()   { return studentName;   }
    public String    getDoctorName()    { return doctorName;    }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setHistoryId(int historyId)              { this.historyId     = historyId;     }
    public void setRegNumber(String regNumber)           { this.regNumber     = regNumber;     }
    public void setConditionName(String conditionName)   { this.conditionName = conditionName; }
    public void setDiagnosisDate(LocalDate diagnosisDate){ this.diagnosisDate = diagnosisDate; }
    public void setDoctorId(int doctorId)                { this.doctorId      = doctorId;      }
    public void setStudentName(String studentName)       { this.studentName   = studentName;   }
    public void setDoctorName(String doctorName)         { this.doctorName    = doctorName;    }

    @Override
    public String toString() {
        return "MedicalHistory{historyId=" + historyId + ", regNumber='" + regNumber + "', conditionName='" + conditionName + "'}";
    }
}
