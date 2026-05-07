package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.JobRecommendationView;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class JobRecommendationServiceTest {
    @Test
    void recommendationsAreOrderedByMatchScoreThenDeadline() throws Exception {
        TestContext context = createContext();
        context.applicantService.createProfile(
                "user-1",
                "Alice",
                "+86 13800000000",
                "20260001",
                "Computer Science",
                "Ready",
                "Java, SQL, Communication",
                "Monday");

        Job strongMatch = createJob(context, "Java\nSQL\nCommunication", LocalDate.now().plusDays(5));
        strongMatch.setTitle("Strong Match");
        context.jobService.saveJob(strongMatch);

        Job mediumEarlier = createJob(context, "Java\nPython", LocalDate.now().plusDays(3));
        mediumEarlier.setTitle("Medium Earlier");
        context.jobService.saveJob(mediumEarlier);

        Job mediumLater = createJob(context, "Java\nTesting", LocalDate.now().plusDays(7));
        mediumLater.setTitle("Medium Later");
        context.jobService.saveJob(mediumLater);

        List<JobRecommendationView> recommendations =
                context.recommendationService.getRecommendedJobsForApplicant("user-1", 5);

        assertEquals(List.of("Strong Match", "Medium Earlier", "Medium Later"),
                recommendations.stream().map(view -> view.getJob().getTitle()).toList());
        assertEquals(List.of(100, 50, 50),
                recommendations.stream().map(JobRecommendationView::getMatchScore).toList());
    }

    @Test
    void recommendationsExcludeAppliedClosedFullAndExpiredJobs() throws Exception {
        TestContext context = createContext();
        context.applicantService.createProfile(
                "user-1",
                "Alice",
                "+86 13800000000",
                "20260001",
                "Computer Science",
                "Ready",
                "Java, SQL",
                "Monday");
        context.applicantService.createProfile(
                "user-2",
                "Bob",
                "+86 13800000001",
                "20260002",
                "Computer Science",
                "Ready",
                "Java",
                "Tuesday");
        context.cvService.uploadCV("user-1", "alice.pdf", new java.io.ByteArrayInputStream("cv".getBytes()));
        context.cvService.uploadCV("user-2", "bob.pdf", new java.io.ByteArrayInputStream("cv".getBytes()));

        Job eligible = createJob(context, "Java", LocalDate.now().plusDays(5));
        eligible.setTitle("Eligible");
        context.jobService.saveJob(eligible);

        Job applied = createJob(context, "Java", LocalDate.now().plusDays(5));
        applied.setTitle("Applied");
        context.jobService.saveJob(applied);
        context.applicationService.submitApplication("user-1", applied.getId());

        Job closed = createJob(context, "Java", LocalDate.now().plusDays(5));
        closed.setTitle("Closed");
        closed.setStatus("CLOSED");
        context.jobService.saveJob(closed);

        Job expired = createJob(context, "Java", LocalDate.now().plusDays(5));
        expired.setTitle("Expired");
        expired.setDeadline(LocalDate.now().minusDays(1));
        context.jobService.saveJob(expired);

        Job full = createJob(context, "Java", LocalDate.now().plusDays(5));
        full.setTitle("Full");
        context.jobService.saveJob(full);
        var accepted = context.applicationService.submitApplication("user-2", full.getId());
        context.applicationService.updateStatusForOrganiser("organiser-1", accepted.getId(), ApplicationStatus.ACCEPTED);

        List<JobRecommendationView> recommendations =
                context.recommendationService.getRecommendedJobsForApplicant("user-1", 10);

        assertEquals(1, recommendations.size());
        assertEquals("Eligible", recommendations.get(0).getJob().getTitle());
    }

    @Test
    void recommendationsRequireApplicantProfile() throws Exception {
        TestContext context = createContext();

        assertTrue(context.recommendationService.getRecommendedJobsForApplicant("missing-user", 5).isEmpty());
    }

    private TestContext createContext() throws Exception {
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path settingsFile = Files.createTempFile("settings", ".json");
        Path policiesFile = Files.createTempFile("applicant-limit-policies", ".json");

        ApplicantService applicantService = new ApplicantService(applicantsFile);
        ApplicationDaoImpl applicationDao = new ApplicationDaoImpl(applicationsFile);
        JobDaoImpl jobDao = new JobDaoImpl(jobsFile);
        JobService jobService = new JobService(jobDao, applicationDao, new MessageService());
        CvService cvService = new CvService(applicantService, new com.bupt.tarecruit.dao.impl.CvDaoImpl(cvsFile));
        SettingsService settingsService = new SettingsService(settingsFile);
        RecruitmentPolicyService recruitmentPolicyService = new RecruitmentPolicyService(
                applicationDao,
                jobDao,
                new ApplicantLimitPolicyDaoImpl(policiesFile),
                settingsService);
        ApplicationService applicationService = new ApplicationService(
                applicantService,
                jobService,
                cvService,
                applicationDao,
                new MessageService(),
                recruitmentPolicyService);
        JobRecommendationService recommendationService = new JobRecommendationService(
                applicantService,
                jobService,
                applicationService,
                recruitmentPolicyService,
                new SkillMatchService());

        return new TestContext(
                applicantService,
                jobService,
                cvService,
                applicationService,
                recommendationService);
    }

    private Job createJob(final TestContext context, final String requirements, final LocalDate deadline) {
        return context.jobService.createJob(
                "organiser-1",
                "TA Role",
                "Computer Science",
                "Support labs.",
                requirements,
                8,
                1,
                deadline);
    }

    private record TestContext(
            ApplicantService applicantService,
            JobService jobService,
            CvService cvService,
            ApplicationService applicationService,
            JobRecommendationService recommendationService) {
    }
}
