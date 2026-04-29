package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.ApplicantLimitPolicyDao;
import com.bupt.tarecruit.dao.ApplicationDao;
import com.bupt.tarecruit.dao.JobDao;
import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.ApplicantLimitPolicy;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.DataValidator;

public class RecruitmentPolicyService {
    private final ApplicationDao applicationDao;
    private final JobDao jobDao;
    private final ApplicantLimitPolicyDao applicantLimitPolicyDao;
    private final SettingsService settingsService;

    public RecruitmentPolicyService() {
        this(new ApplicationDaoImpl(), new JobDaoImpl(), new ApplicantLimitPolicyDaoImpl(), new SettingsService());
    }

    public RecruitmentPolicyService(final ApplicationDao applicationDao,
            final JobDao jobDao,
            final ApplicantLimitPolicyDao applicantLimitPolicyDao,
            final SettingsService settingsService) {
        this.applicationDao = applicationDao;
        this.jobDao = jobDao;
        this.applicantLimitPolicyDao = applicantLimitPolicyDao;
        this.settingsService = settingsService;
    }

    public int resolveApplicantApplicationLimit(final String applicantUserId) {
        DataValidator.validateRequired(applicantUserId, "Applicant user ID");
        return applicantLimitPolicyDao.findByUserId(applicantUserId)
                .map(ApplicantLimitPolicy::getApplicationLimitOverride)
                .filter(limit -> limit != null)
                .orElseGet(settingsService::getDefaultApplicantApplicationLimit);
    }

    public int countActiveApplications(final String applicantUserId) {
        DataValidator.validateRequired(applicantUserId, "Applicant user ID");
        return (int) applicationDao.findByApplicantId(applicantUserId).stream()
                .filter(application -> countsAgainstApplicantLimit(application.getStatus()))
                .count();
    }

    public boolean hasReachedApplicationLimit(final String applicantUserId) {
        return countActiveApplications(applicantUserId) >= resolveApplicantApplicationLimit(applicantUserId);
    }

    public int countAcceptedApplications(final String jobId) {
        DataValidator.validateRequired(jobId, "Job ID");
        return (int) applicationDao.findByJobId(jobId).stream()
                .filter(application -> application.getStatus() == ApplicationStatus.ACCEPTED)
                .count();
    }

    public boolean isJobFull(final String jobId) {
        return countAcceptedApplications(jobId) >= getJob(jobId).getAssistantQuota();
    }

    public int remainingAssistantSlots(final String jobId) {
        Job job = getJob(jobId);
        return Math.max(0, job.getAssistantQuota() - countAcceptedApplications(jobId));
    }

    private boolean countsAgainstApplicantLimit(final ApplicationStatus status) {
        return status == ApplicationStatus.PENDING
                || status == ApplicationStatus.REVIEWING
                || status == ApplicationStatus.ACCEPTED;
    }

    private Job getJob(final String jobId) {
        DataValidator.validateRequired(jobId, "Job ID");
        return jobDao.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("The selected job does not exist."));
    }
}
