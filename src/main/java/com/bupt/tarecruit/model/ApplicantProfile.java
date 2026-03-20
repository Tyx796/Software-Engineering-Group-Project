package com.bupt.tarecruit.model;

import java.time.Instant;

public class ApplicantProfile {
    private String userId;
    private String fullName;
    private String phone;
    private String studentId;
    private String programme;
    private String bio;
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
    public String getCvFileName() { return cvFileName; }
    public void setCvFileName(final String cvFileName) { this.cvFileName = cvFileName; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(final Instant updatedAt) { this.updatedAt = updatedAt; }
}
