package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.AiBacklogGenerationRequestDTO;
import com.oraclejavabot.features.ai.dto.AiBacklogGenerationResponseDTO;
import com.oraclejavabot.features.projects.model.ProjectDocumentEntity;
import com.oraclejavabot.features.projects.model.ProjectEntity;
import com.oraclejavabot.features.projects.repository.ProjectDocumentRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;
import com.oraclejavabot.messaging.event.AiTaskGenerationRequestEvent;
import com.oraclejavabot.messaging.producer.AiTaskProducer;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AiBacklogGenerationService {

    private final ProjectRepository projectRepository;
    private final ProjectDocumentRepository projectDocumentRepository;
    private final AiTaskProducer aiTaskProducer;

    public AiBacklogGenerationService(
            ProjectRepository projectRepository,
            ProjectDocumentRepository projectDocumentRepository,
            AiTaskProducer aiTaskProducer
    ) {
        this.projectRepository = projectRepository;
        this.projectDocumentRepository = projectDocumentRepository;
        this.aiTaskProducer = aiTaskProducer;
    }

    public AiBacklogGenerationResponseDTO generateBacklog(
            String projectId,
            AiBacklogGenerationRequestDTO request
    ) {
        UUID projectUuid = parseUuid(projectId, "Invalid UUID format: ");
        Double maxHours = validateMaxHours(request);
        List<UUID> documentUuids = validateAndParseDocumentIds(request);

        ProjectEntity project = projectRepository.findById(projectUuid)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        List<ProjectDocumentEntity> documents =
                projectDocumentRepository.findByProjectIdAndDocumentIdIn(projectUuid, documentUuids);

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("No selected documents found for project: " + projectId);
        }

        if (documents.size() != documentUuids.size()) {
            throw new IllegalArgumentException("One or more selected documents do not belong to project: " + projectId);
        }

        AiTaskGenerationRequestEvent event = new AiTaskGenerationRequestEvent();
        event.setProjectId(projectUuid.toString());
        event.setProjectName(project.getNombre());
        event.setProjectDescription(project.getDescripcion());
        event.setMaxHours(maxHours);
        event.setDocuments(
                documents.stream()
                        .map(this::toEventDocument)
                        .toList()
        );

        aiTaskProducer.sendTaskGenerationRequest(event);

        return new AiBacklogGenerationResponseDTO(
                "AI task generation event sent to Kafka",
                projectUuid.toString(),
                documents.size(),
                maxHours
        );
    }

    private AiTaskGenerationRequestEvent.Document toEventDocument(ProjectDocumentEntity documentEntity) {
        AiTaskGenerationRequestEvent.Document document = new AiTaskGenerationRequestEvent.Document();

        document.setType(documentEntity.getDocumentType());
        document.setContent(null);
        document.setUrl(documentEntity.getFileUrl());

        return document;
    }

    private Double validateMaxHours(AiBacklogGenerationRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        if (request.getMaxHours() == null) {
            throw new IllegalArgumentException("maxHours is required");
        }

        if (request.getMaxHours() <= 0) {
            throw new IllegalArgumentException("maxHours must be greater than 0");
        }

        if (request.getMaxHours() > 200) {
            throw new IllegalArgumentException("maxHours must not exceed 200");
        }

        return request.getMaxHours();
    }

    private List<UUID> validateAndParseDocumentIds(AiBacklogGenerationRequestDTO request) {
        if (request.getDocumentIds() == null || request.getDocumentIds().isEmpty()) {
            throw new IllegalArgumentException("At least one document must be selected");
        }

        Set<String> uniqueDocumentIds = new HashSet<>(request.getDocumentIds());

        if (uniqueDocumentIds.size() != request.getDocumentIds().size()) {
            throw new IllegalArgumentException("Duplicate document IDs are not allowed");
        }

        return request.getDocumentIds()
                .stream()
                .map(documentId -> parseUuid(documentId, "Invalid document UUID format: "))
                .toList();
    }

    private UUID parseUuid(String value, String errorPrefix) {
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(errorPrefix + value);
        }
    }
}