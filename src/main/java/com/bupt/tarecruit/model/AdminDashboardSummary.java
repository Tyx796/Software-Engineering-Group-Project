package com.bupt.tarecruit.model;

public class AdminDashboardSummary {
    private long totalUsers;
    private long totalApplicants;
    private long totalOrganisers;
    private long totalJobs;
    private long openJobs;
    private long fullJobs;
    private long totalApplications;
    private long pendingOrReviewingApplications;
    private long acceptedApplications;
    private long applicantsAtLimit;
    private long overloadedApplicants;

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(final long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalApplicants() { return totalApplicants; }
    public void setTotalApplicants(final long totalApplicants) { this.totalApplicants = totalApplicants; }
    public long getTotalOrganisers() { return totalOrganisers; }
    public void setTotalOrganisers(final long totalOrganisers) { this.totalOrganisers = totalOrganisers; }
    public long getTotalJobs() { return totalJobs; }
    public void setTotalJobs(final long totalJobs) { this.totalJobs = totalJobs; }
    public long getOpenJobs() { return openJobs; }
    public void setOpenJobs(final long openJobs) { this.openJobs = openJobs; }
    public long getFullJobs() { return fullJobs; }
    public void setFullJobs(final long fullJobs) { this.fullJobs = fullJobs; }
    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(final long totalApplications) { this.totalApplications = totalApplications; }
    public long getPendingOrReviewingApplications() { return pendingOrReviewingApplications; }
    public void setPendingOrReviewingApplications(final long pendingOrReviewingApplications) {
        this.pendingOrReviewingApplications = pendingOrReviewingApplications;
    }
    public long getAcceptedApplications() { return acceptedApplications; }
    public void setAcceptedApplications(final long acceptedApplications) { this.acceptedApplications = acceptedApplications; }
    public long getApplicantsAtLimit() { return applicantsAtLimit; }
    public void setApplicantsAtLimit(final long applicantsAtLimit) { this.applicantsAtLimit = applicantsAtLimit; }
    public long getOverloadedApplicants() { return overloadedApplicants; }
    public void setOverloadedApplicants(final long overloadedApplicants) { this.overloadedApplicants = overloadedApplicants; }
}
