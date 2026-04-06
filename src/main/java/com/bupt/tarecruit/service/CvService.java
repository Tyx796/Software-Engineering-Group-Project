package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.CvDao;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.model.CV;
import com.bupt.tarecruit.util.FileStorageUtil;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public class CvService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "doc", "docx");
    private final ApplicantService applicantService;
    private final CvDao cvDao;

    public CvService() {
        this(new ApplicantService(), new CvDaoImpl());
    }

    public CvService(final ApplicantService applicantService) {
        this(applicantService, new CvDaoImpl());
    }

    public CvService(final ApplicantService applicantService, final CvDao cvDao) {
        this.applicantService = applicantService;
        this.cvDao = cvDao;
    }

    public String uploadCV(final String userId, final String originalFileName, final InputStream inputStream) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Please choose a CV file.");
        }
        String extension = extensionOf(originalFileName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("CV must be a PDF, DOC, or DOCX file.");
        }
        String fileName = "cv." + extension;
        Path targetPath = saveFile(userId, fileName, inputStream);
        linkToProfile(userId, fileName, extension);
        persistMetadata(userId, fileName, extension);
        return fileName;
    }

    /** @deprecated Use {@link #uploadCV} instead. */
    @Deprecated
    public String upload(final String userId, final String originalFileName, final InputStream inputStream) {
        return uploadCV(userId, originalFileName, inputStream);
    }

    public Optional<CV> findByUserId(final String userId) {
        return cvDao.findByUserId(userId);
    }

    public Path saveFile(final String userId, final String fileName, final InputStream inputStream) {
        try {
            Path userDirectory = FileStorageUtil.cvDirectory().resolve(userId);
            FileStorageUtil.ensureDirectory(userDirectory);
            Path targetPath = userDirectory.resolve(fileName);
            FileStorageUtil.saveFile(inputStream, targetPath);
            return targetPath;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to save CV.", exception);
        }
    }

    public void linkToProfile(final String userId, final String fileName, final String fileType) {
        applicantService.attachCv(userId, fileName);
    }

    private void persistMetadata(final String userId, final String fileName, final String fileType) {
        CV cv = new CV();
        cv.setUserId(userId);
        cv.setFileName(fileName);
        cv.setFileType(fileType);
        cv.setUploadedAt(Instant.now());
        cvDao.save(cv);
    }

    private String extensionOf(final String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase();
    }
}
