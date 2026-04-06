package com.bupt.tarecruit.model;

import java.time.Instant;

public class Admin {
    private String userId;
    private String department;
    private Instant createdAt;

    public String getUserId() { return userId; }
    public void setUserId(final String userId) { this.userId = userId; }
    public String getDepartment() { return department; }
    public void setDepartment(final String department) { this.department = department; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(final Instant createdAt) { this.createdAt = createdAt; }
}
