package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.model.TaskSemanticEmbeddingEntity;
import com.oraclejavabot.features.ai.repository.TaskSemanticEmbeddingRepository;
import com.oraclejavabot.messaging.event.AiTaskEmbeddingResponseEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TaskEmbeddingService {

    private static final String STATUS_COMPLETED = "COMPLETED";

    private final TaskSemanticEmbeddingRepository embeddingRepository;

    public TaskEmbeddingService(TaskSemanticEmbeddingRepository embeddingRepository) {
        this.embeddingRepository = embeddingRepository;
    }

    @Transactional
    public void saveEmbeddingFromAiResponse(AiTaskEmbeddingResponseEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Invalid task embedding response");
        }

        if (!STATUS_COMPLETED.equalsIgnoreCase(event.getStatus())) {
            System.err.println(
                    "⚠️ Task embedding generation failed for task "
                            + event.getTaskId()
                            + ": "
                            + event.getErrorMessage()
            );
            return;
        }

        UUID taskUuid = parseId(event.getTaskId(), "TaskId inválido");
        UUID projectUuid = parseId(event.getProjectId(), "ProjectId inválido");

        if (event.getEmbeddingJson() == null || event.getEmbeddingJson().isBlank()) {
            throw new IllegalArgumentException("embeddingJson is required");
        }

        TaskSemanticEmbeddingEntity entity = embeddingRepository.findById(taskUuid)
                .orElseGet(TaskSemanticEmbeddingEntity::new);

        boolean isNew = entity.getTaskId() == null;

        entity.setTaskId(taskUuid);
        entity.setProjectId(projectUuid);
        entity.setEmbeddingText(truncate(event.getEmbeddingText(), 1000));
        entity.setEmbeddingJson(event.getEmbeddingJson());
        entity.setEmbeddingModel(truncate(event.getEmbeddingModel(), 100));

        if (isNew) {
            entity.setCreatedAt(LocalDateTime.now());
        }

        entity.setUpdatedAt(LocalDateTime.now());

        embeddingRepository.save(entity);
    }

    private UUID parseId(String value, String errorMessage) {
        try {
            if (value.contains("-")) {
                return UUID.fromString(value);
            }

            return hexToUuid(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private UUID hexToUuid(String hex) {
        return UUID.fromString(
                hex.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"
                )
        );
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
}
