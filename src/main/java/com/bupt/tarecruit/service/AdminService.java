package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.ApplicantLimitPolicyDao;
import com.bupt.tarecruit.dao.ApplicationDao;
import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.model.ApplicantLimitAdminView;
import com.bupt.tarecruit.model.AdminDashboardSummary;
import com.bupt.tarecruit.model.AdminJobSupervisionView;
import com.bupt.tarecruit.model.ApplicantLimitPolicy;
import com.bupt.tarecruit.model.ApplicantAcceptedAssignmentView;
import com.bupt.tarecruit.model.ApplicantWorkloadView;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.SystemSettings;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.DataValidator;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdminService {
    public static final int DEFAULT_WORKLOAD_THRESHOLD = 20;

    private final UserService userService;
    private final ApplicantService applicantService;
    private final SettingsService settingsService;
    private final ApplicantLimitPolicyDao applicantLimitPolicyDao;
    private final JobService jobService;
    private final ApplicationDao applicationDao;
    private final RecruitmentPolicyService recruitmentPolicyService;

    public AdminService() {
        this(
                new UserService(),
                new ApplicantService(),
                new SettingsService(),
                new ApplicantLimitPolicyDaoImpl(),
                new JobService(),
                new ApplicationDaoImpl(),
                new RecruitmentPolicyService());
    }

    public AdminService(final UserService userService,
            final ApplicantService applicantService,
            final SettingsService settingsService,
            final ApplicantLimitPolicyDao applicantLimitPolicyDao,
            final JobService jobService,
            final ApplicationDao applicationDao,
            final RecruitmentPolicyService recruitmentPolicyService) {
        this.userService = userService;
        this.applicantService = applicantService;
        this.settingsService = settingsService;
        this.applicantLimitPolicyDao = applicantLimitPolicyDao;
        this.jobService = jobService;
        this.applicationDao = applicationDao;
        this.recruitmentPolicyService = recruitmentPolicyService;
    }

    public int getGlobalDefaultApplicantApplicationLimit() {
        return settingsService.getDefaultApplicantApplicationLimit();
    }

    public SystemSettings updateGlobalDefaultApplicantApplicationLimit(final int limit) {
        return settingsService.updateDefaultApplicantApplicationLimit(limit);
    }

    public List<ApplicantLimitAdminView> getApplicantLimitViews() {
        return userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.APPLICANT)
                .sorted(Comparator.comparing(User::getEmail, String.CASE_INSENSITIVE_ORDER))
                .map(this::buildApplicantLimitView)
                .toList();
    }

    public ApplicantLimitAdminView getApplicantLimitView(final String applicantUserId) {
        return buildApplicantLimitView(requireApplicantUser(applicantUserId));
    }

    public ApplicantLimitPolicy saveApplicantApplicationLimitOverride(final String applicantUserId, final int limit) {
        requireApplicantUser(applicantUserId);
        if (limit < 0) {
            throw new IllegalArgumentException("Applicant application limit override must be zero or greater.");
        }
        ApplicantLimitPolicy policy = applicantLimitPolicyDao.findByUserId(applicantUserId)
                .orElseGet(ApplicantLimitPolicy::new);
        policy.setUserId(applicantUserId);
        policy.setApplicationLimitOverride(limit);
        policy.setUpdatedAt(Instant.now());
        applicantLimitPolicyDao.save(policy);
        return policy;
    }

    public void clearApplicantApplicationLimitOverride(final String applicantUserId) {
        requireApplicantUser(applicantUserId);
        applicantLimitPolicyDao.deleteByUserId(applicantUserId);
    }

    public long countApplicantUsers() {
        return userService.getAllUsers().stream().filter(user -> user.getRole() == Role.APPLICANT).count();
    }

    public long countApplicantsUsingOverride() {
        return getApplicantLimitViews().stream().filter(ApplicantLimitAdminView::isUsingOverride).count();
    }

    public long countApplicantsOverEffectiveLimit() {
        return getApplicantLimitViews().stream().filter(ApplicantLimitAdminView::isOverEffectiveLimit).count();
    }

    public AdminDashboardSummary getDashboardSummary() {
        List<User> users = userService.getAllUsers();
        List<Job> jobs = jobService.getAllJobs();
        List<Application> applications = applicationDao.findAll();

        AdminDashboardSummary summary = new AdminDashboardSummary();
        summary.setTotalUsers(users.size());
        summary.setTotalApplicants(users.stream().filter(user -> user.getRole() == Role.APPLICANT).count());
        summary.setTotalOrganisers(users.stream().filter(user -> user.getRole() == Role.ORGANISER).count());
        summary.setTotalJobs(jobs.size());
        summary.setOpenJobs(jobs.stream().filter(job -> JobService.STATUS_OPEN.equals(job.getStatus())).count());
        summary.setFullJobs(jobs.stream().filter(job -> recruitmentPolicyService.isJobFull(job.getId())).count());
        summary.setTotalApplications(applications.size());
        summary.setPendingOrReviewingApplications(applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.PENDING
                        || application.getStatus() == ApplicationStatus.REVIEWING)
                .count());
        summary.setAcceptedApplications(applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.ACCEPTED)
                .count());
        summary.setApplicantsAtLimit(getApplicantLimitViews().stream()
                .filter(view -> view.getActiveApplicationCount() >= view.getEffectiveApplicationLimit())
                .count());
        summary.setOverloadedApplicants(getApplicantWorkloadViews(DEFAULT_WORKLOAD_THRESHOLD).stream()
                .filter(ApplicantWorkloadView::isOverloaded)
                .count());
        return summary;
    }

    public List<ApplicantWorkloadView> getApplicantWorkloadViews(final int workloadThreshold) {
        if (workloadThreshold <= 0) {
            throw new IllegalArgumentException("Workload threshold must be greater than zero.");
        }
        Map<String, Job> jobsById = jobService.getAllJobs().stream()
                .collect(Collectors.toMap(Job::getId, Function.identity(), (left, right) -> left));
        return userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.APPLICANT)
                .sorted(Comparator.comparing(User::getEmail, String.CASE_INSENSITIVE_ORDER))
                .map(user -> buildApplicantWorkloadView(user, jobsById, workloadThreshold))
                .toList();
    }

    public List<AdminJobSupervisionView> getJobSupervisionViews() {
        Map<String, User> usersById = userService.getAllUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (left, right) -> left));
        return jobService.getAllJobs().stream()
                .sorted(Comparator.comparing(Job::getCreatedAt).reversed())
                .map(job -> buildJobSupervisionView(job, usersById.get(job.getOrganiserUserId())))
                .toList();
    }

    private ApplicantLimitAdminView buildApplicantLimitView(final User user) {
        ApplicantLimitAdminView view = new ApplicantLimitAdminView();
        view.setUser(user);
        view.setProfile(applicantService.findByUserId(user.getId()).orElse(null));
        view.setApplicationLimitOverride(applicantLimitPolicyDao.findByUserId(user.getId())
                .map(ApplicantLimitPolicy::getApplicationLimitOverride)
                .orElse(null));
        view.setEffectiveApplicationLimit(recruitmentPolicyService.resolveApplicantApplicationLimit(user.getId()));
        view.setActiveApplicationCount(recruitmentPolicyService.countActiveApplications(user.getId()));
        view.setAcceptedAssignmentCount((int) applicationDao.findByApplicantId(user.getId()).stream()
                .filter(application -> application.getStatus() == ApplicationStatus.ACCEPTED)
                .count());
        return view;
    }

    private ApplicantWorkloadView buildApplicantWorkloadView(
            final User user,
            final Map<String, Job> jobsById,
            final int workloadThreshold) {
        List<ApplicantAcceptedAssignmentView> assignments = applicationDao.findByApplicantId(user.getId()).stream()
                .filter(application -> application.getStatus() == ApplicationStatus.ACCEPTED)
                .map(application -> jobsById.get(application.getJobId()))
                .filter(job -> job != null)
                .map(this::buildAcceptedAssignmentView)
                .toList();
        ApplicantWorkloadView view = new ApplicantWorkloadView();
        view.setUser(user);
        view.setProfile(applicantService.findByUserId(user.getId()).orElse(null));
        view.setAcceptedAssignments(assignments);
        view.setTotalHoursPerWeek(assignments.stream().mapToInt(ApplicantAcceptedAssignmentView::getHoursPerWeek).sum());
        view.setWorkloadThreshold(workloadThreshold);
        return view;
    }

    private ApplicantAcceptedAssignmentView buildAcceptedAssignmentView(final Job job) {
        ApplicantAcceptedAssignmentView assignment = new ApplicantAcceptedAssignmentView();
        assignment.setJobId(job.getId());
        assignment.setTitle(job.getTitle());
        assignment.setDepartment(job.getDepartment());
        assignment.setOrganiserUserId(job.getOrganiserUserId());
        assignment.setHoursPerWeek(job.getHoursPerWeek());
        return assignment;
    }

    private AdminJobSupervisionView buildJobSupervisionView(final Job job, final User organiser) {
        List<Application> applications = applicationDao.findByJobId(job.getId());
        AdminJobSupervisionView view = new AdminJobSupervisionView();
        view.setJob(job);
        view.setOrganiser(organiser);
        view.setAcceptedCount(recruitmentPolicyService.countAcceptedApplications(job.getId()));
        view.setRemainingSlots(recruitmentPolicyService.remainingAssistantSlots(job.getId()));
        view.setFull(recruitmentPolicyService.isJobFull(job.getId()));
        view.setPendingCount(applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.PENDING)
                .count());
        view.setReviewingCount(applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.REVIEWING)
                .count());
        view.setAcceptedStatusCount(applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.ACCEPTED)
                .count());
        view.setRejectedCount(applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.REJECTED)
                .count());
        view.setWithdrawnCount(applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.WITHDRAWN)
                .count());
        view.setCancelledCount(applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.CANCELLED)
                .count());
        return view;
    }

    private User requireApplicantUser(final String applicantUserId) {
        DataValidator.validateRequired(applicantUserId, "Applicant user ID");
        User user = userService.findById(applicantUserId)
                .orElseThrow(() -> new IllegalArgumentException("Applicant user not found."));
        if (user.getRole() != Role.APPLICANT) {
            throw new IllegalArgumentException("Only applicant accounts can have applicant limit overrides.");
        }
        return user;
    }
}
