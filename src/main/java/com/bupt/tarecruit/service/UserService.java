package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.UserDao;
import com.bupt.tarecruit.dao.impl.UserDaoImpl;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.DataValidator;
import com.bupt.tarecruit.util.PasswordUtil;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this(new UserDaoImpl());
    }

    public UserService(final Path usersFile) {
        this(new UserDaoImpl(usersFile));
    }

    public UserService(final UserDao userDao) {
        this.userDao = userDao;
    }

    public User register(final String username, final String email, final String password, final Role role) {
        validateRegistration(username, email, password, role);
        String normalizedEmail = email.trim().toLowerCase();
        if (userDao.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = User.create(username.trim(), normalizedEmail, PasswordUtil.hash(password), role);
        userDao.save(user);
        return user;
    }

    public User login(final String email, final String password) {
        validateLogin(email, password);
        String normalizedEmail = email.trim().toLowerCase();
        String hash = PasswordUtil.hash(password);
        return getAllUsers().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(normalizedEmail)
                        && user.getPasswordHash().equals(hash))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

    public Optional<User> findById(final String userId) {
        return userDao.findById(userId);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    private void validateRegistration(final String username, final String email, final String password, final Role role) {
        DataValidator.validateRequired(username, "Username");
        DataValidator.validateEmail(email);
        DataValidator.validatePassword(password);
        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }
    }

    private void validateLogin(final String email, final String password) {
        DataValidator.validateEmail(email);
        DataValidator.validateRequired(password, "Password");
    }
}
