package com.bupt.tarecruit.service;

import com.bupt.tarecruit.util.AppPaths;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class CvService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "doc", "docx");
    private final ApplicantService applicantService;

    public CvService() {
        this(new ApplicantService());
    }

    public CvService(final ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    public String upload(final String userId, final String originalFileName, final InputStream inputStream) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Please choose a CV file.");
        }
        String extension = extensionOf(originalFileName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("CV must be a PDF, DOC, or DOCX file.");
        }
        try {
            Path userDirectory = AppPaths.cvDirectory().resolve(userId);
            AppPaths.ensureDirectory(userDirectory);
            String fileName = "cv." + extension;
            Files.copy(inputStream, userDirectory.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            applicantService.attachCv(userId, fileName);
            return fileName;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to save CV.", exception);
        }
    }

    private String extensionOf(final String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase();
    }
}
