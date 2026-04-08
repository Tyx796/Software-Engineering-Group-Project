package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.model.Job;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class JobServiceTest {
    @Test
    void getAvailableJobsReturnsOnlyOpenAndUnexpiredJobsSortedByDeadline() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        Job laterOpenJob = service.createJob(
                "organiser-1",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));

        Job earlierOpenJob = service.createJob(
                "organiser-1",
                "Database TA",
                "Computer Science",
                "Support database labs.",
                "SQL\nER Design",
                8,
                LocalDate.now().plusDays(3));

        Job closedJob = Job.create("organiser-1");
        closedJob.setTitle("Closed Job");
        closedJob.setDepartment("Computer Science");
        closedJob.setDescription("Closed.");
        closedJob.setRequirements(List.of("Java"));
        closedJob.setHoursPerWeek(6);
        closedJob.setDeadline(LocalDate.now().plusDays(2));
        closedJob.setStatus("CLOSED");
        service.saveJob(closedJob);

        List<Job> availableJobs = service.getAvailableJobs();

        assertEquals(2, availableJobs.size());
        assertEquals(earlierOpenJob.getId(), availableJobs.get(0).getId());
        assertEquals(laterOpenJob.getId(), availableJobs.get(1).getId());
    }

    @Test
    void searchAvailableJobsMatchesTitleDepartmentAndRequirementsIgnoringCase() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        Job titleMatch = service.createJob(
                "organiser-1",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));

        Job departmentMatch = service.createJob(
                "organiser-1",
                "Teaching Assistant",
                "Artificial Intelligence",
                "Support AI labs.",
                "Python\nNumPy",
                8,
                LocalDate.now().plusDays(5));

        Job requirementMatch = service.createJob(
                "organiser-1",
                "Algorithms TA",
                "Computer Science",
                "Support algorithms labs.",
                "Problem Solving\nProof Writing",
                8,
                LocalDate.now().plusDays(6));

        assertEquals(List.of(titleMatch.getId()),
                service.searchAvailableJobs("engineering").stream().map(Job::getId).toList());
        assertEquals(List.of(departmentMatch.getId()),
                service.searchAvailableJobs("artificial").stream().map(Job::getId).toList());
        assertEquals(List.of(requirementMatch.getId()),
                service.searchAvailableJobs("proof").stream().map(Job::getId).toList());
    }

    @Test
    void searchAvailableJobsReturnsAllAvailableJobsForEmptyKeyword() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        service.createJob(
                "organiser-1",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));

        service.createJob(
                "organiser-1",
                "Database TA",
                "Computer Science",
                "Support database labs.",
                "SQL\nER Design",
                8,
                LocalDate.now().plusDays(3));

        assertEquals(2, service.searchAvailableJobs("").size());
        assertEquals(2, service.searchAvailableJobs("   ").size());
        assertEquals(2, service.searchAvailableJobs(null).size());
    }

    @Test
    void searchAvailableJobsStillExcludesClosedAndExpiredJobs() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        Job openMatch = service.createJob(
                "organiser-1",
                "Networking TA",
                "Computer Science",
                "Support networking labs.",
                "TCP/IP\nWireshark",
                8,
                LocalDate.now().plusDays(7));

        Job closedMatch = Job.create("organiser-1");
        closedMatch.setTitle("Closed Networking Role");
        closedMatch.setDepartment("Computer Science");
        closedMatch.setDescription("Closed role.");
        closedMatch.setRequirements(List.of("Networking"));
        closedMatch.setHoursPerWeek(8);
        closedMatch.setDeadline(LocalDate.now().plusDays(5));
        closedMatch.setStatus("CLOSED");
        service.saveJob(closedMatch);

        Job expiredMatch = Job.create("organiser-1");
        expiredMatch.setTitle("Expired Networking Role");
        expiredMatch.setDepartment("Computer Science");
        expiredMatch.setDescription("Expired role.");
        expiredMatch.setRequirements(List.of("Networking"));
        expiredMatch.setHoursPerWeek(8);
        expiredMatch.setDeadline(LocalDate.now().minusDays(1));
        service.saveJob(expiredMatch);

        List<Job> results = service.searchAvailableJobs("network");

        assertEquals(1, results.size());
        assertEquals(openMatch.getId(), results.get(0).getId());
        assertTrue(results.stream().noneMatch(job -> job.getId().equals(closedMatch.getId())));
        assertTrue(results.stream().noneMatch(job -> job.getId().equals(expiredMatch.getId())));
    }
}
