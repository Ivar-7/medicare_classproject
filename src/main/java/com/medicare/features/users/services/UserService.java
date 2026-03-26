package com.medicare.features.users.services;

import com.medicare.features.users.dao.UserDAO;
import com.medicare.models.User;
import com.medicare.shared.utils.PasswordUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    public Optional<User> getUserById(int id) throws SQLException {
        return userDAO.findById(id);
    }

    public Optional<User> getUserByUsername(String username) throws SQLException {
        return userDAO.findByUsername(username);
    }

    public Optional<User> authenticate(String username, String password) throws SQLException {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent() && PasswordUtils.verify(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public void createUser(User user) throws SQLException {
        user.setPassword(PasswordUtils.hash(user.getPassword()));
        userDAO.save(user);
    }

    public void updateUser(User user) throws SQLException {
        userDAO.update(user);
    }

    public void changePassword(int userId, String newPassword) throws SQLException {
        userDAO.updatePassword(userId, PasswordUtils.hash(newPassword));
    }

    public void deleteUser(int userId) throws SQLException {
        userDAO.delete(userId);
    }

    public int countUsers() throws SQLException {
        return userDAO.count();
    }

    public List<User> getDoctors() throws SQLException {
        return userDAO.findByRole("Doctor");
    }
}
