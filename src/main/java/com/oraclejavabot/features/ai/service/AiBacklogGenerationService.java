package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.AiBacklogGenerationResponseDTO;
import com.oraclejavabot.features.projects.model.ProjectDocumentEntity;
import com.oraclejavabot.features.projects.model.ProjectEntity;
import com.oraclejavabot.features.projects.repository.ProjectDocumentRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;
import com.oraclejavabot.messaging.event.AiTaskGenerationRequestEvent;
import com.oraclejavabot.messaging.producer.AiTaskProducer;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public AiBacklogGenerationResponseDTO generateBacklog(String projectId) {
        UUID projectUuid = parseUuid(projectId);

        ProjectEntity project = projectRepository.findById(projectUuid)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        List<ProjectDocumentEntity> documents = projectDocumentRepository.findByProjectId(projectUuid);

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("Project has no documents uploaded: " + projectId);
        }

        AiTaskGenerationRequestEvent event = new AiTaskGenerationRequestEvent();
        event.setProjectId(projectUuid.toString());
        event.setProjectName(project.getNombre());
        event.setProjectDescription(project.getDescripcion());
        event.setDocuments(
                documents.stream()
                        .map(this::toEventDocument)
                        .toList()
        );

        aiTaskProducer.sendTaskGenerationRequest(event);

        return new AiBacklogGenerationResponseDTO(
                "AI task generation event sent to Kafka",
                projectUuid.toString(),
                documents.size()
        );
    }

    private AiTaskGenerationRequestEvent.Document toEventDocument(ProjectDocumentEntity documentEntity) {
        AiTaskGenerationRequestEvent.Document document = new AiTaskGenerationRequestEvent.Document();

        document.setType(documentEntity.getDocumentType());
        document.setContent(null);
        document.setUrl(documentEntity.getFileUrl());

        return document;
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid UUID format: " + value);
        }
    }
}