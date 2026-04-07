package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class Iteration1WorkflowTest {
    @Test
    void applicantAndOrganiserCoreWorkflowCompletes() throws Exception {
        Path usersFile = Files.createTempFile("users", ".json");
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");

        UserService userService = new UserService(usersFile);
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        JobService jobService = new JobService(jobsFile);

        User applicant = userService.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);
        User organiser = userService.register("Olivia", "olivia@example.com", "secret1", Role.ORGANISER);

        assertEquals(Role.APPLICANT, userService.login("alice@example.com", "secret1").getRole());
        assertEquals(Role.ORGANISER, userService.login("olivia@example.com", "secret1").getRole());

        Applicant profile = applicantService.createProfile(
                applicant.getId(),
                "Alice Chen",
                "+1 202 555 0199",
                "20269999",
                "Software Engineering",
                "Ready to support labs and tutorials.");
        applicantService.attachCv(applicant.getId(), "cv.pdf");

        assertEquals("Alice Chen", profile.getFullName());
        assertEquals("cv.pdf", applicantService.findByUserId(applicant.getId()).orElseThrow().getCvFileName());

        Job job = jobService.createJob(
                organiser.getId(),
                "Software Testing TA",
                "Computer Science",
                "Support testing labs and coursework help sessions.",
                "JUnit\nGit\nCommunication",
                8,
                LocalDate.now().plusDays(7));

        assertEquals(1, jobService.getJobsByOrganiser(organiser.getId()).size());
        assertFalse(jobService.getAvailableJobs().isEmpty());
        assertTrue(jobService.findById(job.getId()).isPresent());
        assertEquals(3, job.getRequirements().size());
    }
}
