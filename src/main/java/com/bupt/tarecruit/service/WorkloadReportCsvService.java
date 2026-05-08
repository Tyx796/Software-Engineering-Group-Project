package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.ApplicantAcceptedAssignmentView;
import com.bupt.tarecruit.model.ApplicantWorkloadView;
import java.util.List;

public class WorkloadReportCsvService {
    public String export(final List<ApplicantWorkloadView> workloadViews) {
        StringBuilder builder = new StringBuilder();
        builder.append("Applicant Name,Email,Student ID,Programme,Total Hours Per Week,Threshold,Status,Assigned Jobs")
                .append(System.lineSeparator());

        if (workloadViews == null) {
            return builder.toString();
        }

        workloadViews.stream()
                .filter(view -> view != null && !view.getAcceptedAssignments().isEmpty())
                .forEach(view -> builder.append(toCsvRow(view)).append(System.lineSeparator()));
        return builder.toString();
    }

    private String toCsvRow(final ApplicantWorkloadView view) {
        Applicant profile = view.getProfile();
        String applicantName = profile == null ? view.getUser().getUsername() : profile.getFullName();
        String studentId = profile == null ? "" : profile.getStudentId();
        String programme = profile == null ? "" : profile.getProgramme();
        String assignedJobs = view.getAcceptedAssignments().stream()
                .map(this::formatAssignment)
                .reduce((left, right) -> left + "; " + right)
                .orElse("");

        return String.join(",",
                escape(applicantName),
                escape(view.getUser().getEmail()),
                escape(studentId),
                escape(programme),
                escape(Integer.toString(view.getTotalHoursPerWeek())),
                escape(Integer.toString(view.getWorkloadThreshold())),
                escape(view.getWorkloadStatusLabel()),
                escape(assignedJobs));
    }

    private String formatAssignment(final ApplicantAcceptedAssignmentView assignment) {
        return assignment.getTitle() + " (" + assignment.getHoursPerWeek() + " hours/week)";
    }

    private String escape(final String value) {
        String sanitized = value == null ? "" : value.replace("\r", " ").replace("\n", " ");
        if (sanitized.contains(",") || sanitized.contains("\"")) {
            return "\"" + sanitized.replace("\"", "\"\"") + "\"";
        }
        return sanitized;
    }
}
