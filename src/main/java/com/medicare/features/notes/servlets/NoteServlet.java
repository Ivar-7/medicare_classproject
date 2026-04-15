package com.medicare.features.notes.servlets;

import com.medicare.features.notes.services.TreatmentNoteService;
import com.medicare.features.visits.services.MedicalVisitService;
import com.medicare.models.TreatmentNote;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/notes", "/notes/*"})
public class NoteServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(NoteServlet.class.getName());

    private final TreatmentNoteService noteService = new TreatmentNoteService();
    private final MedicalVisitService visitService = new MedicalVisitService();

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
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "NoteServlet GET error", e);
            if (pathInfo == null || pathInfo.equals("/")) {
                request.setAttribute("error", mapNoteSqlError(e));
                request.setAttribute("notes", java.util.Collections.emptyList());
                request.getRequestDispatcher("/WEB-INF/views/notes/list.jsp").forward(request, response);
                return;
            }
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error", mapNoteSqlError(e));
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

        try {
            if (!visitService.existsVisit(visitId)) {
                forwardWithError(request, response, "Visit ID does not exist.",
                                 noteIdRaw, visitIdRaw, clinicalNotes, followUpDateRaw);
                return;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "NoteServlet visit lookup error", e);
            forwardWithError(request, response,
                             "Unable to validate visit due to a database issue. Please try again.",
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
            if (!noteService.noteExists(noteId)) {
                ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error",
                                                       "Treatment note was not found.");
                return;
            }
            note.setNoteId(noteId);
            noteService.updateNote(note);
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "success",
                                                   "Treatment note updated successfully.");
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "Invalid note ID.",
                             noteIdRaw, visitIdRaw, clinicalNotes, followUpDateRaw);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "NoteServlet POST save error", e);
            forwardWithError(request, response, mapNoteSqlError(e),
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
            if (!noteService.noteExists(noteId)) {
                ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error",
                                                       "Treatment note was not found.");
                return;
            }
            noteService.deleteNote(noteId);
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "success",
                                                   "Treatment note deleted successfully.");
        } catch (NumberFormatException e) {
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error", "Invalid note ID.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "NoteServlet POST delete error", e);
            ServletRequestUtils.redirectWithMessage(request, response, "/notes", "error",
                                                   mapNoteSqlError(e));
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

    private String mapNoteSqlError(SQLException e) {
        String sqlMessage = flattenSqlMessage(e).toLowerCase();
        if (sqlMessage.contains("foreign key constraint failed")) {
            return "Treatment note references an invalid visit. Please verify visit information.";
        }
        if (sqlMessage.contains("not null constraint failed")) {
            return "Some required treatment note fields are missing. Please complete all required fields.";
        }
        if (sqlMessage.contains("invalid date value in column 'follow_up_date'")
            || sqlMessage.contains("invalid date value in column 'created_at'")
            || sqlMessage.contains("invalid date value in column 'updated_at'")) {
            return "Treatment note data contains invalid dates. Please contact an administrator.";
        }
        return "A database error occurred while processing the treatment note.";
    }

    private String flattenSqlMessage(SQLException e) {
        StringBuilder sb = new StringBuilder();
        SQLException current = e;
        while (current != null) {
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                if (sb.length() > 0) {
                    sb.append(" | ");
                }
                sb.append(current.getMessage());
            }
            current = current.getNextException();
        }
        return sb.toString();
    }
}
