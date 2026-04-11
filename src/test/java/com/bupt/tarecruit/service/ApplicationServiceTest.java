package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.ApplicantDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.model.Message;
import com.bupt.tarecruit.model.MessageType;
import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApplicationServiceTest {
    @Test
    void submitApplicationCreatesPendingRecordWithTimestamp() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", job.getId());

        assertEquals("user-1", application.getApplicantUserId());
        assertEquals(job.getId(), application.getJobId());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertNotNull(application.getAppliedAt());
        assertTrue(context.applicationService.getApplicationDetails(application.getId()).isPresent());
    }

    @Test
    void duplicateApplicationIsRejected() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        context.applicationService.submitApplication("user-1", job.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.submitApplication("user-1", job.getId()));
        assertEquals("You have already applied for this job.", exception.getMessage());
    }

    @Test
    void withdrawnApplicationAllowsApplicantToApplyAgain() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application first = context.applicationService.submitApplication("user-1", job.getId());
        first.setStatus(ApplicationStatus.WITHDRAWN);
        new ApplicationDaoImpl(context.applicationsFile).save(first);

        Application reapplied = context.applicationService.submitApplication("user-1", job.getId());

        assertEquals(ApplicationStatus.PENDING, reapplied.getStatus());
        assertTrue(context.applicationService.getApplicationsByApplicant("user-1").stream()
                .anyMatch(application -> application.getId().equals(first.getId())
                        && application.getStatus() == ApplicationStatus.WITHDRAWN));
    }

    @Test
    void missingProfileIsRejected() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.submitApplication("user-1", job.getId()));
        assertEquals("Please complete your profile before applying.", exception.getMessage());
    }

    @Test
    void incompleteProfileIsRejected() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");

        Applicant incompleteProfile = new Applicant();
        incompleteProfile.setUserId("user-1");
        incompleteProfile.setFullName("Alice");
        incompleteProfile.setProgramme("Computer Science");
        new ApplicantDaoImpl(context.applicantsFile).save(incompleteProfile);
        saveCv(context, "user-1", "cv.pdf");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.submitApplication("user-1", job.getId()));
        assertEquals("Please complete your profile before applying.", exception.getMessage());
    }

    @Test
    void missingCvIsRejected() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.submitApplication("user-1", job.getId()));
        assertEquals("Please upload your CV before applying.", exception.getMessage());
    }

    @Test
    void missingJobIsRejected() throws Exception {
        TestContext context = createContext();
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.submitApplication("user-1", "missing-job"));
        assertEquals("The selected job does not exist.", exception.getMessage());
    }

    @Test
    void closedOrExpiredJobIsRejected() throws Exception {
        TestContext context = createContext();
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Job closedJob = Job.create("organiser-1");
        closedJob.setTitle("Closed Job");
        closedJob.setDepartment("CS");
        closedJob.setDescription("Closed");
        closedJob.setRequirements(List.of("Java"));
        closedJob.setHoursPerWeek(8);
        closedJob.setDeadline(LocalDate.now().plusDays(3));
        closedJob.setStatus("CLOSED");
        context.jobService.saveJob(closedJob);

        IllegalArgumentException closedException = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.submitApplication("user-1", closedJob.getId()));
        assertEquals("This job is not open for applications.", closedException.getMessage());

        Job expiredJob = Job.create("organiser-1");
        expiredJob.setTitle("Expired Job");
        expiredJob.setDepartment("CS");
        expiredJob.setDescription("Expired");
        expiredJob.setRequirements(List.of("Java"));
        expiredJob.setHoursPerWeek(8);
        expiredJob.setDeadline(LocalDate.now().minusDays(1));
        context.jobService.saveJob(expiredJob);

        IllegalArgumentException expiredException = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.submitApplication("user-1", expiredJob.getId()));
        assertEquals("This job is no longer accepting applications.", expiredException.getMessage());
    }

    @Test
    void getApplicationsByApplicantAndJobReturnsMatchingRecords() throws Exception {
        TestContext context = createContext();
        Job jobOne = createOpenJob(context.jobService, "organiser-1");
        Job jobTwo = createOpenJob(context.jobService, "organiser-2");
        createCompleteProfile(context.applicantService, "user-1");
        createCompleteProfile(context.applicantService, "user-2");
        saveCv(context, "user-1", "cv.pdf");
        saveCv(context, "user-2", "cv.pdf");

        Application applicationOne = context.applicationService.submitApplication("user-1", jobOne.getId());
        Application applicationTwo = context.applicationService.submitApplication("user-1", jobTwo.getId());
        context.applicationService.submitApplication("user-2", jobTwo.getId());

        assertEquals(2, context.applicationService.getApplicationsByApplicant("user-1").size());
        assertEquals(2, context.applicationService.getApplicationsByJob(jobTwo.getId()).size());
        assertEquals(applicationOne.getId(),
                context.applicationService.getApplicationDetails(applicationOne.getId()).orElseThrow().getId());
        assertTrue(context.applicationService.getApplicationsByApplicant("user-1").stream()
                .anyMatch(application -> application.getId().equals(applicationTwo.getId())));
    }

    @Test
    void organiserCanViewApplicationsForOwnedJob() throws Exception {
        TestContext context = createContext();
        Job ownedJob = createOpenJob(context.jobService, "organiser-1");
        Job otherJob = createOpenJob(context.jobService, "organiser-2");
        createCompleteProfile(context.applicantService, "user-1");
        createCompleteProfile(context.applicantService, "user-2");
        saveCv(context, "user-1", "cv.pdf");
        saveCv(context, "user-2", "cv.pdf");

        Application first = context.applicationService.submitApplication("user-1", ownedJob.getId());
        Application second = context.applicationService.submitApplication("user-2", ownedJob.getId());
        context.applicationService.submitApplication("user-2", otherJob.getId());

        List<Application> applications = context.applicationService
                .getApplicationsForOrganiserJob("organiser-1", ownedJob.getId());

        assertEquals(2, applications.size());
        assertEquals(second.getId(), applications.get(0).getId());
        assertTrue(applications.stream().anyMatch(application -> application.getId().equals(first.getId())));
        assertTrue(applications.stream().anyMatch(application -> application.getId().equals(second.getId())));
    }

    @Test
    void organiserCannotViewApplicationsForUnownedJob() throws Exception {
        TestContext context = createContext();
        Job ownedByAnotherOrganiser = createOpenJob(context.jobService, "organiser-2");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.getApplicationsForOrganiserJob("organiser-1", ownedByAnotherOrganiser.getId()));
        assertEquals("You are not allowed to access this job.", exception.getMessage());
    }

    @Test
    void organiserCanViewOwnedApplicationDetails() throws Exception {
        TestContext context = createContext();
        Job ownedJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", ownedJob.getId());

        Application resolved = context.applicationService
                .getApplicationDetailsForOrganiser("organiser-1", application.getId());

        assertEquals(application.getId(), resolved.getId());
        assertEquals("user-1", resolved.getApplicantUserId());
    }

    @Test
    void organiserCannotViewUnownedApplicationDetails() throws Exception {
        TestContext context = createContext();
        Job ownedByAnotherOrganiser = createOpenJob(context.jobService, "organiser-2");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", ownedByAnotherOrganiser.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.getApplicationDetailsForOrganiser("organiser-1", application.getId()));
        assertEquals("You are not allowed to access this job.", exception.getMessage());
    }

    @Test
    void openingPendingApplicationMarksItAsReviewingOnce() throws Exception {
        TestContext context = createContext();
        Job ownedJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", ownedJob.getId());

        Application reviewed = context.applicationService.openApplicationForOrganiser("organiser-1", application.getId());

        assertEquals(ApplicationStatus.REVIEWING, reviewed.getStatus());
        assertNotNull(reviewed.getReviewedAt());

        Application openedAgain = context.applicationService.openApplicationForOrganiser("organiser-1", application.getId());
        assertEquals(ApplicationStatus.REVIEWING, openedAgain.getStatus());
        assertEquals(reviewed.getReviewedAt(), openedAgain.getReviewedAt());
    }

    @Test
    void organiserCanUpdateApplicationStatus() throws Exception {
        TestContext context = createContext();
        Job ownedJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", ownedJob.getId());

        Application accepted = context.applicationService
                .updateStatusForOrganiser("organiser-1", application.getId(), ApplicationStatus.ACCEPTED);

        assertEquals(ApplicationStatus.ACCEPTED, accepted.getStatus());
        assertNotNull(accepted.getReviewedAt());
        assertEquals(ApplicationStatus.ACCEPTED,
                context.applicationService.getApplicationDetails(application.getId()).orElseThrow().getStatus());
    }

    @Test
    void organiserCannotManuallySetPendingOrReviewing() throws Exception {
        TestContext context = createContext();
        Job ownedJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", ownedJob.getId());
        context.applicationService.openApplicationForOrganiser("organiser-1", application.getId());

        IllegalArgumentException pendingException = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.updateStatusForOrganiser(
                        "organiser-1",
                        application.getId(),
                        ApplicationStatus.PENDING));
        IllegalArgumentException reviewingException = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.updateStatusForOrganiser(
                        "organiser-1",
                        application.getId(),
                        ApplicationStatus.REVIEWING));

        assertEquals("Organisers can only set applications to ACCEPTED or REJECTED.", pendingException.getMessage());
        assertEquals("Organisers can only set applications to ACCEPTED or REJECTED.", reviewingException.getMessage());
    }

    @Test
    void finalDecisionCannotBeChangedAgain() throws Exception {
        TestContext context = createContext();
        Job ownedJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", ownedJob.getId());

        context.applicationService.updateStatusForOrganiser("organiser-1", application.getId(), ApplicationStatus.ACCEPTED);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.updateStatusForOrganiser(
                        "organiser-1",
                        application.getId(),
                        ApplicationStatus.REJECTED));
        assertEquals("A final decision has already been made for this application.", exception.getMessage());
    }

    @Test
    void withdrawnAndCancelledApplicationsAreAlsoFinalStates() throws Exception {
        TestContext context = createContext();
        Job ownedJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application withdrawn = context.applicationService.submitApplication("user-1", ownedJob.getId());
        withdrawn.setStatus(ApplicationStatus.WITHDRAWN);
        new ApplicationDaoImpl(context.applicationsFile).save(withdrawn);

        IllegalArgumentException withdrawnException = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.updateStatusForOrganiser(
                        "organiser-1",
                        withdrawn.getId(),
                        ApplicationStatus.ACCEPTED));
        assertEquals("A final decision has already been made for this application.", withdrawnException.getMessage());

        Application cancelled = context.applicationService.submitApplication("user-1", ownedJob.getId());
        cancelled.setStatus(ApplicationStatus.CANCELLED);
        new ApplicationDaoImpl(context.applicationsFile).save(cancelled);

        IllegalArgumentException cancelledException = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.updateStatusForOrganiser(
                        "organiser-1",
                        cancelled.getId(),
                        ApplicationStatus.REJECTED));
        assertEquals("A final decision has already been made for this application.", cancelledException.getMessage());
    }

    @Test
    void organiserCannotUpdateUnownedApplicationStatus() throws Exception {
        TestContext context = createContext();
        Job ownedByAnotherOrganiser = createOpenJob(context.jobService, "organiser-2");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", ownedByAnotherOrganiser.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.updateStatusForOrganiser(
                        "organiser-1",
                        application.getId(),
                        ApplicationStatus.REJECTED));
        assertEquals("You are not allowed to access this job.", exception.getMessage());
    }

    @Test
    void applicantCanWithdrawPendingReviewingAndAcceptedApplications() throws Exception {
        TestContext context = createContext();
        Job pendingJob = createOpenJob(context.jobService, "organiser-1");
        Job reviewingJob = createOpenJob(context.jobService, "organiser-1");
        Job acceptedJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application pending = context.applicationService.submitApplication("user-1", pendingJob.getId());
        Application reviewing = context.applicationService.submitApplication("user-1", reviewingJob.getId());
        context.applicationService.openApplicationForOrganiser("organiser-1", reviewing.getId());
        Application accepted = context.applicationService.submitApplication("user-1", acceptedJob.getId());
        context.applicationService.updateStatusForOrganiser("organiser-1", accepted.getId(), ApplicationStatus.ACCEPTED);

        assertEquals(
                ApplicationStatus.WITHDRAWN,
                context.applicationService.withdrawApplicationByApplicant("user-1", pending.getId()).getStatus());
        assertEquals(
                ApplicationStatus.WITHDRAWN,
                context.applicationService.withdrawApplicationByApplicant("user-1", reviewing.getId()).getStatus());
        assertEquals(
                ApplicationStatus.WITHDRAWN,
                context.applicationService.withdrawApplicationByApplicant("user-1", accepted.getId()).getStatus());
    }

    @Test
    void acceptedWithdrawalSendsNotificationToOrganiser() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", job.getId());
        context.applicationService.updateStatusForOrganiser("organiser-1", application.getId(), ApplicationStatus.ACCEPTED);

        context.applicationService.withdrawApplicationByApplicant("user-1", application.getId());

        List<Message> organiserMessages = context.messageService.getMessagesForRecipient("organiser-1");
        assertEquals(1, organiserMessages.size());
        assertEquals("user-1", organiserMessages.get(0).getSenderUserId());
        assertEquals("Application Withdrawn", organiserMessages.get(0).getSubject());
        assertEquals(MessageType.APPLICATION_WITHDRAWN, organiserMessages.get(0).getType());
        assertEquals(application.getId(), organiserMessages.get(0).getRelatedApplicationId());
        assertEquals(job.getId(), organiserMessages.get(0).getRelatedJobId());
        assertTrue(organiserMessages.get(0).getContent().contains(job.getTitle()));
    }

    @Test
    void pendingAndReviewingWithdrawalsDoNotNotifyOrganiser() throws Exception {
        TestContext context = createContext();
        Job pendingJob = createOpenJob(context.jobService, "organiser-1");
        Job reviewingJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application pending = context.applicationService.submitApplication("user-1", pendingJob.getId());
        Application reviewing = context.applicationService.submitApplication("user-1", reviewingJob.getId());
        context.applicationService.openApplicationForOrganiser("organiser-1", reviewing.getId());

        context.applicationService.withdrawApplicationByApplicant("user-1", pending.getId());
        context.applicationService.withdrawApplicationByApplicant("user-1", reviewing.getId());

        assertTrue(context.messageService.getMessagesForRecipient("organiser-1").isEmpty());
    }

    @Test
    void applicantCannotWithdrawRejectedOrAlreadyFinalApplications() throws Exception {
        TestContext context = createContext();
        Job rejectedJob = createOpenJob(context.jobService, "organiser-1");
        Job withdrawnJob = createOpenJob(context.jobService, "organiser-1");
        Job cancelledJob = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application rejected = context.applicationService.submitApplication("user-1", rejectedJob.getId());
        context.applicationService.updateStatusForOrganiser("organiser-1", rejected.getId(), ApplicationStatus.REJECTED);

        Application withdrawn = context.applicationService.submitApplication("user-1", withdrawnJob.getId());
        withdrawn.setStatus(ApplicationStatus.WITHDRAWN);
        new ApplicationDaoImpl(context.applicationsFile).save(withdrawn);

        Application cancelled = context.applicationService.submitApplication("user-1", cancelledJob.getId());
        cancelled.setStatus(ApplicationStatus.CANCELLED);
        new ApplicationDaoImpl(context.applicationsFile).save(cancelled);

        assertEquals(
                "This application can no longer be withdrawn.",
                assertThrows(
                        IllegalArgumentException.class,
                        () -> context.applicationService.withdrawApplicationByApplicant("user-1", rejected.getId()))
                        .getMessage());
        assertEquals(
                "This application can no longer be withdrawn.",
                assertThrows(
                        IllegalArgumentException.class,
                        () -> context.applicationService.withdrawApplicationByApplicant("user-1", withdrawn.getId()))
                        .getMessage());
        assertEquals(
                "This application can no longer be withdrawn.",
                assertThrows(
                        IllegalArgumentException.class,
                        () -> context.applicationService.withdrawApplicationByApplicant("user-1", cancelled.getId()))
                        .getMessage());
    }

    @Test
    void applicantCannotWithdrawAnotherUsersApplication() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        createCompleteProfile(context.applicantService, "user-2");
        saveCv(context, "user-1", "cv.pdf");
        saveCv(context, "user-2", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", job.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> context.applicationService.withdrawApplicationByApplicant("user-2", application.getId()));
        assertEquals("Application not found.", exception.getMessage());
    }

    @Test
    void withdrawnApplicationIsIgnoredWhenResolvingCurrentApplicationForJob() throws Exception {
        TestContext context = createContext();
        Job job = createOpenJob(context.jobService, "organiser-1");
        createCompleteProfile(context.applicantService, "user-1");
        saveCv(context, "user-1", "cv.pdf");

        Application application = context.applicationService.submitApplication("user-1", job.getId());
        context.applicationService.withdrawApplicationByApplicant("user-1", application.getId());

        assertTrue(context.applicationService.findByApplicantAndJob("user-1", job.getId()).isEmpty());
    }

    private TestContext createContext() throws Exception {
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path jobsFile = Files.createTempFile("jobs", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path applicationsFile = Files.createTempFile("applications", ".json");
        Path messagesFile = Files.createTempFile("messages", ".json");

        ApplicantService applicantService = new ApplicantService(applicantsFile);
        JobService jobService = new JobService(jobsFile);
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile));
        MessageService messageService = new MessageService(messagesFile);
        ApplicationService applicationService = new ApplicationService(
                applicantService,
                jobService,
                cvService,
                new ApplicationDaoImpl(applicationsFile),
                messageService);

        return new TestContext(
                applicantService,
                jobService,
                cvService,
                applicationService,
                messageService,
                applicantsFile,
                cvsFile,
                applicationsFile);
    }

    private Job createOpenJob(final JobService jobService, final String organiserUserId) {
        return jobService.createJob(
                organiserUserId,
                "Software Engineering TA",
                "Computer Science",
                "Support labs and students.",
                "Java\nCommunication",
                8,
                LocalDate.now().plusDays(7));
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
        private final Path applicantsFile;
        private final Path cvsFile;
        private final Path applicationsFile;

        private TestContext(final ApplicantService applicantService, final JobService jobService,
                final CvService cvService, final ApplicationService applicationService, final MessageService messageService,
                final Path applicantsFile, final Path cvsFile, final Path applicationsFile) {
            this.applicantService = applicantService;
            this.jobService = jobService;
            this.cvService = cvService;
            this.applicationService = applicationService;
            this.messageService = messageService;
            this.applicantsFile = applicantsFile;
            this.cvsFile = cvsFile;
            this.applicationsFile = applicationsFile;
        }
    }
}
