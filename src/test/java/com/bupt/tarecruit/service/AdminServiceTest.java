package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.AdminDashboardSummary;
import com.bupt.tarecruit.model.AdminJobSupervisionView;
import com.bupt.tarecruit.model.ApplicantWorkloadView;
import com.bupt.tarecruit.model.ApplicantLimitAdminView;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminServiceTest {
    @Test
    void updatingGlobalDefaultChangesEffectiveLimitImmediately() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);

        context.adminService.updateGlobalDefaultApplicantApplicationLimit(5);

        assertEquals(5, context.adminService.getGlobalDefaultApplicantApplicationLimit());
        assertEquals(5, context.adminService.getApplicantLimitView(applicant.getId()).getEffectiveApplicationLimit());
    }

    @Test
    void updatingApplicantOverrideChangesEffectiveLimitImmediately() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        context.adminService.updateGlobalDefaultApplicantApplicationLimit(3);

        context.adminService.saveApplicantApplicationLimitOverride(applicant.getId(), 6);

        ApplicantLimitAdminView view = context.adminService.getApplicantLimitView(applicant.getId());
        assertEquals(6, view.getEffectiveApplicationLimit());
        assertEquals(6, view.getApplicationLimitOverride());
        assertTrue(view.isUsingOverride());
    }

    @Test
    void applicantOverrideHasPriorityOverGlobalDefault() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        context.adminService.updateGlobalDefaultApplicantApplicationLimit(2);
        context.adminService.saveApplicantApplicationLimitOverride(applicant.getId(), 7);

        assertEquals(7, context.adminService.getApplicantLimitView(applicant.getId()).getEffectiveApplicationLimit());
    }

    @Test
    void adminCanSetOverrideForRegisteredApplicantWithoutProfile() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);

        context.adminService.saveApplicantApplicationLimitOverride(applicant.getId(), 4);

        ApplicantLimitAdminView view = context.adminService.getApplicantLimitView(applicant.getId());
        assertEquals(4, view.getEffectiveApplicationLimit());
        assertTrue(view.isUsingOverride());
        assertEquals(null, view.getProfile());
    }

    @Test
    void clearingApplicantOverrideFallsBackToGlobalDefault() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        context.adminService.updateGlobalDefaultApplicantApplicationLimit(3);
        context.adminService.saveApplicantApplicationLimitOverride(applicant.getId(), 5);

        context.adminService.clearApplicantApplicationLimitOverride(applicant.getId());

        ApplicantLimitAdminView view = context.adminService.getApplicantLimitView(applicant.getId());
        assertEquals(3, view.getEffectiveApplicationLimit());
        assertFalse(view.isUsingOverride());
    }

    @Test
    void getApplicantLimitViewsIncludesActiveApplicationCounts() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        Job jobOne = createJob(context, "organiser-1", 2);
        Job jobTwo = createJob(context, "organiser-1", 2);
        saveApplication(context, applicant.getId(), jobOne.getId(), ApplicationStatus.PENDING);
        saveApplication(context, applicant.getId(), jobTwo.getId(), ApplicationStatus.ACCEPTED);

        List<ApplicantLimitAdminView> views = context.adminService.getApplicantLimitViews();

        assertEquals(1, views.size());
        assertEquals(2, views.get(0).getActiveApplicationCount());
        assertEquals(1, views.get(0).getAcceptedAssignmentCount());
    }

    @Test
    void countsApplicantsOverEffectiveLimit() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        context.adminService.updateGlobalDefaultApplicantApplicationLimit(1);
        Job jobOne = createJob(context, "organiser-1", 2);
        Job jobTwo = createJob(context, "organiser-1", 2);
        saveApplication(context, applicant.getId(), jobOne.getId(), ApplicationStatus.PENDING);
        saveApplication(context, applicant.getId(), jobTwo.getId(), ApplicationStatus.REVIEWING);

        assertEquals(1, context.adminService.countApplicantsOverEffectiveLimit());
    }

    @Test
    void nonApplicantAccountsCannotReceiveApplicantOverride() throws Exception {
        TestContext context = createContext();
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.adminService.saveApplicantApplicationLimitOverride(organiser.getId(), 4));
        assertEquals("Only applicant accounts can have applicant limit overrides.", exception.getMessage());
    }

    @Test
    void applicantOverrideRejectsNegativeValues() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.adminService.saveApplicantApplicationLimitOverride(applicant.getId(), -1));
        assertEquals("Applicant application limit override must be zero or greater.", exception.getMessage());
    }

    @Test
    void dashboardSummaryAggregatesAdminMetrics() throws Exception {
        TestContext context = createContext();
        User applicantOne = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User applicantTwo = context.userService.register("Ben", "ben@example.com", "secret1", Role.APPLICANT);
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);
        context.userService.register("Ada", "ada@example.com", "secret1", Role.ADMIN);
        context.adminService.updateGlobalDefaultApplicantApplicationLimit(1);
        context.adminService.saveApplicantApplicationLimitOverride(applicantTwo.getId(), 2);

        Job fullJob = createJob(context, organiser.getId(), 1);
        fullJob.setHoursPerWeek(22);
        context.jobDao.save(fullJob);
        Job openJob = createJob(context, organiser.getId(), 2);

        saveApplication(context, applicantOne.getId(), fullJob.getId(), ApplicationStatus.ACCEPTED);
        saveApplication(context, applicantOne.getId(), openJob.getId(), ApplicationStatus.REVIEWING);
        saveApplication(context, applicantTwo.getId(), openJob.getId(), ApplicationStatus.PENDING);
        saveApplication(context, applicantTwo.getId(), fullJob.getId(), ApplicationStatus.REJECTED);

        AdminDashboardSummary summary = context.adminService.getDashboardSummary();

        assertEquals(4, summary.getTotalUsers());
        assertEquals(2, summary.getTotalApplicants());
        assertEquals(1, summary.getTotalOrganisers());
        assertEquals(2, summary.getTotalJobs());
        assertEquals(2, summary.getOpenJobs());
        assertEquals(1, summary.getFullJobs());
        assertEquals(4, summary.getTotalApplications());
        assertEquals(2, summary.getPendingOrReviewingApplications());
        assertEquals(1, summary.getAcceptedApplications());
        assertEquals(1, summary.getApplicantsAtLimit());
        assertEquals(1, summary.getOverloadedApplicants());
    }

    @Test
    void workloadViewsIncludeAcceptedAssignmentsAndOverloadFlag() throws Exception {
        TestContext context = createContext();
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);
        context.applicantService.createProfile(
                applicant.getId(),
                "Alice Zhang",
                "13800138000",
                "20240001",
                "Computer Science",
                "Interested in teaching.");

        Job jobOne = createJob(context, organiser.getId(), 2);
        jobOne.setTitle("Algorithms TA");
        jobOne.setDepartment("CS");
        jobOne.setHoursPerWeek(12);
        context.jobDao.save(jobOne);
        Job jobTwo = createJob(context, organiser.getId(), 1);
        jobTwo.setTitle("Database TA");
        jobTwo.setDepartment("CS");
        jobTwo.setHoursPerWeek(10);
        context.jobDao.save(jobTwo);
        saveApplication(context, applicant.getId(), jobOne.getId(), ApplicationStatus.ACCEPTED);
        saveApplication(context, applicant.getId(), jobTwo.getId(), ApplicationStatus.ACCEPTED);

        List<ApplicantWorkloadView> views = context.adminService.getApplicantWorkloadViews(20);

        assertEquals(1, views.size());
        ApplicantWorkloadView view = views.get(0);
        assertEquals("Alice Zhang", view.getProfile().getFullName());
        assertEquals(2, view.getAcceptedAssignments().size());
        assertEquals(22, view.getTotalHoursPerWeek());
        assertTrue(view.isOverloaded());
    }

    @Test
    void jobSupervisionViewsExposeQuotaUsageAndUnexpectedPendingState() throws Exception {
        TestContext context = createContext();
        User applicantOne = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User applicantTwo = context.userService.register("Ben", "ben@example.com", "secret1", Role.APPLICANT);
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);

        Job fullJob = createJob(context, organiser.getId(), 1);
        fullJob.setTitle("Programming TA");
        context.jobDao.save(fullJob);
        saveApplication(context, applicantOne.getId(), fullJob.getId(), ApplicationStatus.ACCEPTED);
        saveApplication(context, applicantTwo.getId(), fullJob.getId(), ApplicationStatus.PENDING);
        saveApplication(context, applicantTwo.getId(), fullJob.getId(), ApplicationStatus.REJECTED);

        List<AdminJobSupervisionView> views = context.adminService.getJobSupervisionViews();

        assertEquals(1, views.size());
        AdminJobSupervisionView view = views.get(0);
        assertEquals("Programming TA", view.getJob().getTitle());
        assertEquals("Olivia", view.getOrganiser().getUsername());
        assertEquals(1, view.getAcceptedCount());
        assertEquals(0, view.getRemainingSlots());
        assertTrue(view.isFull());
        assertEquals(1, view.getPendingCount());
        assertEquals(1, view.getRejectedCount());
        assertTrue(view.hasUnexpectedPendingOrReviewingWhenFull());
        assertFalse(view.isAcceptedOverQuota());
    }

    private TestContext createContext() throws Exception {
        Path usersFile = Files.createTempFile("users", ".json");
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path settingsFile = Files.createTempFile("settings", ".json");
        Path policiesFile = Files.createTempFile("applicant-limit-policies", ".json");

        UserService userService = new UserService(usersFile);
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        SettingsService settingsService = new SettingsService(settingsFile);
        ApplicationDaoImpl applicationDao = new ApplicationDaoImpl(applicationsFile);
        JobDaoImpl jobDao = new JobDaoImpl(jobsFile);
        ApplicantLimitPolicyDaoImpl policyDao = new ApplicantLimitPolicyDaoImpl(policiesFile);
        JobService jobService = new JobService(jobDao, applicationDao, new MessageService());
        RecruitmentPolicyService recruitmentPolicyService = new RecruitmentPolicyService(
                applicationDao,
                jobDao,
                policyDao,
                settingsService);
        AdminService adminService = new AdminService(
                userService,
                applicantService,
                settingsService,
                policyDao,
                jobService,
                applicationDao,
                recruitmentPolicyService);

        return new TestContext(userService, applicantService, adminService, applicationDao, jobDao);
    }

    private Job createJob(final TestContext context, final String organiserUserId, final int assistantQuota) {
        Job job = Job.create(organiserUserId);
        job.setTitle("TA Role");
        job.setDepartment("CS");
        job.setDescription("Support students.");
        job.setRequirements(List.of("Java"));
        job.setHoursPerWeek(8);
        job.setAssistantQuota(assistantQuota);
        job.setDeadline(LocalDate.now().plusDays(7));
        context.jobDao.save(job);
        return job;
    }

    private void saveApplication(final TestContext context, final String userId, final String jobId,
            final ApplicationStatus status) {
        Application application = Application.create(userId, jobId);
        application.setStatus(status);
        context.applicationDao.save(application);
    }

    private record TestContext(
            UserService userService,
            ApplicantService applicantService,
            AdminService adminService,
            ApplicationDaoImpl applicationDao,
            JobDaoImpl jobDao) {
    }
}
