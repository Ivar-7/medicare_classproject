package com.medicare.features.notes.services;

import com.medicare.features.notes.dao.TreatmentNoteDAO;
import com.medicare.models.TreatmentNote;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TreatmentNoteService {

    private final TreatmentNoteDAO noteDAO = new TreatmentNoteDAO();

    public List<TreatmentNote> getAllNotes() throws SQLException {
        return noteDAO.findAll();
    }

    public Optional<TreatmentNote> getNoteById(int id) throws SQLException {
        return noteDAO.findById(id);
    }

    public boolean noteExists(int id) throws SQLException {
        return noteDAO.existsById(id);
    }

    public Optional<Integer> getNoteVisitId(int id) throws SQLException {
        return noteDAO.findVisitIdByNoteId(id);
    }

    public List<TreatmentNote> getNotesByVisit(int visitId) throws SQLException {
        return noteDAO.findByVisit(visitId);
    }

    public void createNote(TreatmentNote note) throws SQLException {
        noteDAO.save(note);
    }

    public void updateNote(TreatmentNote note) throws SQLException {
        noteDAO.update(note);
    }

    public void deleteNote(int id) throws SQLException {
        noteDAO.delete(id);
    }

    public int countNotes() throws SQLException {
        return noteDAO.count();
    }
}
