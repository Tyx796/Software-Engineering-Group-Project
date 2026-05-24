package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.ApplicantAcceptedAssignmentView;
import com.bupt.tarecruit.model.ApplicantWorkloadView;
import com.bupt.tarecruit.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkloadReportCsvServiceTest {
    private final WorkloadReportCsvService service = new WorkloadReportCsvService();

    @Test
    void exportIncludesOnlyApplicantsWithAcceptedAssignments() {
        ApplicantWorkloadView included = workloadView("Alice, Zhang", "alice@example.com", "20260001", "CS", 12, 20,
                List.of(assignment("SE TA", 12)));
        ApplicantWorkloadView excluded = workloadView("Bob", "bob@example.com", "20260002", "Math", 0, 20, List.of());

        String csv = service.export(List.of(included, excluded));

        assertTrue(csv.contains("Applicant Name,Email,Student ID,Programme,Total Hours Per Week,Threshold,Status,Assigned Jobs"));
        assertTrue(csv.contains("\"Alice, Zhang\""));
        assertFalse(csv.contains("bob@example.com"));
    }

    @Test
    void exportEscapesQuotesAndNewlines() {
        ApplicantWorkloadView view = workloadView("Alice \"A\"", "alice@example.com", "20260001", "CS\nAI", 24, 20,
                List.of(
                        assignment("SE \"Core\"", 12),
                        assignment("DB TA", 12)));

        String csv = service.export(List.of(view));
        String[] lines = csv.split("\\r?\\n");

        assertEquals(2, lines.length);
        assertTrue(lines[1].contains("\"Alice \"\"A\"\"\""));
        assertTrue(lines[1].contains(",CS AI,"));
        assertTrue(lines[1].contains("\"SE \"\"Core\"\" (12 hours/week); DB TA (12 hours/week)\""));
    }

    private ApplicantWorkloadView workloadView(final String fullName, final String email, final String studentId,
            final String programme, final int totalHours, final int threshold,
            final List<ApplicantAcceptedAssignmentView> assignments) {
        User user = new User();
        user.setUsername(fullName);
        user.setEmail(email);

        Applicant profile = new Applicant();
        profile.setFullName(fullName);
        profile.setStudentId(studentId);
        profile.setProgramme(programme);

        ApplicantWorkloadView view = new ApplicantWorkloadView();
        view.setUser(user);
        view.setProfile(profile);
        view.setAcceptedAssignments(assignments);
        view.setTotalHoursPerWeek(totalHours);
        view.setWorkloadThreshold(threshold);
        return view;
    }

    private ApplicantAcceptedAssignmentView assignment(final String title, final int hours) {
        ApplicantAcceptedAssignmentView assignment = new ApplicantAcceptedAssignmentView();
        assignment.setTitle(title);
        assignment.setHoursPerWeek(hours);
        return assignment;
    }
}
