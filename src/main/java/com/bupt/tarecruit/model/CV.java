package com.bupt.tarecruit.model;

import java.time.Instant;

public class CV {
    private String userId;
    private String fileName;
    private String fileType;
    private Instant uploadedAt;

    public String getUserId() { return userId; }
    public void setUserId(final String userId) { this.userId = userId; }
    public String getFileName() { return fileName; }
    public void setFileName(final String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(final String fileType) { this.fileType = fileType; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(final Instant uploadedAt) { this.uploadedAt = uploadedAt; }
}
