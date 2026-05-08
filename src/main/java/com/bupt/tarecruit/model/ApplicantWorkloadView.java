package com.bupt.tarecruit.model;

import java.util.ArrayList;
import java.util.List;

public class ApplicantWorkloadView {
    private User user;
    private Applicant profile;
    private List<ApplicantAcceptedAssignmentView> acceptedAssignments = new ArrayList<>();
    private int totalHoursPerWeek;
    private int workloadThreshold;

    public User getUser() { return user; }
    public void setUser(final User user) { this.user = user; }
    public Applicant getProfile() { return profile; }
    public void setProfile(final Applicant profile) { this.profile = profile; }
    public List<ApplicantAcceptedAssignmentView> getAcceptedAssignments() { return acceptedAssignments; }
    public void setAcceptedAssignments(final List<ApplicantAcceptedAssignmentView> acceptedAssignments) {
        this.acceptedAssignments = acceptedAssignments == null ? new ArrayList<>() : new ArrayList<>(acceptedAssignments);
    }
    public int getTotalHoursPerWeek() { return totalHoursPerWeek; }
    public void setTotalHoursPerWeek(final int totalHoursPerWeek) { this.totalHoursPerWeek = totalHoursPerWeek; }
    public int getWorkloadThreshold() { return workloadThreshold; }
    public void setWorkloadThreshold(final int workloadThreshold) { this.workloadThreshold = workloadThreshold; }

    public boolean isOverloaded() {
        return totalHoursPerWeek > workloadThreshold;
    }

    public String getWorkloadStatusLabel() {
        return isOverloaded() ? "Overloaded" : "Within threshold";
    }

    public String getWorkloadAlertMessage() {
        if (isOverloaded()) {
            return totalHoursPerWeek + " hours/week exceeds the " + workloadThreshold + "-hour threshold.";
        }
        return totalHoursPerWeek + " hours/week is within the " + workloadThreshold + "-hour threshold.";
    }
}
