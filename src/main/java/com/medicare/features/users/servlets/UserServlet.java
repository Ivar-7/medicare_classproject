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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/users", "/users/*"})
public class UserServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UserServlet.class.getName());
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s'.-]*$");

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
        String firstName = ServletRequestUtils.trim(request.getParameter("firstName"));
        String lastName = ServletRequestUtils.trim(request.getParameter("lastName"));
        String email = ServletRequestUtils.trim(request.getParameter("email"));
        String phone = ServletRequestUtils.trim(request.getParameter("phone"));
        String dateOfEmploymentRaw = ServletRequestUtils.trim(request.getParameter("dateOfEmployment"));
        String roleRaw = ServletRequestUtils.trim(request.getParameter("role"));
        String password = ServletRequestUtils.trim(request.getParameter("password"));

        boolean isCreate = userIdRaw == null || userIdRaw.isBlank();

        if (username == null || username.isBlank()) {
            forwardWithError(request, response, "Username is required.", userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
            return;
        }
        if (firstName == null || firstName.isBlank()) {
            forwardWithError(request, response, "First name is required.", userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
            return;
        }
        if (lastName == null || lastName.isBlank()) {
            forwardWithError(request, response, "Last name is required.", userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
            return;
        }
        if (!NAME_PATTERN.matcher(firstName).matches() || !NAME_PATTERN.matcher(lastName).matches()) {
            forwardWithError(request, response, "Names cannot contain numbers.",
                             userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
            return;
        }

        LocalDate dateOfEmployment = null;
        if (dateOfEmploymentRaw != null && !dateOfEmploymentRaw.isBlank()) {
            try {
                dateOfEmployment = LocalDate.parse(dateOfEmploymentRaw);
            } catch (DateTimeParseException e) {
                forwardWithError(request, response, "Date of employment must be a valid date.",
                                 userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
                return;
            }
            if (dateOfEmployment.isAfter(LocalDate.now())) {
                forwardWithError(request, response, "Date of employment cannot be in the future.",
                                 userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
                return;
            }
        }

        if (roleRaw == null || roleRaw.isBlank()) {
            forwardWithError(request, response, "Role is required.", userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
            return;
        }
        if (isCreate && (password == null || password.isBlank())) {
            forwardWithError(request, response, "Password is required when creating a user.",
                             userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
            return;
        }

        User.Role role;
        try {
            role = User.Role.valueOf(roleRaw);
        } catch (IllegalArgumentException e) {
            forwardWithError(request, response, "Invalid role selected.", userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
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
                forwardWithError(request, response, "Invalid user ID.", userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
                return;
            }
        }

        try {
            if (usernameTakenForDifferentUser(username, userId)) {
                forwardWithError(request, response, "Username is already in use.",
                                 userIdRaw, username, firstName, lastName, email, phone, dateOfEmploymentRaw, roleRaw);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setRole(role);
            user.setDateOfEmployment(dateOfEmployment);

            if (isCreate) {
                LocalDate today = LocalDate.now();
                user.setPasswordHash(password);
                user.setCreatedAt(today);
                user.setUpdatedAt(today);
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
                                  String firstName,
                                  String lastName,
                                  String email,
                                  String phone,
                                  String dateOfEmploymentRaw,
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
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);

        if (dateOfEmploymentRaw != null && !dateOfEmploymentRaw.isBlank()) {
            request.setAttribute("dateOfEmployment", dateOfEmploymentRaw);
            try {
                user.setDateOfEmployment(LocalDate.parse(dateOfEmploymentRaw));
            } catch (DateTimeParseException ignored) {
                // Keep raw value in request attribute so user can correct it.
            }
        }

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
