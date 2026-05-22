package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.ApplicationDao;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.MessageType;
import com.bupt.tarecruit.util.DataValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates applicant application submission, organiser review decisions, and
 * applicant withdrawal workflow.
 *
 * <p>This service enforces the central recruitment rules: applicants must have a
 * complete profile and CV, duplicate active applications are blocked, application
 * limits are checked, job capacity is respected, and accepted withdrawals notify
 * the owning organiser.</p>
 */
public class ApplicationService {
    private static final String APPLICANT_LIMIT_REACHED_MESSAGE =
            "You have reached your application limit. Please contact Admin if you need an adjustment.";
    private static final String JOB_FULL_MESSAGE = "This job is already full.";

    private final ApplicantService applicantService;
    private final JobService jobService;
    private final CvService cvService;
    private final ApplicationDao applicationDao;
    private final MessageService messageService;
    private final RecruitmentPolicyService recruitmentPolicyService;

    public ApplicationService() {
        this.applicantService = new ApplicantService();
        this.jobService = new JobService();
        this.cvService = new CvService(applicantService);
        this.applicationDao = new ApplicationDaoImpl();
        this.messageService = new MessageService();
        this.recruitmentPolicyService = new RecruitmentPolicyService();
    }

    public ApplicationService(final ApplicantService applicantService, final JobService jobService,
            final CvService cvService, final ApplicationDao applicationDao) {
        this(applicantService, jobService, cvService, applicationDao, new MessageService(), new RecruitmentPolicyService());
    }

    public ApplicationService(final ApplicantService applicantService, final JobService jobService,
            final CvService cvService, final ApplicationDao applicationDao, final MessageService messageService) {
        this(applicantService, jobService, cvService, applicationDao, messageService, new RecruitmentPolicyService());
    }

    public ApplicationService(final ApplicantService applicantService, final JobService jobService,
            final CvService cvService, final ApplicationDao applicationDao, final MessageService messageService,
            final RecruitmentPolicyService recruitmentPolicyService) {
        this.applicantService = applicantService;
        this.jobService = jobService;
        this.cvService = cvService;
        this.applicationDao = applicationDao;
        this.messageService = messageService;
        this.recruitmentPolicyService = recruitmentPolicyService;
    }

    /**
     * Creates a pending application for an applicant and job after all workflow
     * preconditions have passed.
     *
     * @param applicantUserId authenticated applicant user ID
     * @param jobId target job ID
     * @return the persisted pending application
     * @throws IllegalArgumentException when profile, CV, duplicate, limit, status,
     *         deadline, or capacity checks fail
     */
    public Application submitApplication(final String applicantUserId, final String jobId) {
        DataValidator.validateRequired(applicantUserId, "Applicant user ID");
        DataValidator.validateRequired(jobId, "Job ID");

        if (!applicantService.hasCompleteProfile(applicantUserId)) {
            throw new IllegalArgumentException("Please complete your profile before applying.");
        }
        if (!cvService.hasUploadedCv(applicantUserId)) {
            throw new IllegalArgumentException("Please upload your CV before applying.");
        }
        if (hasExistingApplication(applicantUserId, jobId)) {
            throw new IllegalArgumentException("You have already applied for this job.");
        }
        if (recruitmentPolicyService.hasReachedApplicationLimit(applicantUserId)) {
            throw new IllegalArgumentException(APPLICANT_LIMIT_REACHED_MESSAGE);
        }

        Job job = jobService.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("The selected job does not exist."));
        validateJobOpenForApplications(job);
        validateJobHasAvailableSlots(job.getId());

