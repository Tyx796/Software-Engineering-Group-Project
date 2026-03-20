package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.AppPaths;
import com.bupt.tarecruit.util.JsonStorage;
import com.bupt.tarecruit.util.PasswordUtil;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final Path usersFile;

    public UserService() {
        this(AppPaths.dataDirectory().resolve("users.json"));
    }

    public UserService(final Path usersFile) {
        this.usersFile = usersFile;
    }

    public User register(final String username, final String email, final String password, final Role role) {
        validateRegistration(username, email, password);
        List<User> users = getAllUsers();
        String normalizedEmail = email.trim().toLowerCase();
        boolean duplicate = users.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(normalizedEmail));
        if (duplicate) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = User.create(username.trim(), normalizedEmail, PasswordUtil.hash(password), role);
        users.add(user);
        JsonStorage.writeList(usersFile, users);
        return user;
    }

    public User login(final String email, final String password) {
        validateLogin(email, password);
        String hash = PasswordUtil.hash(password);
        return getAllUsers().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email.trim())
                        && user.getPasswordHash().equals(hash))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
    }

    public Optional<User> findById(final String userId) {
        return getAllUsers().stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    public List<User> getAllUsers() {
        return JsonStorage.readList(usersFile, new TypeToken<>() { });
    }

    private void validateRegistration(final String username, final String email, final String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        validateLogin(email, password);
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
    }

    private void validateLogin(final String email, final String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }
    }
}
