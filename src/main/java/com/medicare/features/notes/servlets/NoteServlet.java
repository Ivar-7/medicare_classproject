package com.medicare.features.notes.servlets;

import com.medicare.features.notes.services.TreatmentNoteService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                request.setAttribute("notes", noteService.getAllNotes());
                request.getRequestDispatcher("/WEB-INF/views/notes/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                request.getRequestDispatcher("/WEB-INF/views/notes/form.jsp").forward(request, response);
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                noteService.getNoteById(id)
                           .ifPresent(n -> request.setAttribute("note", n));
                request.getRequestDispatcher("/WEB-INF/views/notes/form.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "NoteServlet GET error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // implement create / update / delete note logic
        response.sendRedirect(request.getContextPath() + "/notes");
    }
}
