package com.medicare.models;

public class Prescription {

    private int    prescriptionId;
    private int    visitId;
    private String medicineName;
    private String diagnosis;
    private String dosage;
    private String duration;

    // Transient display/validation field populated from visit JOIN
    private String studentRegNumber;

    public Prescription() { }

    public Prescription(int prescriptionId, int visitId,
                        String medicineName, String diagnosis, String dosage, String duration) {
        this.prescriptionId = prescriptionId;
        this.visitId        = visitId;
        this.medicineName   = medicineName;
        this.diagnosis      = diagnosis;
        this.dosage         = dosage;
        this.duration       = duration;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getPrescriptionId() { return prescriptionId; }
    public int    getVisitId()        { return visitId;        }
    public String getMedicineName()   { return medicineName;   }
    public String getDiagnosis()      { return diagnosis;      }
    public String getDosage()         { return dosage;         }
    public String getDuration()       { return duration;       }
    public String getStudentRegNumber() { return studentRegNumber; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setPrescriptionId(int prescriptionId)  { this.prescriptionId = prescriptionId; }
    public void setVisitId(int visitId)                { this.visitId        = visitId;        }
    public void setMedicineName(String medicineName)   { this.medicineName   = medicineName;   }
    public void setDiagnosis(String diagnosis)         { this.diagnosis      = diagnosis;      }
    public void setDosage(String dosage)               { this.dosage         = dosage;         }
    public void setDuration(String duration)           { this.duration       = duration;       }
    public void setStudentRegNumber(String studentRegNumber) { this.studentRegNumber = studentRegNumber; }

    @Override
    public String toString() {
        return "Prescription{prescriptionId=" + prescriptionId + ", visitId=" + visitId
               + ", medicineName='" + medicineName + "'}";
    }
}
