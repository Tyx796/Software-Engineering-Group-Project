package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.ApplicantWorkloadView;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.JobRecommendationView;
import com.bupt.tarecruit.model.OrganiserApplicationReviewView;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.SkillMatchView;
import com.bupt.tarecruit.model.User;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class Iteration4WorkflowTest {
    @Test
    void applicantGetsRecommendationsAndSkillGapSignalsFromProfileSkills() throws Exception {
        TestContext context = createContext();
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);
        User applicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User filler = context.userService.register("Bob", "bob@example.com", "secret1", Role.APPLICANT);

        context.applicantService.createProfile(
                applicant.getId(),
                "Alice Chen",
                "+86 13800000000",
                "20260001",
                "Software Engineering",
                "Ready to support labs.",
                "Java, Git, Communication",
                "Monday, Wednesday");
        context.applicantService.createProfile(
                filler.getId(),
                "Bob Chen",
                "+86 13800000001",
                "20260002",
                "Computer Science",
                "Ready to support labs.",
                "Python, SQL",
                "Tuesday");
        context.cvService.uploadCV(applicant.getId(), "alice.pdf", new ByteArrayInputStream("cv".getBytes()));
        context.cvService.uploadCV(filler.getId(), "bob.pdf", new ByteArrayInputStream("cv".getBytes()));

        Job bestFit = createJob(context, organiser, "Algorithms TA", "Java\nGit\nCommunication", 8, 1,
                LocalDate.now().plusDays(5));
        Job skillGap = createJob(context, organiser, "Databases TA", "Java\nSQL", 10, 1, LocalDate.now().plusDays(3));
        Job alreadyApplied = createJob(context, organiser, "Networks TA", "Java\nDocker", 6, 1,
                LocalDate.now().plusDays(4));
        Job fullJob = createJob(context, organiser, "AI Lab TA", "Python", 12, 1, LocalDate.now().plusDays(2));
        Job expiredJob = createJob(context, organiser, "Legacy Systems TA", "Java", 4, 1, LocalDate.now().plusDays(6));
        expiredJob.setDeadline(LocalDate.now().minusDays(1));
        context.jobService.saveJob(expiredJob);

        context.applicationService.submitApplication(applicant.getId(), alreadyApplied.getId());
        Application fullJobApplication = context.applicationService.submitApplication(filler.getId(), fullJob.getId());
        context.applicationService.updateStatusForOrganiser(
                organiser.getId(),
                fullJobApplication.getId(),
                ApplicationStatus.ACCEPTED);

        List<JobRecommendationView> recommendations =
                context.recommendationService.getRecommendedJobsForApplicant(applicant.getId(), 5);

        assertEquals(List.of(bestFit.getId(), skillGap.getId()),
                recommendations.stream().map(view -> view.getJob().getId()).toList());
        assertEquals(List.of(100, 50),
                recommendations.stream().map(JobRecommendationView::getMatchScore).toList());

        SkillMatchView skillGapView = context.skillMatchService.calculateMatch(
                context.applicantService.findByUserId(applicant.getId()).orElseThrow(),
                skillGap);
        assertEquals(50, skillGapView.getMatchScore());
        assertEquals(List.of("Java"), skillGapView.getMatchedSkills());
        assertEquals(List.of("SQL"), skillGapView.getMissingSkills());
    }

    @Test
    void organiserReviewAndAdminWorkloadExportReflectIterationFourSignals() throws Exception {
        TestContext context = createContext();
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);
        User strongApplicant = context.userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User weakerApplicant = context.userService.register("Ben", "ben@example.com", "secret1", Role.APPLICANT);

        context.applicantService.createProfile(
                strongApplicant.getId(),
                "Alice Chen",
                "+86 13800000000",
                "20260001",
                "Software Engineering",
                "Ready to support labs.",
                "Java, Git, Communication",
                "Monday, Wednesday");
        context.applicantService.createProfile(
                weakerApplicant.getId(),
                "Ben Li",
                "+86 13800000002",
                "20260003",
                "Software Engineering",
                "Ready to support labs.",
                "Java",
                "Friday");
        context.cvService.uploadCV(strongApplicant.getId(), "alice.pdf", new ByteArrayInputStream("cv".getBytes()));
        context.cvService.uploadCV(weakerApplicant.getId(), "ben.pdf", new ByteArrayInputStream("cv".getBytes()));

        Job reviewJob = createJob(context, organiser, "Software Testing TA", "Java\nGit\nCommunication", 12, 2,
                LocalDate.now().plusDays(7));
        Application strongApplication = context.applicationService.submitApplication(strongApplicant.getId(),
                reviewJob.getId());
        context.applicationService.submitApplication(weakerApplicant.getId(), reviewJob.getId());

        List<OrganiserApplicationReviewView> reviewViews = context.reviewService.getReviewViews(
                organiser.getId(),
                reviewJob.getId(),
                null,
                null,
                "match");

        assertEquals(2, reviewViews.size());
        assertEquals(strongApplicant.getId(), reviewViews.get(0).getApplication().getApplicantUserId());
        assertEquals(100, reviewViews.get(0).getSkillMatch().getMatchScore());
        assertTrue(reviewViews.get(0).getSkillMatch().getMatchScore()
                > reviewViews.get(1).getSkillMatch().getMatchScore());

        context.applicationService.updateStatusForOrganiser(
                organiser.getId(),
                strongApplication.getId(),
                ApplicationStatus.ACCEPTED);

        Job secondAcceptedJob = createJob(context, organiser, "Database Support TA", "Java\nSQL", 12, 1,
                LocalDate.now().plusDays(8));
        Application secondAcceptedApplication = context.applicationService.submitApplication(
                strongApplicant.getId(),
                secondAcceptedJob.getId());
        context.applicationService.updateStatusForOrganiser(
                organiser.getId(),
                secondAcceptedApplication.getId(),
                ApplicationStatus.ACCEPTED);

        ApplicantWorkloadView workloadView = context.adminService.getApplicantWorkloadViews(20).stream()
                .filter(view -> strongApplicant.getId().equals(view.getUser().getId()))
                .findFirst()
                .orElseThrow();

        assertTrue(workloadView.isOverloaded());
        assertEquals(24, workloadView.getTotalHoursPerWeek());
        assertEquals("Overloaded", workloadView.getWorkloadStatusLabel());

        String csv = context.csvService.export(context.adminService.getApplicantWorkloadViews(20));

        assertTrue(csv.contains("Applicant Name,Email,Student ID,Programme,Total Hours Per Week,Threshold,Status,Assigned Jobs"));
        assertTrue(csv.contains("alice@example.com"));
        assertTrue(csv.contains("Overloaded"));
        assertTrue(csv.contains("Software Testing TA (12 hours/week); Database Support TA (12 hours/week)"));
        assertFalse(csv.contains("ben@example.com"));
    }

    private TestContext createContext() throws Exception {
        Path usersFile = Files.createTempFile("users", ".json");
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path settingsFile = Files.createTempFile("settings", ".json");
        Path policiesFile = Files.createTempFile("applicant-limit-policies", ".json");
        Path cvRootDirectory = Files.createTempDirectory("cv-root");

        UserService userService = new UserService(usersFile);
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        ApplicationDaoImpl applicationDao = new ApplicationDaoImpl(applicationsFile);
        JobDaoImpl jobDao = new JobDaoImpl(jobsFile);
        JobService jobService = new JobService(jobDao, applicationDao, new MessageService());
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);
        SettingsService settingsService = new SettingsService(settingsFile);
        ApplicantLimitPolicyDaoImpl policyDao = new ApplicantLimitPolicyDaoImpl(policiesFile);
        RecruitmentPolicyService recruitmentPolicyService = new RecruitmentPolicyService(
                applicationDao,
                jobDao,
                policyDao,
                settingsService);
        ApplicationService applicationService = new ApplicationService(
                applicantService,
                jobService,
                cvService,
                applicationDao,
                new MessageService(),
                recruitmentPolicyService);
        SkillMatchService skillMatchService = new SkillMatchService();
        JobRecommendationService recommendationService = new JobRecommendationService(
                applicantService,
                jobService,
                applicationService,
                recruitmentPolicyService,
                skillMatchService);
        OrganiserApplicationReviewService reviewService = new OrganiserApplicationReviewService(
                jobService,
                applicationService,
                applicantService,
                skillMatchService);
        AdminService adminService = new AdminService(
                userService,
                applicantService,
                settingsService,
                policyDao,
                jobService,
                applicationDao,
                recruitmentPolicyService);

        return new TestContext(
                userService,
                applicantService,
                jobService,
                cvService,
                applicationService,
                recommendationService,
                reviewService,
                adminService,
                skillMatchService,
                new WorkloadReportCsvService());
    }

    private Job createJob(final TestContext context,
            final User organiser,
            final String title,
            final String requirements,
            final int hoursPerWeek,
            final int assistantQuota,
            final LocalDate deadline) {
        return context.jobService.createJob(
                organiser.getId(),
                title,
                "Computer Science",
                "Support labs and tutorials.",
                requirements,
                hoursPerWeek,
                assistantQuota,
                deadline);
    }

    private record TestContext(
            UserService userService,
            ApplicantService applicantService,
            JobService jobService,
            CvService cvService,
            ApplicationService applicationService,
            JobRecommendationService recommendationService,
            OrganiserApplicationReviewService reviewService,
            AdminService adminService,
            SkillMatchService skillMatchService,
            WorkloadReportCsvService csvService) {
    }
}
