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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(RegisterServlet.class.getName());
  private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s'.-]*$");

  private final UserService userService = new UserService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session != null && session.getAttribute("currentUser") != null) {
      response.sendRedirect(request.getContextPath() + "/home");
      return;
    }

    request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session != null && session.getAttribute("currentUser") != null) {
      response.sendRedirect(request.getContextPath() + "/home");
      return;
    }

    String firstName = ServletRequestUtils.trim(request.getParameter("firstName"));
    String lastName = ServletRequestUtils.trim(request.getParameter("lastName"));
    String username = ServletRequestUtils.trim(request.getParameter("username"));
    String email = ServletRequestUtils.trim(request.getParameter("email"));
    String phone = ServletRequestUtils.trim(request.getParameter("phone"));
    String dateOfEmploymentRaw = ServletRequestUtils.trim(request.getParameter("dateOfEmployment"));
    String password = ServletRequestUtils.trim(request.getParameter("password"));
    String roleRaw = ServletRequestUtils.trim(request.getParameter("role"));

    request.setAttribute("firstName", firstName);
    request.setAttribute("lastName", lastName);
    request.setAttribute("username", username);
    request.setAttribute("email", email);
    request.setAttribute("phone", phone);
    request.setAttribute("dateOfEmployment", dateOfEmploymentRaw);
    request.setAttribute("role", roleRaw);

    if (firstName == null || firstName.isBlank()) {
      request.setAttribute("error", "First name is required.");
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
      return;
    }
    if (lastName == null || lastName.isBlank()) {
      request.setAttribute("error", "Last name is required.");
      request.setAttribute("firstName", firstName);
      request.setAttribute("username", username);
      request.setAttribute("email", email);
      request.setAttribute("phone", phone);
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
      return;
    }

    if (!NAME_PATTERN.matcher(firstName).matches() || !NAME_PATTERN.matcher(lastName).matches()) {
      request.setAttribute("error", "Names cannot contain numbers.");
      request.setAttribute("firstName", firstName);
      request.setAttribute("lastName", lastName);
      request.setAttribute("username", username);
      request.setAttribute("email", email);
      request.setAttribute("phone", phone);
      request.setAttribute("role", roleRaw);
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
      return;
    }

    if (username == null || username.isBlank()) {
      request.setAttribute("error", "Username is required.");
      request.setAttribute("firstName", firstName);
      request.setAttribute("lastName", lastName);
      request.setAttribute("email", email);
      request.setAttribute("phone", phone);
      request.setAttribute("role", roleRaw);
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
      return;
    }

    if (password == null || password.isBlank()) {
      request.setAttribute("error", "Password is required.");
      request.setAttribute("firstName", firstName);
      request.setAttribute("lastName", lastName);
      request.setAttribute("username", username);
      request.setAttribute("email", email);
      request.setAttribute("phone", phone);
      request.setAttribute("role", roleRaw);
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
      return;
    }

    if (roleRaw == null || roleRaw.isBlank()) {
      request.setAttribute("error", "Role is required.");
      request.setAttribute("firstName", firstName);
      request.setAttribute("lastName", lastName);
      request.setAttribute("username", username);
      request.setAttribute("email", email);
      request.setAttribute("phone", phone);
      request.setAttribute("role", roleRaw);
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
      return;
    }

    User.Role role;
    try {
      role = User.Role.valueOf(roleRaw);
      if (role == User.Role.Admin) {
        request.setAttribute("error", "Cannot register with Admin role.");
        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("role", roleRaw);
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        return;
      }
    } catch (IllegalArgumentException e) {
      request.setAttribute("error", "Invalid role selected.");
      request.setAttribute("firstName", firstName);
      request.setAttribute("lastName", lastName);
      request.setAttribute("username", username);
      request.setAttribute("email", email);
      request.setAttribute("phone", phone);
      request.setAttribute("role", roleRaw);
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
      return;
    }

    LocalDate dateOfEmployment = null;
    if (dateOfEmploymentRaw != null && !dateOfEmploymentRaw.isBlank()) {
      try {
        dateOfEmployment = LocalDate.parse(dateOfEmploymentRaw);
      } catch (DateTimeParseException e) {
        request.setAttribute("error", "Date of employment must be a valid date.");
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        return;
      }

      if (dateOfEmployment.isAfter(LocalDate.now())) {
        request.setAttribute("error", "Date of employment cannot be in the future.");
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        return;
      }
    }

    try {
      boolean usernameTaken = userService.usernameExists(username);
      boolean emailTaken = email != null
          && !email.isBlank()
          && userService.emailExists(email);

      if (usernameTaken) {
        request.setAttribute("error", "Username is already in use.");
        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("role", roleRaw);
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        return;
      }

      if (emailTaken) {
        request.setAttribute("error", "Email is already in use.");
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        return;
      }

      User newUser = new User();
      newUser.setFirstName(firstName);
      newUser.setLastName(lastName);
      newUser.setUsername(username);
      newUser.setPasswordHash(password);
      newUser.setRole(role);
      newUser.setEmail(email);
      newUser.setPhone(phone);
      newUser.setDateOfEmployment(dateOfEmployment);
      newUser.setCreatedAt(LocalDate.now());
      newUser.setUpdatedAt(LocalDate.now());

      userService.createUser(newUser);

      request.setAttribute("success", "Registration successful! Please log in.");
      request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);

    } catch (SQLException e) {
      logger.log(Level.SEVERE, "RegisterServlet POST error", e);
      String sqlMessage = flattenSqlMessage(e).toLowerCase();
      if (sqlMessage.contains("unique constraint failed: users.email")) {
        request.setAttribute("error", "Email is already in use.");
      } else if (sqlMessage.contains("unique constraint failed: users.username")) {
        request.setAttribute("error", "Username is already in use.");
      } else if (sqlMessage.contains("invalid date value in column 'date_of_employment'")) {
        request.setAttribute("error", "An existing user has an invalid employment date. Please contact an admin to correct user data.");
      } else {
        request.setAttribute("error", "A system error occurred during registration. Please try again.");
      }
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "RegisterServlet POST error", e);
      request.setAttribute("error", "A system error occurred during registration. Please try again.");
      request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }
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
