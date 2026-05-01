package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Iteration3WorkflowTest {
    @Test
    void applicantCanSeeUpdatedResultAfterOrganiserReviewsApplication() throws Exception {
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
        JobService jobService = new JobService(jobDao);
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);
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

        User applicant = userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User organiser = userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);

        applicantService.createProfile(
                applicant.getId(),
                "Alice Chen",
                "+1 202 555 0199",
                "20269999",
                "Software Engineering",
                "Ready to support labs and tutorials.",
                "Java, Git, Communication",
                "Monday, Wednesday");
        cvService.uploadCV(applicant.getId(), "cv.pdf", new ByteArrayInputStream("pdf-data".getBytes()));

        Job job = jobService.createJob(
                organiser.getId(),
                "Software Testing TA",
                "Computer Science",
                "Support testing labs and coursework help sessions.",
                "JUnit\nGit\nCommunication",
                8,
                LocalDate.now().plusDays(7));

        Application application = applicationService.submitApplication(applicant.getId(), job.getId());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertEquals(1, applicationService.getApplicationsForOrganiserJob(organiser.getId(), job.getId()).size());
        assertEquals(application.getId(),
                applicationService.getApplicationDetailsForOrganiser(organiser.getId(), application.getId()).getId());

        Application reviewing = applicationService.openApplicationForOrganiser(organiser.getId(), application.getId());
        assertEquals(ApplicationStatus.REVIEWING, reviewing.getStatus());
        assertNotNull(reviewing.getReviewedAt());

        applicationService.updateStatusForOrganiser(organiser.getId(), application.getId(), ApplicationStatus.ACCEPTED);

        assertEquals(
                ApplicationStatus.ACCEPTED,
                applicationService.getApplicationsByApplicant(applicant.getId()).get(0).getStatus());
        assertEquals(
                ApplicationStatus.ACCEPTED,
                applicationService.getApplicationDetails(application.getId()).orElseThrow().getStatus());
    }

    @Test
    void finalAcceptanceFillsSingleSlotJobAndAutoRejectsOtherApplicants() throws Exception {
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
        JobService jobService = new JobService(jobDao);
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);
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

        User applicantOne = userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User applicantTwo = userService.register("Bob", "bob@example.com", "secret1", Role.APPLICANT);
        User organiser = userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);

        applicantService.createProfile(
                applicantOne.getId(),
                "Alice Chen",
                "+1 202 555 0199",
                "20269999",
                "Software Engineering",
                "Ready to support labs and tutorials.",
                "Java, Git, Communication",
                "Monday, Wednesday");
        applicantService.createProfile(
                applicantTwo.getId(),
                "Bob Chen",
                "+1 202 555 0188",
                "20269998",
                "Software Engineering",
                "Ready to support labs and tutorials.",
                "Java, Git, Communication",
                "Tuesday, Thursday");
        cvService.uploadCV(applicantOne.getId(), "alice.pdf", new ByteArrayInputStream("pdf-data".getBytes()));
        cvService.uploadCV(applicantTwo.getId(), "bob.pdf", new ByteArrayInputStream("pdf-data".getBytes()));

        Job job = jobService.createJob(
                organiser.getId(),
                "Software Testing TA",
                "Computer Science",
                "Support testing labs and coursework help sessions.",
                "JUnit\nGit\nCommunication",
                8,
                LocalDate.now().plusDays(7));

        Application first = applicationService.submitApplication(applicantOne.getId(), job.getId());
        Application second = applicationService.submitApplication(applicantTwo.getId(), job.getId());

        applicationService.updateStatusForOrganiser(organiser.getId(), first.getId(), ApplicationStatus.ACCEPTED);

        assertEquals(ApplicationStatus.ACCEPTED,
                applicationService.getApplicationDetails(first.getId()).orElseThrow().getStatus());
        assertEquals(ApplicationStatus.REJECTED,
                applicationService.getApplicationDetails(second.getId()).orElseThrow().getStatus());
    }
}