        Application application = Application.create(applicantUserId, jobId);
        applicationDao.save(application);
        return application;
    }

    public List<Application> getApplicationsByApplicant(final String applicantUserId) {
        DataValidator.validateRequired(applicantUserId, "Applicant user ID");
        return applicationDao.findByApplicantId(applicantUserId);
    }

    public Optional<Application> findByApplicantAndJob(final String applicantUserId, final String jobId) {
        DataValidator.validateRequired(applicantUserId, "Applicant user ID");
        DataValidator.validateRequired(jobId, "Job ID");
        return applicationDao.findByApplicantId(applicantUserId).stream()
                .filter(application -> application.getJobId().equals(jobId))
                .filter(application -> blocksNewApplication(application.getStatus()))
                .sorted(Comparator.comparing(
                        Application::getAppliedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .findFirst();
    }

    public Optional<Application> getApplicationDetails(final String applicationId) {
        DataValidator.validateRequired(applicationId, "Application ID");
        return applicationDao.findById(applicationId);
    }

    public Application getApplicationDetailsForOrganiser(final String organiserUserId, final String applicationId) {
        DataValidator.validateRequired(organiserUserId, "Organiser user ID");
        DataValidator.validateRequired(applicationId, "Application ID");
        Application application = getApplicationDetails(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        jobService.getOwnedJobForOrganiser(organiserUserId, application.getJobId());
        return application;
    }

    public Application openApplicationForOrganiser(final String organiserUserId, final String applicationId) {
        Application application = getApplicationDetailsForOrganiser(organiserUserId, applicationId);
        if (application.getStatus() == ApplicationStatus.PENDING && application.getReviewedAt() == null) {
            application.setStatus(ApplicationStatus.REVIEWING);
            application.setReviewedAt(Instant.now());
            applicationDao.save(application);
        }
        return application;
    }

    public List<Application> getApplicationsByJob(final String jobId) {
        DataValidator.validateRequired(jobId, "Job ID");
        return applicationDao.findByJobId(jobId);
    }

    public List<Application> getApplicationsForOrganiserJob(final String organiserUserId, final String jobId) {
        DataValidator.validateRequired(organiserUserId, "Organiser user ID");
        DataValidator.validateRequired(jobId, "Job ID");
        jobService.getOwnedJobForOrganiser(organiserUserId, jobId);
        return applicationDao.findByJobId(jobId).stream()
                .sorted(Comparator.comparing(
                        Application::getAppliedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    /**
     * Allows the owning organiser to make a final accept or reject decision.
     *
     * <p>When an accept decision fills the job quota, remaining pending or
     * reviewing applications for that job are rejected automatically.</p>
     *
     * @param organiserUserId authenticated organiser user ID
     * @param applicationId target application ID
     * @param status final decision, limited to {@code ACCEPTED} or {@code REJECTED}
     * @return the updated application
     */
    public Application updateStatusForOrganiser(final String organiserUserId, final String applicationId,
            final ApplicationStatus status) {
        DataValidator.validateRequired(organiserUserId, "Organiser user ID");
        DataValidator.validateRequired(applicationId, "Application ID");
        if (status == null) {
            throw new IllegalArgumentException("Application status is required.");
        }
        if (status != ApplicationStatus.ACCEPTED && status != ApplicationStatus.REJECTED) {
            throw new IllegalArgumentException("Organisers can only set applications to ACCEPTED or REJECTED.");
        }

        Application application = getApplicationDetailsForOrganiser(organiserUserId, applicationId);
        if (isFinalStatus(application.getStatus())) {
            throw new IllegalArgumentException("A final decision has already been made for this application.");
        }
        if (status == ApplicationStatus.ACCEPTED) {
            validateJobHasAvailableSlots(application.getJobId());
        }
        application.setStatus(status);
        if (application.getReviewedAt() == null) {
            application.setReviewedAt(Instant.now());
        }
        applicationDao.save(application);
        if (status == ApplicationStatus.ACCEPTED) {
            rejectRemainingActiveApplicationsIfJobFilled(application);
        }
        return application;
    }

    /**
     * Withdraws an application on behalf of its applicant owner.
     *
     * @param applicantUserId authenticated applicant user ID
     * @param applicationId target application ID
     * @return the withdrawn application
     */
    public Application withdrawApplicationByApplicant(final String applicantUserId, final String applicationId) {
        DataValidator.validateRequired(applicantUserId, "Applicant user ID");
        DataValidator.validateRequired(applicationId, "Application ID");

        Application application = getApplicationDetails(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        if (!applicantUserId.equals(application.getApplicantUserId())) {
            throw new IllegalArgumentException("Application not found.");
        }
        if (!canApplicantWithdraw(application.getStatus())) {
            throw new IllegalArgumentException("This application can no longer be withdrawn.");
        }

        ApplicationStatus previousStatus = application.getStatus();
        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationDao.save(application);
        if (previousStatus == ApplicationStatus.ACCEPTED) {
            sendAcceptedWithdrawalMessage(application);
        }
        return application;
    }

    private boolean hasExistingApplication(final String applicantUserId, final String jobId) {
        return applicationDao.findByApplicantId(applicantUserId).stream()
                .filter(application -> application.getJobId().equals(jobId))
                .anyMatch(application -> blocksNewApplication(application.getStatus()));
    }

    private void validateJobOpenForApplications(final Job job) {
        if (!"OPEN".equals(job.getStatus())) {
            throw new IllegalArgumentException("This job is not open for applications.");
        }
        if (job.getDeadline() == null || job.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("This job is no longer accepting applications.");
        }
    }

    private void validateJobHasAvailableSlots(final String jobId) {
        if (recruitmentPolicyService.isJobFull(jobId)) {
            throw new IllegalArgumentException(JOB_FULL_MESSAGE);
        }
    }

    private void rejectRemainingActiveApplicationsIfJobFilled(final Application acceptedApplication) {
        if (!recruitmentPolicyService.isJobFull(acceptedApplication.getJobId())) {
            return;
        }
        Instant rejectionTime = Instant.now();
        applicationDao.findByJobId(acceptedApplication.getJobId()).stream()
                .filter(application -> !acceptedApplication.getId().equals(application.getId()))
                .filter(application -> application.getStatus() == ApplicationStatus.PENDING
                        || application.getStatus() == ApplicationStatus.REVIEWING)
                .forEach(application -> {
                    application.setStatus(ApplicationStatus.REJECTED);
                    if (application.getReviewedAt() == null) {
                        application.setReviewedAt(rejectionTime);
                    }
                    applicationDao.save(application);
                });
    }

    private boolean isFinalStatus(final ApplicationStatus status) {
        return status == ApplicationStatus.ACCEPTED
                || status == ApplicationStatus.REJECTED
                || status == ApplicationStatus.WITHDRAWN
                || status == ApplicationStatus.CANCELLED;
    }

    private boolean blocksNewApplication(final ApplicationStatus status) {
        return status != ApplicationStatus.WITHDRAWN;
    }

    private boolean canApplicantWithdraw(final ApplicationStatus status) {
        return status == ApplicationStatus.PENDING
                || status == ApplicationStatus.REVIEWING
                || status == ApplicationStatus.ACCEPTED;
    }

    private void sendAcceptedWithdrawalMessage(final Application application) {
        Job job = jobService.findById(application.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("The selected job does not exist."));
        messageService.sendMessage(
                application.getApplicantUserId(),
                job.getOrganiserUserId(),
                "Application Withdrawn",
                "An accepted application was withdrawn for " + job.getTitle() + ".",
                MessageType.APPLICATION_WITHDRAWN,
                application.getId(),
                job.getId());
    }
}
