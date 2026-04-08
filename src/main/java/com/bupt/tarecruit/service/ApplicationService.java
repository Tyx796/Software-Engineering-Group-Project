package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.ApplicationDao;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.DataValidator;
import java.time.LocalDate;
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
        if (cvService.findByUserId(applicantUserId).isEmpty()) {
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

    public Optional<Application> getApplicationDetails(final String applicationId) {
        DataValidator.validateRequired(applicationId, "Application ID");
        return applicationDao.findById(applicationId);
    }

    public List<Application> getApplicationsByJob(final String jobId) {
        DataValidator.validateRequired(jobId, "Job ID");
        return applicationDao.findByJobId(jobId);
    }

    private boolean hasExistingApplication(final String applicantUserId, final String jobId) {
        return applicationDao.findByApplicantId(applicantUserId).stream()
                .anyMatch(application -> application.getJobId().equals(jobId));
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
