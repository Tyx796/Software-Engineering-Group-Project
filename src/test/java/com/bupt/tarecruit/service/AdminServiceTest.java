package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
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
                recruitmentPolicyService);

        return new TestContext(userService, adminService, applicationDao, jobDao);
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
            AdminService adminService,
            ApplicationDaoImpl applicationDao,
            JobDaoImpl jobDao) {
    }
}
