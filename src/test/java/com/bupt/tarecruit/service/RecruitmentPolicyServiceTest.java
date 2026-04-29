package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.ApplicantLimitPolicy;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecruitmentPolicyServiceTest {
    @Test
    void resolveApplicantApplicationLimitUsesGlobalDefaultWhenNoOverrideExists() throws Exception {
        TestContext context = createContext();
        context.settingsService.updateDefaultApplicantApplicationLimit(4);

        assertEquals(4, context.policyService.resolveApplicantApplicationLimit("user-1"));
    }

    @Test
    void resolveApplicantApplicationLimitUsesOverrideBeforeGlobalDefault() throws Exception {
        TestContext context = createContext();
        context.settingsService.updateDefaultApplicantApplicationLimit(4);
        saveOverride(context, "user-1", 6);

        assertEquals(6, context.policyService.resolveApplicantApplicationLimit("user-1"));
    }

    @Test
    void resolveApplicantApplicationLimitWorksWithoutApplicantProfile() throws Exception {
        TestContext context = createContext();
        context.settingsService.updateDefaultApplicantApplicationLimit(3);
        saveOverride(context, "user-without-profile", 5);

        assertEquals(5, context.policyService.resolveApplicantApplicationLimit("user-without-profile"));
    }

    @Test
    void countActiveApplicationsIncludesPendingReviewingAccepted() throws Exception {
        TestContext context = createContext();
        Job jobOne = createJob(context, "organiser-1", 2);
        Job jobTwo = createJob(context, "organiser-1", 2);
        Job jobThree = createJob(context, "organiser-1", 2);

        saveApplication(context, "user-1", jobOne.getId(), ApplicationStatus.PENDING);
        saveApplication(context, "user-1", jobTwo.getId(), ApplicationStatus.REVIEWING);
        saveApplication(context, "user-1", jobThree.getId(), ApplicationStatus.ACCEPTED);

        assertEquals(3, context.policyService.countActiveApplications("user-1"));
    }

    @Test
    void countActiveApplicationsExcludesRejectedWithdrawnCancelled() throws Exception {
        TestContext context = createContext();
        Job jobOne = createJob(context, "organiser-1", 2);
        Job jobTwo = createJob(context, "organiser-1", 2);
        Job jobThree = createJob(context, "organiser-1", 2);

        saveApplication(context, "user-1", jobOne.getId(), ApplicationStatus.REJECTED);
        saveApplication(context, "user-1", jobTwo.getId(), ApplicationStatus.WITHDRAWN);
        saveApplication(context, "user-1", jobThree.getId(), ApplicationStatus.CANCELLED);

        assertEquals(0, context.policyService.countActiveApplications("user-1"));
    }

    @Test
    void hasReachedApplicationLimitReturnsTrueWhenActiveCountEqualsLimit() throws Exception {
        TestContext context = createContext();
        context.settingsService.updateDefaultApplicantApplicationLimit(1);
        Job job = createJob(context, "organiser-1", 1);
        saveApplication(context, "user-1", job.getId(), ApplicationStatus.PENDING);

        assertTrue(context.policyService.hasReachedApplicationLimit("user-1"));
    }

    @Test
    void hasReachedApplicationLimitReturnsTrueWhenAlreadyOverLimit() throws Exception {
        TestContext context = createContext();
        context.settingsService.updateDefaultApplicantApplicationLimit(1);
        Job jobOne = createJob(context, "organiser-1", 1);
        Job jobTwo = createJob(context, "organiser-1", 1);
        saveApplication(context, "user-1", jobOne.getId(), ApplicationStatus.PENDING);
        saveApplication(context, "user-1", jobTwo.getId(), ApplicationStatus.REVIEWING);

        assertTrue(context.policyService.hasReachedApplicationLimit("user-1"));
    }

    @Test
    void hasReachedApplicationLimitReturnsFalseWhenBelowLimit() throws Exception {
        TestContext context = createContext();
        context.settingsService.updateDefaultApplicantApplicationLimit(2);
        Job job = createJob(context, "organiser-1", 1);
        saveApplication(context, "user-1", job.getId(), ApplicationStatus.PENDING);

        assertFalse(context.policyService.hasReachedApplicationLimit("user-1"));
    }

    @Test
    void countAcceptedApplicationsCountsOnlyAccepted() throws Exception {
        TestContext context = createContext();
        Job job = createJob(context, "organiser-1", 3);
        saveApplication(context, "user-1", job.getId(), ApplicationStatus.ACCEPTED);
        saveApplication(context, "user-2", job.getId(), ApplicationStatus.REVIEWING);
        saveApplication(context, "user-3", job.getId(), ApplicationStatus.REJECTED);

        assertEquals(1, context.policyService.countAcceptedApplications(job.getId()));
    }

    @Test
    void isJobFullReturnsTrueWhenAcceptedCountEqualsQuota() throws Exception {
        TestContext context = createContext();
        Job job = createJob(context, "organiser-1", 1);
        saveApplication(context, "user-1", job.getId(), ApplicationStatus.ACCEPTED);

        assertTrue(context.policyService.isJobFull(job.getId()));
        assertEquals(0, context.policyService.remainingAssistantSlots(job.getId()));
    }

    @Test
    void isJobFullReturnsTrueWhenAcceptedCountExceedsQuota() throws Exception {
        TestContext context = createContext();
        Job job = createJob(context, "organiser-1", 1);
        saveApplication(context, "user-1", job.getId(), ApplicationStatus.ACCEPTED);
        saveApplication(context, "user-2", job.getId(), ApplicationStatus.ACCEPTED);

        assertTrue(context.policyService.isJobFull(job.getId()));
        assertEquals(0, context.policyService.remainingAssistantSlots(job.getId()));
    }

    @Test
    void isJobFullReturnsFalseWhenAcceptedCountBelowQuota() throws Exception {
        TestContext context = createContext();
        Job job = createJob(context, "organiser-1", 2);
        saveApplication(context, "user-1", job.getId(), ApplicationStatus.ACCEPTED);

        assertFalse(context.policyService.isJobFull(job.getId()));
        assertEquals(1, context.policyService.remainingAssistantSlots(job.getId()));
    }

    @Test
    void zeroQuotaIsTreatedAsAlreadyFull() throws Exception {
        TestContext context = createContext();
        Job job = createJob(context, "organiser-1", 0);

        assertTrue(context.policyService.isJobFull(job.getId()));
        assertEquals(0, context.policyService.remainingAssistantSlots(job.getId()));
    }

    @Test
    void legacyJobWithoutAssistantQuotaIsTreatedAsQuotaOne() throws Exception {
        TestContext context = createContext();
        Job job = Job.create("organiser-1");
        job.setAssistantQuota(null);
        job.setTitle("Legacy Job");
        job.setDepartment("CS");
        job.setDescription("Legacy");
        job.setRequirements(java.util.List.of("Java"));
        job.setHoursPerWeek(8);
        job.setDeadline(LocalDate.now().plusDays(7));
        context.jobDao.save(job);

        assertEquals(1, context.policyService.remainingAssistantSlots(job.getId()));
        assertFalse(context.policyService.isJobFull(job.getId()));

        saveApplication(context, "user-1", job.getId(), ApplicationStatus.ACCEPTED);
        assertTrue(context.policyService.isJobFull(job.getId()));
    }

    private TestContext createContext() throws Exception {
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path policiesFile = Files.createTempFile("applicant-limit-policies", ".json");
        Path settingsFile = Files.createTempFile("settings", ".json");

        ApplicationDaoImpl applicationDao = new ApplicationDaoImpl(applicationsFile);
        JobDaoImpl jobDao = new JobDaoImpl(jobsFile);
        ApplicantLimitPolicyDaoImpl policyDao = new ApplicantLimitPolicyDaoImpl(policiesFile);
        SettingsService settingsService = new SettingsService(settingsFile);
        RecruitmentPolicyService policyService = new RecruitmentPolicyService(
                applicationDao,
                jobDao,
                policyDao,
                settingsService);

        return new TestContext(applicationDao, jobDao, policyDao, settingsService, policyService);
    }

    private Job createJob(final TestContext context, final String organiserUserId, final Integer assistantQuota) {
        Job job = Job.create(organiserUserId);
        job.setTitle("TA Role");
        job.setDepartment("CS");
        job.setDescription("Support students.");
        job.setRequirements(java.util.List.of("Java"));
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

    private void saveOverride(final TestContext context, final String userId, final int limit) {
        ApplicantLimitPolicy policy = new ApplicantLimitPolicy();
        policy.setUserId(userId);
        policy.setApplicationLimitOverride(limit);
        policy.setUpdatedAt(Instant.now());
        context.policyDao.save(policy);
    }

    private static final class TestContext {
        private final ApplicationDaoImpl applicationDao;
        private final JobDaoImpl jobDao;
        private final ApplicantLimitPolicyDaoImpl policyDao;
        private final SettingsService settingsService;
        private final RecruitmentPolicyService policyService;

        private TestContext(final ApplicationDaoImpl applicationDao,
                final JobDaoImpl jobDao,
                final ApplicantLimitPolicyDaoImpl policyDao,
                final SettingsService settingsService,
                final RecruitmentPolicyService policyService) {
            this.applicationDao = applicationDao;
            this.jobDao = jobDao;
            this.policyDao = policyDao;
            this.settingsService = settingsService;
            this.policyService = policyService;
        }
    }
}
