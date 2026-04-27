package com.oraclejavabot.features.projects.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PROYECTO_DOCUMENTO")
public class ProjectDocumentEntity {

    @Id
    @GeneratedValue
    @Column(name = "DOCUMENT_ID")
    private UUID documentId;

    @Column(name = "PROJECT_ID", nullable = false)
    private UUID projectId;

    @Column(name = "DOCUMENT_TYPE", nullable = false)
    private String documentType;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FILE_URL", nullable = false)
    private String fileUrl;

    @Column(name = "STORAGE_PATH", nullable = false)
    private String storagePath;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "FILE_SIZE_BYTES")
    private Long fileSizeBytes;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public ProjectDocumentEntity() {}

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