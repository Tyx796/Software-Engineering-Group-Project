package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.Message;
import com.bupt.tarecruit.model.MessageType;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class WithdrawalMessageWorkflowTest {
    @Test
    void acceptedWithdrawalNotifiesOrganiserAndMessageCanBeMarkedRead() throws Exception {
        TestContext context = createContext();

        User applicant = context.userService.register("alice", "alice@example.com", "secret1", Role.APPLICANT);
        User organiser = context.userService.register("olivia", "olivia@example.com", "secret1", Role.ORGANISER);

        createCompleteProfile(context.applicantService, applicant.getId());
        saveCv(context, applicant.getId(), "cv.pdf");

        Job job = context.jobService.createJob(
                organiser.getId(),
                "Software Testing TA",
                "Computer Science",
                "Support testing labs.",
                "JUnit\nGit",
                8,
                LocalDate.now().plusDays(7));

        Application application = context.applicationService.submitApplication(applicant.getId(), job.getId());
        context.applicationService.updateStatusForOrganiser(organiser.getId(), application.getId(), ApplicationStatus.ACCEPTED);
        context.applicationService.withdrawApplicationByApplicant(applicant.getId(), application.getId());

        List<Message> organiserMessages = context.messageService.getMessagesForRecipient(organiser.getId());
        assertEquals(1, organiserMessages.size());
        assertEquals(MessageType.APPLICATION_WITHDRAWN, organiserMessages.get(0).getType());
        assertEquals(applicant.getId(), organiserMessages.get(0).getSenderUserId());
        assertEquals(job.getId(), organiserMessages.get(0).getRelatedJobId());

        Message read = context.messageService.markAsRead(organiser.getId(), organiserMessages.get(0).getId());
        assertTrue(read.isRead());
    }

    @Test
    void cancellingJobCancelsApplicationsAndNotifiesApplicants() throws Exception {
        TestContext context = createContext();

        User organiser = context.userService.register("olivia", "olivia@example.com", "secret1", Role.ORGANISER);
        User applicantOne = context.userService.register("alice", "alice@example.com", "secret1", Role.APPLICANT);
        User applicantTwo = context.userService.register("bob", "bob@example.com", "secret1", Role.APPLICANT);

        createCompleteProfile(context.applicantService, applicantOne.getId());
        createCompleteProfile(context.applicantService, applicantTwo.getId());
        saveCv(context, applicantOne.getId(), "cv.pdf");
        saveCv(context, applicantTwo.getId(), "cv.pdf");

        Job job = context.jobService.createJob(
                organiser.getId(),
                "Database TA",
                "Computer Science",
                "Support database labs.",
                "SQL\nDesign",
                8,
                LocalDate.now().plusDays(7));

        Application first = context.applicationService.submitApplication(applicantOne.getId(), job.getId());
        context.applicationService.withdrawApplicationByApplicant(applicantOne.getId(), first.getId());
        Application second = context.applicationService.submitApplication(applicantOne.getId(), job.getId());
        Application third = context.applicationService.submitApplication(applicantTwo.getId(), job.getId());

        context.jobService.cancelJobForOrganiser(organiser.getId(), job.getId());

        assertEquals(JobService.STATUS_CANCELLED, context.jobService.findById(job.getId()).orElseThrow().getStatus());
        assertTrue(context.applicationService.getApplicationsByJob(job.getId()).stream()
                .allMatch(application -> application.getStatus() == ApplicationStatus.CANCELLED));

        List<Message> applicantOneMessages = context.messageService.getMessagesForRecipient(applicantOne.getId());
        List<Message> applicantTwoMessages = context.messageService.getMessagesForRecipient(applicantTwo.getId());
        assertEquals(1, applicantOneMessages.size());
        assertEquals(1, applicantTwoMessages.size());
        assertEquals(MessageType.JOB_CANCELLED, applicantOneMessages.get(0).getType());
        assertEquals(organiser.getId(), applicantOneMessages.get(0).getSenderUserId());
        assertEquals(job.getId(), applicantOneMessages.get(0).getRelatedJobId());
        assertEquals(job.getId(), applicantTwoMessages.get(0).getRelatedJobId());
        assertTrue(List.of(first.getId(), second.getId()).contains(applicantOneMessages.get(0).getRelatedApplicationId()));
        assertEquals(third.getId(), applicantTwoMessages.get(0).getRelatedApplicationId());
    }

    private TestContext createContext() throws Exception {
        Path usersFile = Files.createTempFile("users", ".json");
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path messagesFile = Files.createTempFile("messages", ".json");
        Path cvRootDirectory = Files.createTempDirectory("cv-root");

        UserService userService = new UserService(usersFile);
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        MessageService messageService = new MessageService(messagesFile);
        JobService jobService = new JobService(
                new JobDaoImpl(jobsFile),
                new ApplicationDaoImpl(applicationsFile),
                messageService);
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);
        ApplicationService applicationService = new ApplicationService(
                applicantService,
                jobService,
                cvService,
                new ApplicationDaoImpl(applicationsFile),
                messageService);

        return new TestContext(userService, applicantService, jobService, cvService, applicationService, messageService);
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

    private record TestContext(
            UserService userService,
            ApplicantService applicantService,
            JobService jobService,
            CvService cvService,
            ApplicationService applicationService,
            MessageService messageService) {
    }
}
