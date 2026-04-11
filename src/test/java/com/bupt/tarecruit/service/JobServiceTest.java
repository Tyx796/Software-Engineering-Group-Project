package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.Message;
import com.bupt.tarecruit.model.MessageType;
import java.io.ByteArrayInputStream;
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

        Job cancelledMatch = Job.create("organiser-1");
        cancelledMatch.setTitle("Cancelled Networking Role");
        cancelledMatch.setDepartment("Computer Science");
        cancelledMatch.setDescription("Cancelled role.");
        cancelledMatch.setRequirements(List.of("Networking"));
        cancelledMatch.setHoursPerWeek(8);
        cancelledMatch.setDeadline(LocalDate.now().plusDays(5));
        cancelledMatch.setStatus(JobService.STATUS_CANCELLED);
        service.saveJob(cancelledMatch);

        List<Job> results = service.searchAvailableJobs("network");

        assertEquals(1, results.size());
        assertEquals(openMatch.getId(), results.get(0).getId());
        assertTrue(results.stream().noneMatch(job -> job.getId().equals(closedMatch.getId())));
        assertTrue(results.stream().noneMatch(job -> job.getId().equals(expiredMatch.getId())));
        assertTrue(results.stream().noneMatch(job -> job.getId().equals(cancelledMatch.getId())));
    }

    @Test
    void organiserCanUpdateOwnedJob() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        Job job = service.createJob(
                "organiser-1",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));

        Job updated = service.updateJobForOrganiser(
                "organiser-1",
                job.getId(),
                "Advanced Software Testing TA",
                "Computer Science",
                "Support testing labs and revision sessions.",
                "JUnit\nMocking\nFeedback",
                10,
                LocalDate.now().plusDays(10));

        assertEquals("Advanced Software Testing TA", updated.getTitle());
        assertEquals(10, updated.getHoursPerWeek());
        assertEquals(3, updated.getRequirements().size());
        assertEquals("Advanced Software Testing TA", service.findById(job.getId()).orElseThrow().getTitle());
    }

    @Test
    void organiserCannotUpdateUnownedJob() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        Job job = service.createJob(
                "organiser-2",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateJobForOrganiser(
                        "organiser-1",
                        job.getId(),
                        "Edited Title",
                        "Computer Science",
                        "Updated description",
                        "JUnit",
                        8,
                        LocalDate.now().plusDays(8)));
        assertEquals("You are not allowed to access this job.", exception.getMessage());
    }

    @Test
    void cancelledJobCannotBeEdited() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        Job job = service.createJob(
                "organiser-1",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));
        job.setStatus(JobService.STATUS_CANCELLED);
        service.saveJob(job);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateJobForOrganiser(
                        "organiser-1",
                        job.getId(),
                        "Edited Title",
                        "Computer Science",
                        "Updated description",
                        "JUnit",
                        8,
                        LocalDate.now().plusDays(8)));
        assertEquals("Cancelled jobs cannot be edited.", exception.getMessage());
    }

    @Test
    void organiserCanCancelOwnedJob() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        Job job = service.createJob(
                "organiser-1",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));

        Job cancelled = service.cancelJobForOrganiser("organiser-1", job.getId());

        assertEquals(JobService.STATUS_CANCELLED, cancelled.getStatus());
        assertEquals(JobService.STATUS_CANCELLED, service.findById(job.getId()).orElseThrow().getStatus());
        assertTrue(service.getAvailableJobs().stream().noneMatch(current -> current.getId().equals(job.getId())));
    }

    @Test
    void organiserCannotCancelUnownedOrAlreadyCancelledJob() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);

        Job job = service.createJob(
                "organiser-2",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));

        IllegalArgumentException unownedException = assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelJobForOrganiser("organiser-1", job.getId()));
        assertEquals("You are not allowed to access this job.", unownedException.getMessage());

        service.cancelJobForOrganiser("organiser-2", job.getId());

        IllegalArgumentException cancelledException = assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelJobForOrganiser("organiser-2", job.getId()));
        assertEquals("This job has already been cancelled.", cancelledException.getMessage());
    }

    @Test
    void cancellingJobCancelsApplicationsAndNotifiesApplicantsOncePerApplicant() throws Exception {
        TestContext context = createContext();
        Job job = context.jobService.createJob(
                "organiser-1",
                "Software Engineering TA",
                "Computer Science",
                "Support software engineering labs.",
                "Java\nGit",
                8,
                LocalDate.now().plusDays(7));

        createCompleteProfile(context.applicantService, "user-1");
        createCompleteProfile(context.applicantService, "user-2");
        saveCv(context, "user-1", "cv.pdf");
        saveCv(context, "user-2", "cv.pdf");

        Application withdrawnFirst = context.applicationService.submitApplication("user-1", job.getId());
        context.applicationService.withdrawApplicationByApplicant("user-1", withdrawnFirst.getId());
        Application activeSecond = context.applicationService.submitApplication("user-1", job.getId());
        Application otherApplicant = context.applicationService.submitApplication("user-2", job.getId());

        context.jobService.cancelJobForOrganiser("organiser-1", job.getId());

        List<Application> applications = context.applicationService.getApplicationsByJob(job.getId());
        assertEquals(3, applications.size());
        assertTrue(applications.stream().allMatch(application -> application.getStatus() == ApplicationStatus.CANCELLED));

        List<Message> userOneMessages = context.messageService.getMessagesForRecipient("user-1");
        List<Message> userTwoMessages = context.messageService.getMessagesForRecipient("user-2");
        assertEquals(1, userOneMessages.size());
        assertEquals(1, userTwoMessages.size());
        assertEquals(MessageType.JOB_CANCELLED, userOneMessages.get(0).getType());
        assertEquals("organiser-1", userOneMessages.get(0).getSenderUserId());
        assertEquals(job.getId(), userOneMessages.get(0).getRelatedJobId());
        assertTrue(userOneMessages.get(0).getContent().contains(job.getTitle()));
        assertEquals(activeSecond.getApplicantUserId(), userOneMessages.get(0).getRecipientUserId());
        assertEquals(otherApplicant.getApplicantUserId(), userTwoMessages.get(0).getRecipientUserId());
    }

    private TestContext createContext() throws Exception {
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path messagesFile = Files.createTempFile("messages", ".json");

        ApplicantService applicantService = new ApplicantService(applicantsFile);
        MessageService messageService = new MessageService(messagesFile);
        JobService jobService = new JobService(
                new JobDaoImpl(jobsFile),
                new ApplicationDaoImpl(applicationsFile),
                messageService);
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile));
        ApplicationService applicationService = new ApplicationService(
                applicantService,
                jobService,
                cvService,
                new ApplicationDaoImpl(applicationsFile),
                messageService);

        return new TestContext(applicantService, jobService, cvService, applicationService, messageService);
    }

    private void createCompleteProfile(final ApplicantService applicantService, final String userId) {
        applicantService.createProfile(
                userId,
                "Alice Chen",
                "+86 13800000000",
                "20260001",
                "Computer Science",
                "Ready to support labs.");
    }

    private void saveCv(final TestContext context, final String userId, final String fileName) {
        context.cvService.uploadCV(userId, fileName, new ByteArrayInputStream("cv-data".getBytes()));
    }

    private static final class TestContext {
        private final ApplicantService applicantService;
        private final JobService jobService;
        private final CvService cvService;
        private final ApplicationService applicationService;
        private final MessageService messageService;

        private TestContext(
                final ApplicantService applicantService,
                final JobService jobService,
                final CvService cvService,
                final ApplicationService applicationService,
                final MessageService messageService) {
            this.applicantService = applicantService;
            this.jobService = jobService;
            this.cvService = cvService;
            this.applicationService = applicationService;
            this.messageService = messageService;
        }
    }
}
