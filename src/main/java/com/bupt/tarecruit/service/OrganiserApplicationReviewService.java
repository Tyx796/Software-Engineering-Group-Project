package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.OrganiserApplicationReviewView;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrganiserApplicationReviewService {
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final ApplicantService applicantService;
    private final SkillMatchService skillMatchService;

    public OrganiserApplicationReviewService() {
        this(new JobService(), new ApplicationService(), new ApplicantService(), new SkillMatchService());
    }

    public OrganiserApplicationReviewService(final JobService jobService,
            final ApplicationService applicationService,
            final ApplicantService applicantService,
            final SkillMatchService skillMatchService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.applicantService = applicantService;
        this.skillMatchService = skillMatchService;
    }

    public List<OrganiserApplicationReviewView> getReviewViews(final String organiserUserId,
            final String jobId,
            final String statusFilter,
            final String keyword,
            final String sortOption) {
        var job = jobService.getOwnedJobForOrganiser(organiserUserId, jobId);
        Map<String, Applicant> applicantsByUserId = applicantService.getAllProfiles().stream()
                .collect(Collectors.toMap(Applicant::getUserId, Function.identity(), (left, right) -> right));

        return applicationService.getApplicationsForOrganiserJob(organiserUserId, jobId).stream()
                .map(application -> buildReviewView(application, applicantsByUserId.get(application.getApplicantUserId()), job))
                .filter(view -> matchesStatus(view, statusFilter))
                .filter(view -> matchesKeyword(view, keyword))
                .sorted(resolveComparator(sortOption))
                .toList();
    }

    private OrganiserApplicationReviewView buildReviewView(final Application application,
            final Applicant applicant,
            final com.bupt.tarecruit.model.Job job) {
        OrganiserApplicationReviewView view = new OrganiserApplicationReviewView();
        view.setApplication(application);
        view.setApplicant(applicant);
        view.setSkillMatch(skillMatchService.calculateMatch(applicant, job));
        return view;
    }

    private boolean matchesStatus(final OrganiserApplicationReviewView view, final String statusFilter) {
        if (statusFilter == null || statusFilter.isBlank()) {
            return true;
        }
        return view.getApplication().getStatus().name().equalsIgnoreCase(statusFilter.trim());
    }

    private boolean matchesKeyword(final OrganiserApplicationReviewView view, final String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        Applicant applicant = view.getApplicant();
        return contains(applicant == null ? null : applicant.getFullName(), normalizedKeyword)
                || contains(applicant == null ? null : applicant.getStudentId(), normalizedKeyword)
                || contains(applicant == null ? null : applicant.getProgramme(), normalizedKeyword)
                || contains(view.getApplication().getApplicantUserId(), normalizedKeyword);
    }

    private Comparator<OrganiserApplicationReviewView> resolveComparator(final String sortOption) {
        String normalizedSort = sortOption == null ? "" : sortOption.trim().toLowerCase(Locale.ROOT);
        return switch (normalizedSort) {
            case "match" -> Comparator
                    .comparingInt((OrganiserApplicationReviewView view) -> view.getSkillMatch().getMatchScore())
                    .reversed()
                    .thenComparing(this::appliedAtComparator, Comparator.reverseOrder());
            case "status" -> Comparator
                    .comparing((OrganiserApplicationReviewView view) -> view.getApplication().getStatus().name())
                    .thenComparing(this::appliedAtComparator, Comparator.reverseOrder());
            default -> Comparator.comparing(this::appliedAtComparator, Comparator.reverseOrder());
        };
    }

    private Instant appliedAtComparator(final OrganiserApplicationReviewView view) {
        Instant appliedAt = view.getApplication().getAppliedAt();
        return appliedAt == null ? Instant.EPOCH : appliedAt;
    }

    private boolean contains(final String value, final String normalizedKeyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }
}
