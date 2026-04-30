package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.TaskEmbeddingBackfillResponseDTO;
import com.oraclejavabot.features.projects.repository.ProjectRepository;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.service.TaskService;
import com.oraclejavabot.messaging.event.AiTaskEmbeddingRequestEvent;
import com.oraclejavabot.messaging.producer.AiTaskEmbeddingProducer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskEmbeddingBackfillService {

    private static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-3-small";

    private final ProjectRepository projectRepository;
    private final TaskService taskService;
    private final AiTaskEmbeddingProducer taskEmbeddingProducer;

    public TaskEmbeddingBackfillService(
            ProjectRepository projectRepository,
            TaskService taskService,
            AiTaskEmbeddingProducer taskEmbeddingProducer
    ) {
        this.projectRepository = projectRepository;
        this.taskService = taskService;
        this.taskEmbeddingProducer = taskEmbeddingProducer;
    }

    public TaskEmbeddingBackfillResponseDTO backfillProjectTaskEmbeddings(String projectId) {
        UUID projectUuid = parseProjectId(projectId);
        String projectHex = uuidToHex(projectUuid);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        List<TaskResponseDTO> tasks = taskService.getTasksByProject(projectHex);

        int sentCount = 0;

        for (TaskResponseDTO task : tasks) {
            if (task.getTaskId() == null || task.getTitulo() == null || task.getTitulo().isBlank()) {
                continue;
            }

            try {
                AiTaskEmbeddingRequestEvent event = new AiTaskEmbeddingRequestEvent();

                event.setTaskId(task.getTaskId());
                event.setProjectId(projectHex);
                event.setTitulo(task.getTitulo());
                event.setDescripcion(task.getDescripcion());
                event.setEmbeddingModel(DEFAULT_EMBEDDING_MODEL);

                taskEmbeddingProducer.sendTaskEmbeddingRequest(event);
                sentCount++;

            } catch (Exception e) {
                System.err.println(
                        "⚠️ Could not send embedding request for task "
                                + task.getTaskId()
                                + ": "
                                + e.getMessage()
                );
            }
        }

        return new TaskEmbeddingBackfillResponseDTO(
                "Task embedding backfill started",
                projectHex,
                sentCount
        );
    }

    private UUID parseProjectId(String projectId) {
        return parseId(projectId, "ProjectId inválido");
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

    private String uuidToHex(UUID uuid) {
        return uuid.toString().replace("-", "").toUpperCase();
    }
}