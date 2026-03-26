package com.medicare.features.notes.servlets;

import com.medicare.features.notes.services.TreatmentNoteService;
import com.medicare.models.TreatmentNote;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/notes", "/notes/*"})
public class NoteServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(NoteServlet.class.getName());

    private final TreatmentNoteService noteService = new TreatmentNoteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        ServletRequestUtils.exposeAlertsFromQuery(request);
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                request.setAttribute("notes", noteService.getAllNotes());
                request.getRequestDispatcher("/WEB-INF/views/notes/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                request.getRequestDispatcher("/WEB-INF/views/notes/form.jsp").forward(request, response);
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                TreatmentNote note = noteService.getNoteById(id).orElse(null);
                if (note == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("note", note);
                request.getRequestDispatcher("/WEB-INF/views/notes/form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "NoteServlet GET error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ServletRequestUtils.trim(request.getParameter("action"));
        if ("delete".equalsIgnoreCase(action)) {
            deleteNote(request, response);
            return;
        }
        saveNote(request, response);
    }

    private void saveNote(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String noteIdRaw = ServletRequestUtils.trim(request.getParameter("noteId"));
        String visitIdRaw = ServletRequestUtils.trim(request.getParameter("visitId"));
        String clinicalNotes = ServletRequestUtils.trim(request.getParameter("clinicalNotes"));
        String followUpDateRaw = ServletRequestUtils.trim(request.getParameter("followUpDate"));

        int visitId;
        try {
            visitId = Integer.parseInt(visitIdRaw);
            if (visitId <= 0) {
                throw new NumberFormatException("visitId must be positive");
            }
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "A valid visit ID is required.",
                             noteIdRaw, visitIdRaw, clinicalNotes, followUpDateRaw);
            return;
        }

        if (clinicalNotes == null || clinicalNotes.isBlank()) {
            forwardWithError(request, response, "Clinical notes are required.",
                             noteIdRaw, visitIdRaw, clinicalNotes, followUpDateRaw);
            return;
        }

        LocalDate followUpDate = null;
        if (followUpDateRaw != null && !followUpDateRaw.isBlank()) {
            try {
                followUpDate = LocalDate.parse(followUpDateRaw);
            } catch (DateTimeParseException e) {
                forwardWithError(request, response, "Follow-up date must be in YYYY-MM-DD format.",
                                 noteIdRaw, visitIdRaw, clinicalNotes, followUpDateRaw);
                return;
            }
        }

        TreatmentNote note = new TreatmentNote();
        note.setVisitId(visitId);
        note.setClinicalNotes(clinicalNotes);
        note.setFollowUpDate(followUpDate);

        try {
            if (noteIdRaw == null || noteIdRaw.isBlank()) {
                noteService.createNote(note);
                ServletRequestUtils.redirectWithMessage(request, response, "/notes", "success",
                                                       "Treatment note created successfully.");
                return;
            }

            int noteId = Integer.parseInt(noteIdRaw);
            note.setNoteId(noteId);
            noteService.updateNote(note);
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "success",
                                                   "Treatment note updated successfully.");
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "Invalid note ID.",
                             noteIdRaw, visitIdRaw, clinicalNotes, followUpDateRaw);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "NoteServlet POST save error", e);
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error",
                                                   "A system error occurred while saving the note.");
        }
    }

    private void deleteNote(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String noteIdRaw = ServletRequestUtils.trim(request.getParameter("noteId"));
        if (noteIdRaw == null || noteIdRaw.isBlank()) {
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error",
                                                   "Note ID is required to delete.");
            return;
        }

        try {
            int noteId = Integer.parseInt(noteIdRaw);
            noteService.deleteNote(noteId);
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "success",
                                                   "Treatment note deleted successfully.");
        } catch (NumberFormatException e) {
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error", "Invalid note ID.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "NoteServlet POST delete error", e);
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error",
                                                   "A system error occurred while deleting the note.");
        }
    }

    private void forwardWithError(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String errorMessage,
                                  String noteIdRaw,
                                  String visitIdRaw,
                                  String clinicalNotes,
                                  String followUpDateRaw) throws ServletException, IOException {
        TreatmentNote note = new TreatmentNote();

        if (noteIdRaw != null && !noteIdRaw.isBlank()) {
            try {
                note.setNoteId(Integer.parseInt(noteIdRaw));
            } catch (NumberFormatException ignored) {
                // Keep ID unset if malformed so the user can correct the input.
            }
        }

        if (visitIdRaw != null && !visitIdRaw.isBlank()) {
            try {
                note.setVisitId(Integer.parseInt(visitIdRaw));
            } catch (NumberFormatException ignored) {
                // Keep visit ID unset if malformed so the user can correct the input.
            }
        }

        if (followUpDateRaw != null && !followUpDateRaw.isBlank()) {
            try {
                note.setFollowUpDate(LocalDate.parse(followUpDateRaw));
            } catch (DateTimeParseException ignored) {
                // Keep follow-up date unset if malformed so the user can correct the input.
            }
        }

        note.setClinicalNotes(clinicalNotes);

        request.setAttribute("error", errorMessage);
        request.setAttribute("note", note);
        request.getRequestDispatcher("/WEB-INF/views/notes/form.jsp").forward(request, response);
    }
}
