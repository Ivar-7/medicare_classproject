package com.medicare.models;

import java.time.LocalDate;
import java.io.Serializable;

public class TreatmentNote implements Serializable {

    private int       noteId;
    private int       visitId;
    private String    clinicalNotes;
    private LocalDate followUpDate;

    public TreatmentNote() { }

    public TreatmentNote(int noteId, int visitId, String clinicalNotes, LocalDate followUpDate) {
        this.noteId        = noteId;
        this.visitId       = visitId;
        this.clinicalNotes = clinicalNotes;
        this.followUpDate  = followUpDate;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int       getNoteId()        { return noteId;        }
    public int       getVisitId()       { return visitId;       }
    public String    getClinicalNotes() { return clinicalNotes; }
    public LocalDate getFollowUpDate()  { return followUpDate;  }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setNoteId(int noteId)                  { this.noteId        = noteId;        }
    public void setVisitId(int visitId)                { this.visitId       = visitId;       }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }
    public void setFollowUpDate(LocalDate followUpDate){ this.followUpDate  = followUpDate;  }

    @Override
    public String toString() {
        return "TreatmentNote{noteId=" + noteId + ", visitId=" + visitId + "}";
    }
}
