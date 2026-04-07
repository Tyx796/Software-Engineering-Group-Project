package com.bupt.tarecruit.service;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CvServiceTest {
    @Test
    void requiresSupportedExtension() throws Exception {
        Path file = Files.createTempFile("applicants", ".json");
        ApplicantService applicantService = new ApplicantService(file);
        applicantService.createProfile("user-1", "Alice", "+1 202 555 0100", "20260001", "CS", "");
        CvService cvService = new CvService(applicantService);

        assertThrows(IllegalArgumentException.class,
                () -> cvService.uploadCV("user-1", "resume.txt", new ByteArrayInputStream(new byte[0])));
    }
}
