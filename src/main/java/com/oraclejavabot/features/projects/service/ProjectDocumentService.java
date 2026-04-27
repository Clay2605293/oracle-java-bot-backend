package com.oraclejavabot.features.projects.service;

import com.oraclejavabot.features.projects.dto.ProjectDocumentResponseDTO;
import com.oraclejavabot.features.projects.model.ProjectDocumentEntity;
import com.oraclejavabot.features.projects.repository.ProjectDocumentRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectDocumentService {

    private final ProjectDocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final SupabaseStorageService storageService;

    public ProjectDocumentService(
            ProjectDocumentRepository documentRepository,
            ProjectRepository projectRepository,
            SupabaseStorageService storageService
    ) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
        this.storageService = storageService;
    }

    public ProjectDocumentResponseDTO uploadDocument(
            String projectId,
            String documentType,
            MultipartFile file
    ) {
        UUID projectUuid = parseUuid(projectId);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        String normalizedDocumentType = normalizeDocumentType(documentType);

        SupabaseStorageService.UploadedFile uploadedFile =
                storageService.uploadProjectDocument(projectUuid, file);

        ProjectDocumentEntity entity = new ProjectDocumentEntity();
        entity.setProjectId(projectUuid);
        entity.setDocumentType(normalizedDocumentType);
        entity.setFileName(uploadedFile.getFileName());
        entity.setFileUrl(uploadedFile.getFileUrl());
        entity.setStoragePath(uploadedFile.getStoragePath());
        entity.setContentType(uploadedFile.getContentType());
        entity.setFileSizeBytes(uploadedFile.getFileSizeBytes());
        entity.setCreatedAt(LocalDateTime.now());

        ProjectDocumentEntity saved = documentRepository.save(entity);

        return toResponseDTO(saved);
    }

    public List<ProjectDocumentResponseDTO> getDocumentsByProject(String projectId) {
        UUID projectUuid = parseUuid(projectId);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        return documentRepository.findByProjectId(projectUuid)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ProjectDocumentResponseDTO> getDocumentsByProjectAndType(
            String projectId,
            String documentType
    ) {
        UUID projectUuid = parseUuid(projectId);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        String normalizedDocumentType = normalizeDocumentType(documentType);

        return documentRepository.findByProjectIdAndDocumentType(projectUuid, normalizedDocumentType)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private ProjectDocumentResponseDTO toResponseDTO(ProjectDocumentEntity entity) {
        return new ProjectDocumentResponseDTO(
                entity.getDocumentId(),
                entity.getProjectId(),
                entity.getDocumentType(),
                entity.getFileName(),
                entity.getFileUrl(),
                entity.getStoragePath(),
                entity.getContentType(),
                entity.getFileSizeBytes(),
                entity.getCreatedAt()
        );
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid UUID format: " + value);
        }
    }

    private String normalizeDocumentType(String documentType) {
        if (documentType == null || documentType.isBlank()) {
            return "OTHER";
        }

        String normalized = documentType.trim().toUpperCase();

        return switch (normalized) {
            case "SRS", "WBS", "DESIGN", "REQUIREMENTS", "OTHER" -> normalized;
            default -> throw new IllegalArgumentException(
                    "Invalid document type: " + documentType +
                    ". Allowed values: SRS, WBS, DESIGN, REQUIREMENTS, OTHER"
            );
        };
    }
}