package com.bupt.tarecruit.model;

import java.time.Instant;
import java.util.UUID;

public class User {
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private Instant createdAt;

    public static User create(final String username, final String email, final String passwordHash, final Role role) {
        User user = new User();
        user.id = "U-" + UUID.randomUUID();
        user.username = username;
        user.email = email;
        user.passwordHash = passwordHash;
        user.role = role;
        user.createdAt = Instant.now();
        return user;
    }

    public String getId() { return id; }
    public void setId(final String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(final String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(final String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(final String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(final Role role) { this.role = role; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(final Instant createdAt) { this.createdAt = createdAt; }
}
