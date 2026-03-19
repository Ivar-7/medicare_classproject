package com.medicare.models;

import java.time.LocalDateTime;

public class MedicalVisit {

    private int           visitId;
    private String        regNumber;
    private int           doctorId;
    private LocalDateTime visitDate;
    private String        symptoms;
    private String        diagnosis;

    // Transient display fields (populated by JOIN queries)
    private String studentName;
    private String doctorName;

    public MedicalVisit() { }

    public MedicalVisit(int visitId, String regNumber, int doctorId,
                        LocalDateTime visitDate, String symptoms, String diagnosis) {
        this.visitId   = visitId;
        this.regNumber = regNumber;
        this.doctorId  = doctorId;
        this.visitDate = visitDate;
        this.symptoms  = symptoms;
        this.diagnosis = diagnosis;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int           getVisitId()     { return visitId;     }
    public String        getRegNumber()   { return regNumber;   }
    public int           getDoctorId()    { return doctorId;    }
    public LocalDateTime getVisitDate()   { return visitDate;   }
    public String        getSymptoms()    { return symptoms;    }
    public String        getDiagnosis()   { return diagnosis;   }
    public String        getStudentName() { return studentName; }
    public String        getDoctorName()  { return doctorName;  }
    public String        getVisitDateInput() {
        if (visitDate == null) {
            return "";
        }
        return visitDate.withSecond(0).withNano(0).toString();
    }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setVisitId(int visitId)              { this.visitId     = visitId;     }
    public void setRegNumber(String regNumber)        { this.regNumber   = regNumber;   }
    public void setDoctorId(int doctorId)             { this.doctorId    = doctorId;    }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate   = visitDate;   }
    public void setSymptoms(String symptoms)          { this.symptoms    = symptoms;    }
    public void setDiagnosis(String diagnosis)        { this.diagnosis   = diagnosis;   }
    public void setStudentName(String studentName)    { this.studentName = studentName; }
    public void setDoctorName(String doctorName)      { this.doctorName  = doctorName;  }

    @Override
    public String toString() {
        return "MedicalVisit{visitId=" + visitId + ", regNumber='" + regNumber + "', visitDate=" + visitDate + "}";
    }
}
