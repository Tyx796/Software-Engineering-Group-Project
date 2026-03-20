package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.ApplicantProfile;
import com.bupt.tarecruit.util.AppPaths;
import com.bupt.tarecruit.util.JsonStorage;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ApplicantService {
    private final Path applicantsFile;

    public ApplicantService() {
        this(AppPaths.dataDirectory().resolve("applicants.json"));
    }

    public ApplicantService(final Path applicantsFile) {
        this.applicantsFile = applicantsFile;
    }

    public ApplicantProfile createOrUpdateProfile(final String userId, final String fullName, final String phone,
            final String studentId, final String programme, final String bio) {
        validate(fullName, phone, studentId, programme);
        List<ApplicantProfile> profiles = getAllProfiles();
        ApplicantProfile profile = profiles.stream()
                .filter(current -> current.getUserId().equals(userId))
                .findFirst()
                .orElseGet(ApplicantProfile::new);
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
        JsonStorage.writeList(applicantsFile, profiles);
        return profile;
    }

    public Optional<ApplicantProfile> findByUserId(final String userId) {
        return getAllProfiles().stream().filter(profile -> profile.getUserId().equals(userId)).findFirst();
    }

    public void attachCv(final String userId, final String fileName) {
        ApplicantProfile profile = findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Please create your profile before uploading a CV."));
        List<ApplicantProfile> profiles = getAllProfiles();
        profiles.stream().filter(current -> current.getUserId().equals(userId)).findFirst().ifPresent(current -> {
            current.setCvFileName(fileName);
            current.setUpdatedAt(Instant.now());
        });
        JsonStorage.writeList(applicantsFile, profiles);
    }

    public List<ApplicantProfile> getAllProfiles() {
        return JsonStorage.readList(applicantsFile, new TypeToken<>() { });
    }

    private void validate(final String fullName, final String phone, final String studentId, final String programme) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (programme == null || programme.isBlank()) {
            throw new IllegalArgumentException("Programme is required.");
        }
    }
}
