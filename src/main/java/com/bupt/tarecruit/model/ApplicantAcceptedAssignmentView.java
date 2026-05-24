package com.bupt.tarecruit.model;

public class ApplicantAcceptedAssignmentView {
    private String jobId;
    private String title;
    private String department;
    private String organiserUserId;
    private int hoursPerWeek;

    public String getJobId() { return jobId; }
    public void setJobId(final String jobId) { this.jobId = jobId; }
    public String getTitle() { return title; }
    public void setTitle(final String title) { this.title = title; }
    public String getDepartment() { return department; }
    public void setDepartment(final String department) { this.department = department; }
    public String getOrganiserUserId() { return organiserUserId; }
    public void setOrganiserUserId(final String organiserUserId) { this.organiserUserId = organiserUserId; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(final int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
}
