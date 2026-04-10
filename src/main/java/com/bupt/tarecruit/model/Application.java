package com.bupt.tarecruit.model;

import java.time.Instant;
import java.util.UUID;

public class Application {
    private String id;
    private String applicantUserId;
    private String jobId;
    private ApplicationStatus status;
    private Instant appliedAt;
    private Instant reviewedAt;

    public static Application create(final String applicantUserId, final String jobId) {
        Application application = new Application();
        application.id = "APP-" + UUID.randomUUID();
        application.applicantUserId = applicantUserId;
        application.jobId = jobId;
        application.status = ApplicationStatus.PENDING;
        application.appliedAt = Instant.now();
        return application;
    }

    public String getId() { return id; }
    public void setId(final String id) { this.id = id; }
    public String getApplicantUserId() { return applicantUserId; }
    public void setApplicantUserId(final String applicantUserId) { this.applicantUserId = applicantUserId; }
    public String getJobId() { return jobId; }
    public void setJobId(final String jobId) { this.jobId = jobId; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(final ApplicationStatus status) { this.status = status; }
    public Instant getAppliedAt() { return appliedAt; }
    public void setAppliedAt(final Instant appliedAt) { this.appliedAt = appliedAt; }
    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(final Instant reviewedAt) { this.reviewedAt = reviewedAt; }
}
