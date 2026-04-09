package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CvServiceTest {
    @Test
    void requiresSupportedExtension() throws Exception {
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path cvRootDirectory = Files.createTempDirectory("cv-root");
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        applicantService.createProfile("user-1", "Alice", "+1 202 555 0100", "20260001", "CS", "");
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);

        assertThrows(IllegalArgumentException.class,
                () -> cvService.uploadCV("user-1", "resume.txt", new ByteArrayInputStream(new byte[0])));
    }

    @Test
    void rejectsEmptyFile() throws Exception {
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path cvRootDirectory = Files.createTempDirectory("cv-root");
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        applicantService.createProfile("user-1", "Alice", "+1 202 555 0100", "20260001", "CS", "");
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cvService.uploadCV("user-1", "resume.pdf", new ByteArrayInputStream(new byte[0])));

        assertEquals("CV file cannot be empty.", exception.getMessage());
        assertTrue(cvService.findByUserId("user-1").isEmpty());
    }

    @Test
    void replaceCvKeepsOnlyCurrentFileAndUpdatesMetadata() throws Exception {
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path cvRootDirectory = Files.createTempDirectory("cv-root");
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        applicantService.createProfile("user-1", "Alice", "+1 202 555 0100", "20260001", "CS", "");
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);

        cvService.uploadCV("user-1", "resume.pdf", new ByteArrayInputStream("pdf-data".getBytes()));
        cvService.replaceCV("user-1", "resume.docx", new ByteArrayInputStream("docx-data".getBytes()));

        Path userDirectory = cvRootDirectory.resolve("user-1");
        assertFalse(Files.exists(userDirectory.resolve("cv.pdf")));
        assertTrue(Files.exists(userDirectory.resolve("cv.docx")));
        assertEquals("cv.docx", cvService.currentCvFileName("user-1").orElseThrow());
        assertEquals("cv.docx", applicantService.findByUserId("user-1").orElseThrow().getCvFileName());
        assertEquals("docx", cvService.findByUserId("user-1").orElseThrow().getFileType());
    }

    @Test
    void reportsCurrentUploadStatus() throws Exception {
        Path applicantsFile = Files.createTempFile("applicants", ".json");
        Path cvsFile = Files.createTempFile("cvs", ".json");
        Path cvRootDirectory = Files.createTempDirectory("cv-root");
        ApplicantService applicantService = new ApplicantService(applicantsFile);
        applicantService.createProfile("user-1", "Alice", "+1 202 555 0100", "20260001", "CS", "");
        CvService cvService = new CvService(applicantService, new CvDaoImpl(cvsFile), cvRootDirectory);

        assertFalse(cvService.hasUploadedCv("user-1"));

        cvService.uploadCV("user-1", "resume.pdf", new ByteArrayInputStream("pdf-data".getBytes()));

        assertTrue(cvService.hasUploadedCv("user-1"));
        assertEquals("cv.pdf", cvService.currentCvFileName("user-1").orElseThrow());
    }
}
