package com.bupt.tarecruit.model;

import java.time.Instant;

public class Organiser {
    private String userId;
    private String department;
    private String title;
    private Instant createdAt;

    public String getUserId() { return userId; }
    public void setUserId(final String userId) { this.userId = userId; }
    public String getDepartment() { return department; }
    public void setDepartment(final String department) { this.department = department; }
    public String getTitle() { return title; }
    public void setTitle(final String title) { this.title = title; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(final Instant createdAt) { this.createdAt = createdAt; }
}
