package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Applicant;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicantServiceTest {
    @Test
    void createProfileAndAttachCv() throws Exception {
        Path file = Files.createTempFile("applicants", ".json");
        ApplicantService service = new ApplicantService(file);

        Applicant profile = service.createProfile(
                "user-1", "Alice Chen", "+86 13800000000", "20260001", "Computer Science", "Tutor");
        service.attachCv("user-1", "cv.pdf");

        assertEquals("Alice Chen", profile.getFullName());
        assertEquals("cv.pdf", service.findByUserId("user-1").orElseThrow().getCvFileName());
    }

    @Test
    void invalidPhoneIsRejected() throws Exception {
        Path file = Files.createTempFile("applicants", ".json");
        ApplicantService service = new ApplicantService(file);

        assertThrows(IllegalArgumentException.class,
                () -> service.createProfile("user-1", "Alice", "abc", "20260001", "CS", ""));
    }

    @Test
    void createProfilePersistsSkillsAndPreferredWorkingDays() throws Exception {
        Path file = Files.createTempFile("applicants", ".json");
        ApplicantService service = new ApplicantService(file);

        service.createProfile(
                "user-1",
                "Alice Chen",
                "+86 13800000000",
                "20260001",
                "Computer Science",
                "Tutor",
                List.of("Java", "Java", " Communication "),
                List.of("Monday", "", "Wednesday"));

        Applicant profile = service.findByUserId("user-1").orElseThrow();
        assertEquals(List.of("Java", "Communication"), profile.getSkills());
        assertEquals(List.of("Monday", "Wednesday"), profile.getPreferredWorkingDays());
    }
}
