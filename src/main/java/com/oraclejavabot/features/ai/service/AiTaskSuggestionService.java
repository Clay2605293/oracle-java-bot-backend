package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.AiTaskSuggestionResponseDTO;
import com.oraclejavabot.features.ai.model.AiTaskSuggestionEntity;
import com.oraclejavabot.features.ai.repository.AiTaskSuggestionRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;
import com.oraclejavabot.messaging.event.AiTaskGenerationResponseEvent;
import org.springframework.stereotype.Service;

import com.oraclejavabot.features.ai.dto.AiTaskApprovalRequestDTO;
import com.oraclejavabot.features.ai.dto.AiTaskApprovalResponseDTO;
import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.service.TaskService;
import org.springframework.transaction.annotation.Transactional;

import com.oraclejavabot.features.ai.dto.AiTaskSuggestionClearResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AiTaskSuggestionService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_APPROVED = "APPROVED";

    private final AiTaskSuggestionRepository suggestionRepository;
    private final ProjectRepository projectRepository;
    private final TaskService taskService;

    public AiTaskSuggestionService(
            AiTaskSuggestionRepository suggestionRepository,
            ProjectRepository projectRepository,
            TaskService taskService
    ) {
        this.suggestionRepository = suggestionRepository;
        this.projectRepository = projectRepository;
        this.taskService = taskService;
    }

    public int saveSuggestionsFromAiResponse(AiTaskGenerationResponseEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("AI response event is required");
        }

        UUID projectUuid = parseUuid(event.getProjectId());

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found: " + event.getProjectId());
        }

        if (event.getTasks() == null || event.getTasks().isEmpty()) {
            return 0;
        }

        List<AiTaskSuggestionEntity> suggestions = event.getTasks()
                .stream()
                .map(task -> toEntity(projectUuid, task))
                .toList();

        suggestionRepository.saveAll(suggestions);

        return suggestions.size();
    }

    public List<AiTaskSuggestionResponseDTO> getSuggestionsByProject(String projectId) {
        UUID projectUuid = parseUuid(projectId);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        return suggestionRepository.findByProjectId(projectUuid)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AiTaskSuggestionResponseDTO> getPendingSuggestionsByProject(String projectId) {
        UUID projectUuid = parseUuid(projectId);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        return suggestionRepository.findByProjectIdAndStatus(projectUuid, STATUS_PENDING)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private AiTaskSuggestionEntity toEntity(
            UUID projectUuid,
            AiTaskGenerationResponseEvent.Task task
    ) {
        AiTaskSuggestionEntity entity = new AiTaskSuggestionEntity();

        entity.setProjectId(projectUuid);
        entity.setTitulo(truncate(task.getTitulo(), 120));
        entity.setDescripcion(truncate(task.getDescripcion(), 500));
        entity.setTiempoEstimado(task.getTiempoEstimado());
        entity.setStatus(STATUS_PENDING);
        entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }

    private AiTaskSuggestionResponseDTO toResponseDTO(AiTaskSuggestionEntity entity) {
        return new AiTaskSuggestionResponseDTO(
                entity.getAiTaskId(),
                entity.getProjectId(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getTiempoEstimado(),
                entity.getStatus(),
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

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }

    public AiTaskSuggestionResponseDTO rejectSuggestion(String aiTaskId) {
        UUID suggestionUuid = parseUuid(aiTaskId);

        AiTaskSuggestionEntity suggestion = suggestionRepository.findById(suggestionUuid)
                .orElseThrow(() -> new IllegalArgumentException("AI suggestion not found: " + aiTaskId));

        if (STATUS_APPROVED.equalsIgnoreCase(suggestion.getStatus())) {
            throw new IllegalArgumentException("Cannot reject an approved AI suggestion");
        }

        if (STATUS_REJECTED.equalsIgnoreCase(suggestion.getStatus())) {
            return toResponseDTO(suggestion);
        }

        suggestion.setStatus(STATUS_REJECTED);

        AiTaskSuggestionEntity saved = suggestionRepository.save(suggestion);

        return toResponseDTO(saved);
    }


    private void validateApprovalRequest(AiTaskApprovalRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Approval request is required");
        }

        if (request.getFechaLimite() == null || request.getFechaLimite().isBlank()) {
            throw new IllegalArgumentException("fechaLimite is required");
        }

        if (request.getPrioridadId() == null) {
            throw new IllegalArgumentException("prioridadId is required");
        }
    }

    @Transactional
    public AiTaskApprovalResponseDTO approveSuggestion(
            String aiTaskId,
            AiTaskApprovalRequestDTO request
    ) {
        UUID suggestionUuid = parseUuid(aiTaskId);

        AiTaskSuggestionEntity suggestion = suggestionRepository.findById(suggestionUuid)
                .orElseThrow(() -> new IllegalArgumentException("AI suggestion not found: " + aiTaskId));

        if (STATUS_APPROVED.equalsIgnoreCase(suggestion.getStatus())) {
            throw new IllegalArgumentException("AI suggestion is already approved");
        }

        if (STATUS_REJECTED.equalsIgnoreCase(suggestion.getStatus())) {
            throw new IllegalArgumentException("Cannot approve a rejected AI suggestion");
        }

        validateApprovalRequest(request);

        TaskRequestDTO taskRequest = new TaskRequestDTO();
        taskRequest.setTitulo(suggestion.getTitulo());
        taskRequest.setDescripcion(suggestion.getDescripcion());
        taskRequest.setTiempoEstimado(suggestion.getTiempoEstimado());

        taskRequest.setFechaLimite(request.getFechaLimite());
        taskRequest.setPrioridadId(request.getPrioridadId());
        taskRequest.setSprintId(request.getSprintId());

        TaskResponseDTO createdTask = taskService.createTask(
                suggestion.getProjectId().toString(),
                taskRequest
        );

        suggestion.setStatus(STATUS_APPROVED);

        AiTaskSuggestionEntity savedSuggestion = suggestionRepository.save(suggestion);

        return new AiTaskApprovalResponseDTO(
                toResponseDTO(savedSuggestion),
                createdTask
        );
    }

    @Transactional
    public AiTaskSuggestionClearResponseDTO clearSuggestionsByProject(String projectId) {
        UUID projectUuid = parseUuid(projectId);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        Long deletedCount = suggestionRepository.countByProjectId(projectUuid);

        suggestionRepository.deleteByProjectId(projectUuid);

        return new AiTaskSuggestionClearResponseDTO(
                "AI task suggestions cleared successfully",
                projectUuid.toString(),
                deletedCount
        );
    }

}