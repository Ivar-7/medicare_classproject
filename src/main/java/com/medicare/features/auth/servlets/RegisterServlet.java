package com.medicare.features.auth.servlets;

import com.medicare.features.users.services.UserService;
import com.medicare.models.User;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final Pattern FULL_NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s'.-]*$");

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String fullName = ServletRequestUtils.trim(request.getParameter("fullName"));
        String username = ServletRequestUtils.trim(request.getParameter("username"));
        String password = ServletRequestUtils.trim(request.getParameter("password"));
        String roleRaw = ServletRequestUtils.trim(request.getParameter("role"));

        if (fullName == null || fullName.isBlank()) {
            request.setAttribute("error", "Full name is required.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if (!FULL_NAME_PATTERN.matcher(fullName).matches()) {
            request.setAttribute("error", "Full name cannot contain numbers.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("username", username);
            request.setAttribute("role", roleRaw);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if (username == null || username.isBlank()) {
            request.setAttribute("error", "Username is required.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("role", roleRaw);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if (password == null || password.isBlank()) {
            request.setAttribute("error", "Password is required.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("username", username);
            request.setAttribute("role", roleRaw);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if (roleRaw == null || roleRaw.isBlank()) {
            request.setAttribute("error", "Role is required.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("username", username);
            request.setAttribute("role", roleRaw);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        User.Role role;
        try {
            role = User.Role.valueOf(roleRaw);
            if (role == User.Role.Admin) {
                request.setAttribute("error", "Cannot register with Admin role.");
                request.setAttribute("fullName", fullName);
                request.setAttribute("username", username);
                request.setAttribute("role", roleRaw);
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                return;
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid role selected.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("username", username);
            request.setAttribute("role", roleRaw);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        try {
            boolean usernameTaken = userService.getUserByUsername(username).isPresent();

            if (usernameTaken) {
                request.setAttribute("error", "Username is already in use.");
                request.setAttribute("fullName", fullName);
                request.setAttribute("username", username);
                request.setAttribute("role", roleRaw);
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                return;
            }

            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole(role);

            userService.createUser(newUser);

            request.setAttribute("success", "Registration successful! Please log in.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "A system error occurred during registration. Please try again.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("username", username);
            request.setAttribute("role", roleRaw);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}
