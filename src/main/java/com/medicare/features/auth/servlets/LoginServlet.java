package com.medicare.features.auth.servlets;

import com.medicare.features.auth.services.RememberMeService;
import com.medicare.features.users.services.UserService;
import com.medicare.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
    private final UserService userService = new UserService();
    private final RememberMeService rememberMeService = new RememberMeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean rememberMe = "on".equalsIgnoreCase(request.getParameter("rememberMe"));

        request.setAttribute("username", username);

        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "Username is required.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Password is required.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        try {
            Optional<User> user = userService.authenticate(username.trim(), password);
            if (user.isPresent()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("currentUser", user.get());
                session.setMaxInactiveInterval(60 * 60); // 1 hour

                if (rememberMe) {
                    String token = rememberMeService.issueToken(user.get().getUserId());
                    Cookie cookie = new Cookie(RememberMeService.COOKIE_NAME, token);
                    cookie.setHttpOnly(true);
                    cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                    cookie.setMaxAge(RememberMeService.DEFAULT_DAYS * 24 * 60 * 60);
                    cookie.setSecure(request.isSecure());
                    response.addCookie(cookie);
                }

                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                request.setAttribute("error", "Invalid username or password.");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LoginServlet POST error", e);
            request.setAttribute("error", "A system error occurred. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
