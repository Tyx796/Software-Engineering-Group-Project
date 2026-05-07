package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.OrganiserApplicationReviewView;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrganiserApplicationReviewServiceTest {
    @Test
    void filtersByStatusAndKeywordThenSortsByMatchScore() throws Exception {
        TestContext context = createContext();
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);
        Job job = createJob(context, organiser.getId(), "Java\nSQL\nCommunication");

        User alice = createApplicant(context, "Alice Zhang", "20260001", "Computer Science", "Java, SQL, Communication");
        User bob = createApplicant(context, "Bob Li", "20260002", "Computer Science", "Java");
        User cara = createApplicant(context, "Cara Sun", "20260003", "Design", "Figma");

        var aliceApplication = context.applicationService.submitApplication(alice.getId(), job.getId());
        var bobApplication = context.applicationService.submitApplication(bob.getId(), job.getId());
        var caraApplication = context.applicationService.submitApplication(cara.getId(), job.getId());
        context.applicationService.updateStatusForOrganiser(organiser.getId(), aliceApplication.getId(), ApplicationStatus.REJECTED);
        context.applicationService.openApplicationForOrganiser(organiser.getId(), bobApplication.getId());

        List<OrganiserApplicationReviewView> filtered = context.reviewService.getReviewViews(
                organiser.getId(),
                job.getId(),
                "reviewing",
                "computer",
                "match");

        assertEquals(1, filtered.size());
        assertEquals("Bob Li", filtered.get(0).getApplicant().getFullName());
        assertEquals(33, filtered.get(0).getSkillMatch().getMatchScore());

        List<OrganiserApplicationReviewView> sorted = context.reviewService.getReviewViews(
                organiser.getId(),
                job.getId(),
                "",
                "",
                "match");

        assertEquals(
                List.of("Alice Zhang", "Bob Li", "Cara Sun"),
                sorted.stream().map(view -> view.getApplicant().getFullName()).toList());
    }

    @Test
    void sortsByStatusWhenRequested() throws Exception {
        TestContext context = createContext();
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);
        Job job = createJob(context, organiser.getId(), "Java");

        User pendingApplicant = createApplicant(context, "Pending User", "20260001", "CS", "Java");
        User reviewingApplicant = createApplicant(context, "Reviewing User", "20260002", "CS", "Java");
        User rejectedApplicant = createApplicant(context, "Rejected User", "20260003", "CS", "Java");

        var pending = context.applicationService.submitApplication(pendingApplicant.getId(), job.getId());
        var reviewing = context.applicationService.submitApplication(reviewingApplicant.getId(), job.getId());
        var rejected = context.applicationService.submitApplication(rejectedApplicant.getId(), job.getId());
        context.applicationService.openApplicationForOrganiser(organiser.getId(), reviewing.getId());
        context.applicationService.updateStatusForOrganiser(organiser.getId(), rejected.getId(), ApplicationStatus.REJECTED);

        List<OrganiserApplicationReviewView> views = context.reviewService.getReviewViews(
                organiser.getId(),
                job.getId(),
                "",
                "",
                "status");

        assertEquals(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.REJECTED, ApplicationStatus.REVIEWING),
                views.stream().map(view -> view.getApplication().getStatus()).toList());
    }

    @Test
    void reviewViewGenerationHandlesFiftyPlusApplicationsQuickly() throws Exception {
        TestContext context = createContext();
        User organiser = context.userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);
        Job job = createJob(context, organiser.getId(), "Java\nCommunication");

        for (int index = 0; index < 60; index++) {
            User applicant = createApplicant(
                    context,
                    "Applicant " + index,
                    "2026" + String.format("%04d", index),
                    "Computer Science",
                    index % 2 == 0 ? "Java, Communication" : "Java");
            context.applicationService.submitApplication(applicant.getId(), job.getId());
        }

        assertTimeoutPreemptively(Duration.ofSeconds(3), () ->
                context.reviewService.getReviewViews(organiser.getId(), job.getId(), "", "", "match"));
    }

    private TestContext createContext() throws Exception {
        Path usersFile = Files.createTempFile("users", ".json");
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path settingsFile = Files.createTempFile("settings", ".json");
        Path policiesFile = Files.createTempFile("applicant-limit-policies", ".json");

        UserService userService = new UserService(usersFile);
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        ApplicationDaoImpl applicationDao = new ApplicationDaoImpl(applicationsFile);
        JobDaoImpl jobDao = new JobDaoImpl(jobsFile);
        JobService jobService = new JobService(jobDao, applicationDao, new MessageService());
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile));
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
        OrganiserApplicationReviewService reviewService = new OrganiserApplicationReviewService(
                jobService,
                applicationService,
                applicantService,
                new SkillMatchService());

        return new TestContext(userService, applicantService, cvService, jobService, applicationService, reviewService);
    }

    private Job createJob(final TestContext context, final String organiserUserId, final String requirements) {
        return context.jobService.createJob(
                organiserUserId,
                "TA Role",
                "Computer Science",
                "Support labs.",
                requirements,
                8,
                5,
                LocalDate.now().plusDays(7));
    }

    private User createApplicant(final TestContext context, final String name, final String studentId,
            final String programme, final String skills) {
        User user = context.userService.register(name, name.toLowerCase().replace(" ", ".") + "@example.com", "secret1",
                Role.APPLICANT);
        context.applicantService.createProfile(
                user.getId(),
                name,
                "+86 13800000000",
                studentId,
                programme,
                "Ready",
                skills,
                "Monday");
        context.cvService.uploadCV(user.getId(), user.getUsername() + ".pdf", new ByteArrayInputStream("cv".getBytes()));
        return user;
    }

    private record TestContext(
            UserService userService,
            ApplicantService applicantService,
            CvService cvService,
            JobService jobService,
            ApplicationService applicationService,
            OrganiserApplicationReviewService reviewService) {
    }
}
