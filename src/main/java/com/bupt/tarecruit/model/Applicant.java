package com.bupt.tarecruit.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Applicant {
    private String userId;
    private String fullName;
    private String phone;
    private String studentId;
    private String programme;
    private String bio;
    private List<String> skills = new ArrayList<>();
    private List<String> preferredWorkingDays = new ArrayList<>();
    private String cvFileName;
    private Instant updatedAt;

    public String getUserId() { return userId; }
    public void setUserId(final String userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(final String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(final String phone) { this.phone = phone; }
    public String getStudentId() { return studentId; }
    public void setStudentId(final String studentId) { this.studentId = studentId; }
    public String getProgramme() { return programme; }
    public void setProgramme(final String programme) { this.programme = programme; }
    public String getBio() { return bio; }
    public void setBio(final String bio) { this.bio = bio; }
    public List<String> getSkills() { return skills; }
    public void setSkills(final List<String> skills) {
        this.skills = skills == null ? new ArrayList<>() : new ArrayList<>(skills);
    }
    public List<String> getPreferredWorkingDays() { return preferredWorkingDays; }
    public void setPreferredWorkingDays(final List<String> preferredWorkingDays) {
        this.preferredWorkingDays = preferredWorkingDays == null
                ? new ArrayList<>()
                : new ArrayList<>(preferredWorkingDays);
    }
    public String getCvFileName() { return cvFileName; }
    public void setCvFileName(final String cvFileName) { this.cvFileName = cvFileName; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(final Instant updatedAt) { this.updatedAt = updatedAt; }
}
