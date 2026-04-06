package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.ApplicantDao;
import com.bupt.tarecruit.dao.impl.ApplicantDaoImpl;
import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.util.DataValidator;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ApplicantService {
    private final ApplicantDao applicantDao;

    public ApplicantService() {
        this(new ApplicantDaoImpl());
    }

    public ApplicantService(final Path applicantsFile) {
        this(new ApplicantDaoImpl(applicantsFile));
    }

    public ApplicantService(final ApplicantDao applicantDao) {
        this.applicantDao = applicantDao;
    }

    public Applicant createProfile(final String userId, final String fullName, final String phone,
                                   final String studentId, final String programme, final String bio) {
        validateProfile(userId, fullName, phone, studentId, programme);
        List<Applicant> profiles = getAllProfiles();
        Applicant profile = profiles.stream()
                .filter(current -> current.getUserId().equals(userId))
                .findFirst()
                .orElseGet(Applicant::new);
        if (profile.getUserId() == null) {
            profile.setUserId(userId);
            profiles.add(profile);
        }
        profile.setFullName(fullName.trim());
        profile.setPhone(phone.trim());
        profile.setStudentId(studentId.trim());
        profile.setProgramme(programme.trim());
        profile.setBio(bio == null ? "" : bio.trim());
        profile.setUpdatedAt(Instant.now());
        applicantDao.saveAll(profiles);
        return profile;
    }

    /** @deprecated Use {@link #createProfile} instead. */
    @Deprecated
    public Applicant createOrUpdateProfile(final String userId, final String fullName, final String phone,
                                           final String studentId, final String programme, final String bio) {
        return createProfile(userId, fullName, phone, studentId, programme, bio);
    }

    public Optional<Applicant> findByUserId(final String userId) {
        return applicantDao.findByUserId(userId);
    }

    public void attachCv(final String userId, final String fileName) {
        findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Please create your profile before uploading a CV."));
        List<Applicant> profiles = getAllProfiles();
        profiles.stream().filter(current -> current.getUserId().equals(userId)).findFirst().ifPresent(current -> {
            current.setCvFileName(fileName);
            current.setUpdatedAt(Instant.now());
        });
        applicantDao.saveAll(profiles);
    }

    public List<Applicant> getAllProfiles() {
        return applicantDao.findAll();
    }

    public void validateProfile(final String userId, final String fullName, final String phone,
                                final String studentId, final String programme) {
        DataValidator.validateRequired(userId, "User ID");
        DataValidator.validateRequired(fullName, "Full name");
        DataValidator.validatePhone(phone);
        DataValidator.validateRequired(studentId, "Student ID");
        DataValidator.validateRequired(programme, "Programme");
    }
}
