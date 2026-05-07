package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.JobRecommendationView;
import com.bupt.tarecruit.model.SkillMatchView;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JobRecommendationService {
    public static final int DEFAULT_LIMIT = 3;

    private final ApplicantService applicantService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final RecruitmentPolicyService recruitmentPolicyService;
    private final SkillMatchService skillMatchService;

    public JobRecommendationService() {
        this(
                new ApplicantService(),
                new JobService(),
                new ApplicationService(),
                new RecruitmentPolicyService(),
                new SkillMatchService());
    }

    public JobRecommendationService(final ApplicantService applicantService,
            final JobService jobService,
            final ApplicationService applicationService,
            final RecruitmentPolicyService recruitmentPolicyService,
            final SkillMatchService skillMatchService) {
        this.applicantService = applicantService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.recruitmentPolicyService = recruitmentPolicyService;
        this.skillMatchService = skillMatchService;
    }

    public List<JobRecommendationView> getRecommendedJobsForApplicant(final String applicantUserId, final int limit) {
        Applicant applicant = applicantService.findByUserId(applicantUserId).orElse(null);
        if (applicant == null || limit <= 0) {
            return List.of();
        }

        Set<String> appliedJobIds = applicationService.getApplicationsByApplicant(applicantUserId).stream()
                .map(application -> application.getJobId())
                .collect(Collectors.toSet());

        return jobService.getAvailableJobs().stream()
                .filter(job -> !appliedJobIds.contains(job.getId()))
                .filter(job -> !recruitmentPolicyService.isJobFull(job.getId()))
                .map(job -> toRecommendation(job, applicant))
                .sorted(Comparator.comparingInt(JobRecommendationView::getMatchScore).reversed()
                        .thenComparing(view -> view.getJob().getDeadline())
                        .thenComparing(view -> view.getJob().getTitle(), String.CASE_INSENSITIVE_ORDER))
                .limit(limit)
                .toList();
    }

    private JobRecommendationView toRecommendation(final Job job, final Applicant applicant) {
        SkillMatchView skillMatch = skillMatchService.calculateMatch(applicant, job);
        JobRecommendationView recommendation = new JobRecommendationView();
        recommendation.setJob(job);
        recommendation.setSkillMatch(skillMatch);
        return recommendation;
    }
}
