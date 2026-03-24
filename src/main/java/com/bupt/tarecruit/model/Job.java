package com.bupt.tarecruit.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Job {
    private String id;
    private String organiserUserId;
    private String title;
    private String department;
    private String description;
    private List<String> requirements = new ArrayList<>();
    private int hoursPerWeek;
    private LocalDate deadline;
    private String status;
    private Instant createdAt;

    public static Job create(final String organiserUserId) {
        Job job = new Job();
        job.id = "J-" + UUID.randomUUID();
        job.organiserUserId = organiserUserId;
        job.status = "OPEN";
        job.createdAt = Instant.now();
        return job;
    }

    public String getId() { return id; }
    public void setId(final String id) { this.id = id; }
    public String getOrganiserUserId() { return organiserUserId; }
    public void setOrganiserUserId(final String organiserUserId) { this.organiserUserId = organiserUserId; }
    public String getTitle() { return title; }
    public void setTitle(final String title) { this.title = title; }
    public String getDepartment() { return department; }
    public void setDepartment(final String department) { this.department = department; }
    public String getDescription() { return description; }
    public void setDescription(final String description) { this.description = description; }
    public List<String> getRequirements() { return requirements; }
    public void setRequirements(final List<String> requirements) { this.requirements = requirements; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(final int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(final LocalDate deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(final String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(final Instant createdAt) { this.createdAt = createdAt; }
}
