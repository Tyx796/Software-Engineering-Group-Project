package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.ApplicationDao;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.DataValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ApplicationService {
    private final ApplicantService applicantService;
    private final JobService jobService;
    private final CvService cvService;
    private final ApplicationDao applicationDao;

    public ApplicationService() {
        this.applicantService = new ApplicantService();
        this.jobService = new JobService();
        this.cvService = new CvService(applicantService);
        this.applicationDao = new ApplicationDaoImpl();
    }

    public ApplicationService(final ApplicantService applicantService, final JobService jobService,
                              final CvService cvService, final ApplicationDao applicationDao) {
        this.applicantService = applicantService;
        this.jobService = jobService;
        this.cvService = cvService;
        this.applicationDao = applicationDao;
    }

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

        Job job = jobService.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("The selected job does not exist."));
        validateJobOpenForApplications(job);

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

    public Application updateStatusForOrganiser(final String organiserUserId, final String applicationId,
                                                final ApplicationStatus status) {
        DataValidator.validateRequired(organiserUserId, "Organiser user ID");
        DataValidator.validateRequired(applicationId, "Application ID");
        if (status == null) {
            throw new IllegalArgumentException("Application status is required.");
        }

        Application application = getApplicationDetailsForOrganiser(organiserUserId, applicationId);
        application.setStatus(status);
        if (application.getReviewedAt() == null) {
            application.setReviewedAt(Instant.now());
        }
        applicationDao.save(application);
        return application;
    }

    private boolean hasExistingApplication(final String applicantUserId, final String jobId) {
        return findByApplicantAndJob(applicantUserId, jobId).isPresent();
    }

    private void validateJobOpenForApplications(final Job job) {
        if (!"OPEN".equals(job.getStatus())) {
            throw new IllegalArgumentException("This job is not open for applications.");
        }
        if (job.getDeadline() == null || job.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("This job is no longer accepting applications.");
        }
    }
}
