package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.CvDao;
import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.model.CV;
import com.bupt.tarecruit.util.DataValidator;
import com.bupt.tarecruit.util.FileStorageUtil;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class CvService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "doc", "docx");
    private final ApplicantService applicantService;
    private final CvDao cvDao;
    private final Path cvRootDirectory;

    public CvService() {
        this(new ApplicantService(), new CvDaoImpl(), FileStorageUtil.cvDirectory());
    }

    public CvService(final ApplicantService applicantService) {
        this(applicantService, new CvDaoImpl(), FileStorageUtil.cvDirectory());
    }

    public CvService(final ApplicantService applicantService, final CvDao cvDao) {
        this(applicantService, cvDao, FileStorageUtil.cvDirectory());
    }

    public CvService(final ApplicantService applicantService, final CvDao cvDao, final Path cvRootDirectory) {
        this.applicantService = applicantService;
        this.cvDao = cvDao;
        this.cvRootDirectory = cvRootDirectory;
    }

    public String uploadCV(final String userId, final String originalFileName, final InputStream inputStream) {
        return replaceCV(userId, originalFileName, inputStream);
    }

    public String replaceCV(final String userId, final String originalFileName, final InputStream inputStream) {
        DataValidator.validateRequired(userId, "User ID");
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Please choose a CV file.");
        }
        if (inputStream == null) {
            throw new IllegalArgumentException("Please choose a CV file.");
        }
        applicantService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Please create your profile before uploading a CV."));
        String extension = extensionOf(originalFileName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("CV must be a PDF, DOC, or DOCX file.");
        }
        String fileName = "cv." + extension;
        Path targetPath = saveFile(userId, fileName, inputStream);
        deleteStaleFiles(userId, fileName);
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

    public boolean hasUploadedCv(final String userId) {
        return findByUserId(userId).isPresent();
    }

    public Optional<String> currentCvFileName(final String userId) {
        return findByUserId(userId).map(CV::getFileName);
    }

    public Path saveFile(final String userId, final String fileName, final InputStream inputStream) {
        try {
            Path userDirectory = cvRootDirectory.resolve(userId);
            FileStorageUtil.ensureDirectory(userDirectory);
            Path targetPath = userDirectory.resolve(fileName);
            FileStorageUtil.saveFile(inputStream, targetPath);
            if (Files.size(targetPath) == 0L) {
                Files.deleteIfExists(targetPath);
                throw new IllegalArgumentException("CV file cannot be empty.");
            }
            return targetPath;
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            }
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

    private void deleteStaleFiles(final String userId, final String currentFileName) {
        Path userDirectory = cvRootDirectory.resolve(userId);
        if (!Files.exists(userDirectory)) {
            return;
        }
        try (Stream<Path> paths = Files.list(userDirectory)) {
            paths.filter(path -> !path.getFileName().toString().equals(currentFileName))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception exception) {
                            throw new IllegalStateException("Unable to remove old CV file: " + path, exception);
                        }
                    });
        } catch (Exception exception) {
            if (exception instanceof IllegalStateException illegalStateException) {
                throw illegalStateException;
            }
            throw new IllegalStateException("Unable to clean old CV files.", exception);
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
