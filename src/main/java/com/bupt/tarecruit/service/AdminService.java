package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.ApplicantLimitPolicyDao;
import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.model.ApplicantLimitAdminView;
import com.bupt.tarecruit.model.ApplicantLimitPolicy;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.SystemSettings;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.DataValidator;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class AdminService {
    private final UserService userService;
    private final ApplicantService applicantService;
    private final SettingsService settingsService;
    private final ApplicantLimitPolicyDao applicantLimitPolicyDao;
    private final RecruitmentPolicyService recruitmentPolicyService;

    public AdminService() {
        this(
                new UserService(),
                new ApplicantService(),
                new SettingsService(),
                new ApplicantLimitPolicyDaoImpl(),
                new RecruitmentPolicyService());
    }

    public AdminService(final UserService userService,
            final ApplicantService applicantService,
            final SettingsService settingsService,
            final ApplicantLimitPolicyDao applicantLimitPolicyDao,
            final RecruitmentPolicyService recruitmentPolicyService) {
        this.userService = userService;
        this.applicantService = applicantService;
        this.settingsService = settingsService;
        this.applicantLimitPolicyDao = applicantLimitPolicyDao;
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

    private ApplicantLimitAdminView buildApplicantLimitView(final User user) {
        ApplicantLimitAdminView view = new ApplicantLimitAdminView();
        view.setUser(user);
        view.setProfile(applicantService.findByUserId(user.getId()).orElse(null));
        view.setApplicationLimitOverride(applicantLimitPolicyDao.findByUserId(user.getId())
                .map(ApplicantLimitPolicy::getApplicationLimitOverride)
                .orElse(null));
        view.setEffectiveApplicationLimit(recruitmentPolicyService.resolveApplicantApplicationLimit(user.getId()));
        view.setActiveApplicationCount(recruitmentPolicyService.countActiveApplications(user.getId()));
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
