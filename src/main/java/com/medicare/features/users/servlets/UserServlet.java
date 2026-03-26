package com.medicare.features.users.servlets;

import com.medicare.features.users.services.UserService;
import com.medicare.models.User;
import com.medicare.shared.utils.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/users", "/users/*"})
public class UserServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UserServlet.class.getName());
    private static final Pattern FULL_NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s'.-]*$");

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        ServletRequestUtils.exposeAlertsFromQuery(request);
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                request.setAttribute("users", userService.getAllUsers());
                request.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
            } else {
                int userId = Integer.parseInt(pathInfo.substring(1));
                User user = userService.getUserById(userId).orElse(null);
                if (user == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "UserServlet GET error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ServletRequestUtils.trim(request.getParameter("action"));
        if ("delete".equalsIgnoreCase(action)) {
            deleteUser(request, response);
            return;
        }
        saveUser(request, response);
    }

    private void saveUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdRaw = ServletRequestUtils.trim(request.getParameter("userId"));
        String username = ServletRequestUtils.trim(request.getParameter("username"));
        String fullName = ServletRequestUtils.trim(request.getParameter("fullName"));
        String roleRaw = ServletRequestUtils.trim(request.getParameter("role"));
        String password = ServletRequestUtils.trim(request.getParameter("password"));

        boolean isCreate = userIdRaw == null || userIdRaw.isBlank();

        if (username == null || username.isBlank()) {
            forwardWithError(request, response, "Username is required.", userIdRaw, username, fullName, roleRaw);
            return;
        }
        if (fullName == null || fullName.isBlank()) {
            forwardWithError(request, response, "Full name is required.", userIdRaw, username, fullName, roleRaw);
            return;
        }
        if (!FULL_NAME_PATTERN.matcher(fullName).matches()) {
            forwardWithError(request, response, "Full name cannot contain numbers.",
                             userIdRaw, username, fullName, roleRaw);
            return;
        }
        if (roleRaw == null || roleRaw.isBlank()) {
            forwardWithError(request, response, "Role is required.", userIdRaw, username, fullName, roleRaw);
            return;
        }
        if (isCreate && (password == null || password.isBlank())) {
            forwardWithError(request, response, "Password is required when creating a user.",
                             userIdRaw, username, fullName, roleRaw);
            return;
        }

        User.Role role;
        try {
            role = User.Role.valueOf(roleRaw);
        } catch (IllegalArgumentException e) {
            forwardWithError(request, response, "Invalid role selected.", userIdRaw, username, fullName, roleRaw);
            return;
        }

        int userId = 0;
        if (!isCreate) {
            try {
                userId = Integer.parseInt(userIdRaw);
                if (userId <= 0) {
                    throw new NumberFormatException("userId must be positive");
                }
            } catch (NumberFormatException e) {
                forwardWithError(request, response, "Invalid user ID.", userIdRaw, username, fullName, roleRaw);
                return;
            }
        }

        try {
            if (usernameTakenForDifferentUser(username, userId)) {
                forwardWithError(request, response, "Username is already in use.",
                                 userIdRaw, username, fullName, roleRaw);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setRole(role);

            if (isCreate) {
                user.setPassword(password);
                userService.createUser(user);
                ServletRequestUtils.redirectWithMessage(request, response, "/users", "success",
                                                       "User created successfully.");
                return;
            }

            user.setUserId(userId);
            userService.updateUser(user);

            if (password != null && !password.isBlank()) {
                userService.changePassword(userId, password);
            }

            ServletRequestUtils.redirectWithMessage(request, response, "/users", "success",
                                                   "User updated successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "UserServlet POST save error", e);
            ServletRequestUtils.redirectWithMessage(request, response, "/users", "error",
                                                   "A system error occurred while saving the user.");
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String userIdRaw = ServletRequestUtils.trim(request.getParameter("userId"));
        if (userIdRaw == null || userIdRaw.isBlank()) {
            ServletRequestUtils.redirectWithMessage(request, response, "/users", "error",
                                                   "User ID is required to delete.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdRaw);
            userService.deleteUser(userId);
            ServletRequestUtils.redirectWithMessage(request, response, "/users", "success",
                                                   "User deleted successfully.");
        } catch (NumberFormatException e) {
            ServletRequestUtils.redirectWithMessage(request, response, "/users", "error", "Invalid user ID.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "UserServlet POST delete error", e);
            ServletRequestUtils.redirectWithMessage(request, response, "/users", "error",
                                                   "A system error occurred while deleting the user.");
        }
    }

    private void forwardWithError(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String errorMessage,
                                  String userIdRaw,
                                  String username,
                                  String fullName,
                                  String roleRaw) throws ServletException, IOException {
        User user = new User();

        if (userIdRaw != null && !userIdRaw.isBlank()) {
            try {
                user.setUserId(Integer.parseInt(userIdRaw));
            } catch (NumberFormatException ignored) {
                // Keep ID unset if malformed so the user can correct the input.
            }
        }

        user.setUsername(username);
        user.setFullName(fullName);

        if (roleRaw != null && !roleRaw.isBlank()) {
            try {
                user.setRole(User.Role.valueOf(roleRaw));
            } catch (IllegalArgumentException ignored) {
                // Keep role unset when invalid so the user can re-select.
            }
        }

        request.setAttribute("error", errorMessage);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
    }

    private boolean usernameTakenForDifferentUser(String username, int currentUserId) throws Exception {
        return userService.getUserByUsername(username)
                .map(existingUser -> existingUser.getUserId() != currentUserId)
                .orElse(false);
    }
}
