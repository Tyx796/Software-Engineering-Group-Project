package com.bupt.tarecruit.model;

import java.time.Instant;

public class ApplicantLimitPolicy {
    private String userId;
    private Integer applicationLimitOverride;
    private Instant updatedAt;

    public String getUserId() { return userId; }
    public void setUserId(final String userId) { this.userId = userId; }
    public Integer getApplicationLimitOverride() { return applicationLimitOverride; }
    public void setApplicationLimitOverride(final Integer applicationLimitOverride) {
        this.applicationLimitOverride = applicationLimitOverride;
    }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(final Instant updatedAt) { this.updatedAt = updatedAt; }
}
