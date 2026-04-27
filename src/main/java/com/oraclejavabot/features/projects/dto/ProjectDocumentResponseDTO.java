package com.oraclejavabot.features.projects.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProjectDocumentResponseDTO {

    private UUID documentId;
    private UUID projectId;
    private String documentType;
    private String fileName;
    private String fileUrl;
    private String storagePath;
    private String contentType;
    private Long fileSizeBytes;
    private LocalDateTime createdAt;

    public ProjectDocumentResponseDTO() {}

    public ProjectDocumentResponseDTO(
            UUID documentId,
            UUID projectId,
            String documentType,
            String fileName,
            String fileUrl,
            String storagePath,
            String contentType,
            Long fileSizeBytes,
            LocalDateTime createdAt
    ) {
        this.documentId = documentId;
        this.projectId = projectId;
        this.documentType = documentType;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.storagePath = storagePath;
        this.contentType = contentType;
        this.fileSizeBytes = fileSizeBytes;
        this.createdAt = createdAt;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}