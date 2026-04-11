package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
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

class Iteration2WorkflowTest {
    @Test
    void applicantCanCompleteProfileManageCvSearchApplyAndReviewStatus() throws Exception {
        Path usersFile = Files.createTempFile("users", ".json");
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path cvRootDirectory = Files.createTempDirectory("cv-root");

        UserService userService = new UserService(usersFile);
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        JobService jobService = new JobService(jobsFile);
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);
        ApplicationService applicationService = new ApplicationService(
                applicantService,
                jobService,
                cvService,
                new ApplicationDaoImpl(applicationsFile));

        User applicant = userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User organiser = userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);

        assertEquals(Role.APPLICANT, userService.login("alice@example.com", "secret1").getRole());
        assertEquals(Role.ORGANISER, userService.login("olivia@example.com", "secret1").getRole());

        applicantService.createProfile(
                applicant.getId(),
                "Alice Chen",
                "+1 202 555 0199",
                "20269999",
                "Software Engineering",
                "Ready to support labs and tutorials.",
                "Java, Git, Communication",
                "Monday, Wednesday");

        assertTrue(applicantService.hasCompleteProfile(applicant.getId()));
        assertEquals(3, applicantService.findByUserId(applicant.getId()).orElseThrow().getSkills().size());

        cvService.uploadCV(applicant.getId(), "cv.pdf", new ByteArrayInputStream("pdf-data".getBytes()));
        assertTrue(cvService.hasUploadedCv(applicant.getId()));
        assertEquals("cv.pdf", cvService.currentCvFileName(applicant.getId()).orElseThrow());

        cvService.replaceCV(applicant.getId(), "cv.docx", new ByteArrayInputStream("docx-data".getBytes()));
        assertEquals("cv.docx", cvService.currentCvFileName(applicant.getId()).orElseThrow());
        assertFalse(Files.exists(cvRootDirectory.resolve(applicant.getId()).resolve("cv.pdf")));
        assertTrue(Files.exists(cvRootDirectory.resolve(applicant.getId()).resolve("cv.docx")));

        Job softwareTestingJob = jobService.createJob(
                organiser.getId(),
                "Software Testing TA",
                "Computer Science",
                "Support testing labs and coursework help sessions.",
                "JUnit\nGit\nCommunication",
                8,
                LocalDate.now().plusDays(7));
        jobService.createJob(
                organiser.getId(),
                "Database TA",
                "Computer Science",
                "Support database labs.",
                "SQL\nER Design",
                8,
                LocalDate.now().plusDays(10));

        assertEquals(1, jobService.searchAvailableJobs("testing").size());
        assertEquals(softwareTestingJob.getId(), jobService.searchAvailableJobs("testing").get(0).getId());

        Application application = applicationService.submitApplication(applicant.getId(), softwareTestingJob.getId());

        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertEquals(1, applicationService.getApplicationsByApplicant(applicant.getId()).size());
        assertEquals(application.getId(),
                applicationService.findByApplicantAndJob(applicant.getId(), softwareTestingJob.getId()).orElseThrow().getId());
        assertEquals(application.getId(),
                applicationService.getApplicationDetails(application.getId()).orElseThrow().getId());
        assertEquals(1, applicationService.getApplicationsByJob(softwareTestingJob.getId()).size());
    }
}
