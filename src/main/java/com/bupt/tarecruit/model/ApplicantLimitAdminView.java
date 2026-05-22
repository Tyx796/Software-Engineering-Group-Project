package com.bupt.tarecruit.model;

public class ApplicantLimitAdminView {
    private User user;
    private Applicant profile;
    private Integer applicationLimitOverride;
    private int effectiveApplicationLimit;
    private int activeApplicationCount;
    private int acceptedAssignmentCount;

    public User getUser() { return user; }
    public void setUser(final User user) { this.user = user; }
    public Applicant getProfile() { return profile; }
    public void setProfile(final Applicant profile) { this.profile = profile; }
    public Integer getApplicationLimitOverride() { return applicationLimitOverride; }
    public void setApplicationLimitOverride(final Integer applicationLimitOverride) {
        this.applicationLimitOverride = applicationLimitOverride;
    }
    public int getEffectiveApplicationLimit() { return effectiveApplicationLimit; }
    public void setEffectiveApplicationLimit(final int effectiveApplicationLimit) {
        this.effectiveApplicationLimit = effectiveApplicationLimit;
    }
    public int getActiveApplicationCount() { return activeApplicationCount; }
    public void setActiveApplicationCount(final int activeApplicationCount) {
        this.activeApplicationCount = activeApplicationCount;
    }
    public int getAcceptedAssignmentCount() { return acceptedAssignmentCount; }
    public void setAcceptedAssignmentCount(final int acceptedAssignmentCount) {
        this.acceptedAssignmentCount = acceptedAssignmentCount;
    }

    public boolean isUsingOverride() {
        return applicationLimitOverride != null;
    }

    public boolean isOverEffectiveLimit() {
        return activeApplicationCount > effectiveApplicationLimit;
    }
}
