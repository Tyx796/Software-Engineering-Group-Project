package com.bupt.tarecruit.model;

import java.time.Instant;

public class SystemSettings {
    public static final int DEFAULT_APPLICANT_APPLICATION_LIMIT = 3;

    private Integer defaultApplicantApplicationLimit;
    private Instant updatedAt;

    public static SystemSettings defaults() {
        SystemSettings settings = new SystemSettings();
        settings.defaultApplicantApplicationLimit = DEFAULT_APPLICANT_APPLICATION_LIMIT;
        settings.updatedAt = Instant.now();
        return settings;
    }

    public int getDefaultApplicantApplicationLimit() {
        return defaultApplicantApplicationLimit == null
                ? DEFAULT_APPLICANT_APPLICATION_LIMIT
                : defaultApplicantApplicationLimit;
    }

    public void setDefaultApplicantApplicationLimit(final Integer defaultApplicantApplicationLimit) {
        this.defaultApplicantApplicationLimit = defaultApplicantApplicationLimit;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
