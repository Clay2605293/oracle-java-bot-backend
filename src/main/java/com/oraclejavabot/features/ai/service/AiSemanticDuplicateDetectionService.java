package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionRequestDTO;
import com.oraclejavabot.features.ai.dto.AiSemanticDuplicateDetectionLatestResponseDTO;
import com.oraclejavabot.features.ai.dto.AiSemanticDuplicateDetectionResultResponseDTO;
import com.oraclejavabot.features.ai.dto.AiSemanticDuplicateDetectionRunResponseDTO;
import com.oraclejavabot.features.ai.model.AiSemanticDuplicateDetectionRunEntity;
import com.oraclejavabot.features.ai.model.AiSemanticDuplicateResultEntity;
import com.oraclejavabot.features.ai.model.TaskSemanticEmbeddingEntity;
import com.oraclejavabot.features.ai.repository.AiSemanticDuplicateDetectionRunRepository;
import com.oraclejavabot.features.ai.repository.AiSemanticDuplicateResultRepository;
import com.oraclejavabot.features.ai.repository.TaskSemanticEmbeddingRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.service.TaskService;
import com.oraclejavabot.messaging.event.AiSemanticDuplicateDetectionRequestEvent;
import com.oraclejavabot.messaging.event.AiSemanticDuplicateDetectionResponseEvent;
import com.oraclejavabot.messaging.producer.AiSemanticDuplicateDetectionProducer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AiSemanticDuplicateDetectionService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";

    private static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-3-small";
    private static final String DETECTION_ENGINE = "PYTHON_EMBEDDINGS";

    private final AiSemanticDuplicateDetectionRunRepository runRepository;
    private final AiSemanticDuplicateResultRepository resultRepository;
    private final TaskSemanticEmbeddingRepository embeddingRepository;
    private final ProjectRepository projectRepository;
    private final TaskService taskService;
    private final AiSemanticDuplicateDetectionProducer producer;

    public AiSemanticDuplicateDetectionService(
            AiSemanticDuplicateDetectionRunRepository runRepository,
            AiSemanticDuplicateResultRepository resultRepository,
            TaskSemanticEmbeddingRepository embeddingRepository,
            ProjectRepository projectRepository,
            TaskService taskService,
            AiSemanticDuplicateDetectionProducer producer
    ) {
        this.runRepository = runRepository;
        this.resultRepository = resultRepository;
        this.embeddingRepository = embeddingRepository;
        this.projectRepository = projectRepository;
        this.taskService = taskService;
        this.producer = producer;
    }

    public AiSemanticDuplicateDetectionRunResponseDTO startSemanticDuplicateDetection(
            String projectId,
            AiDuplicateDetectionRequestDTO request
    ) {
        UUID projectUuid = parseProjectId(projectId);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found");
        }

        Double threshold = resolveThreshold(request);

        List<TaskResponseDTO> tasks = taskService.getTasksByProject(uuidToHex(projectUuid));

        if (tasks.size() < 2) {
            throw new IllegalArgumentException("At least 2 tasks are required to detect semantic duplicates");
        }

        AiSemanticDuplicateDetectionRunEntity run = new AiSemanticDuplicateDetectionRunEntity();
        run.setProjectId(projectUuid);
        run.setStatus(STATUS_PENDING);
        run.setThreshold(threshold);
        run.setTasksAnalyzed(tasks.size());
        run.setEmbeddingModel(DEFAULT_EMBEDDING_MODEL);
        run.setDetectionEngine(DETECTION_ENGINE);
        run.setCreatedAt(LocalDateTime.now());

        AiSemanticDuplicateDetectionRunEntity savedRun = runRepository.save(run);

        AiSemanticDuplicateDetectionRequestEvent event = buildRequestEvent(savedRun, tasks);
        producer.sendSemanticDuplicateDetectionRequest(event);

        return mapRunToResponse(savedRun);
    }

    public List<AiSemanticDuplicateDetectionRunResponseDTO> getRunsByProject(String projectId) {
        UUID projectUuid = parseProjectId(projectId);

        return runRepository.findByProjectIdOrderByCreatedAtDesc(projectUuid)
                .stream()
                .map(this::mapRunToResponse)
                .collect(Collectors.toList());
    }

    public List<AiSemanticDuplicateDetectionResultResponseDTO> getResultsByRun(String runId) {
        UUID runUuid = parseId(runId, "RunId inválido");

        AiSemanticDuplicateDetectionRunEntity run = runRepository.findById(runUuid)
                .orElseThrow(() -> new IllegalArgumentException("Semantic duplicate detection run not found"));

        return resultRepository.findByRunId(run.getRunId())
                .stream()
                .map(this::mapResultToResponse)
                .collect(Collectors.toList());
    }

    public AiSemanticDuplicateDetectionLatestResponseDTO getLatestByProject(String projectId) {
        UUID projectUuid = parseProjectId(projectId);

        AiSemanticDuplicateDetectionRunEntity latestRun =
                runRepository.findFirstByProjectIdOrderByCreatedAtDesc(projectUuid)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No semantic duplicate detection runs found for project"
                        ));

        List<AiSemanticDuplicateDetectionResultResponseDTO> results =
                resultRepository.findByRunId(latestRun.getRunId())
                        .stream()
                        .map(this::mapResultToResponse)
                        .collect(Collectors.toList());

        AiSemanticDuplicateDetectionLatestResponseDTO response =
                new AiSemanticDuplicateDetectionLatestResponseDTO();

        response.setRun(mapRunToResponse(latestRun));
        response.setResults(results);

        return response;
    }

    @Transactional
    public int saveResultsFromAiResponse(AiSemanticDuplicateDetectionResponseEvent event) {
        if (event == null || event.getRunId() == null || event.getRunId().isBlank()) {
            throw new IllegalArgumentException("Invalid AI semantic duplicate detection response: runId is required");
        }

        UUID runUuid = parseId(event.getRunId(), "RunId inválido");

        AiSemanticDuplicateDetectionRunEntity run = runRepository.findById(runUuid)
                .orElseThrow(() -> new IllegalArgumentException("Semantic duplicate detection run not found"));

        String responseStatus = event.getStatus();

        if (STATUS_FAILED.equalsIgnoreCase(responseStatus)) {
            run.setStatus(STATUS_FAILED);
            run.setCompletedAt(LocalDateTime.now());
            run.setErrorMessage(truncate(event.getErrorMessage(), 1000));
            runRepository.save(run);
            return 0;
        }

        if (event.getEmbeddingModel() != null && !event.getEmbeddingModel().isBlank()) {
            run.setEmbeddingModel(truncate(event.getEmbeddingModel(), 100));
        }

        saveEmbeddings(event, run);

        resultRepository.deleteByRunId(run.getRunId());

        int savedCount = 0;

        if (event.getDuplicates() != null) {
            List<AiSemanticDuplicateResultEntity> resultsToSave = new ArrayList<>();

            for (AiSemanticDuplicateDetectionResponseEvent.Duplicate duplicate : event.getDuplicates()) {
                if (duplicate.getTaskAId() == null || duplicate.getTaskBId() == null) {
                    continue;
                }

                UUID taskAId = parseId(duplicate.getTaskAId(), "TaskAId inválido");
                UUID taskBId = parseId(duplicate.getTaskBId(), "TaskBId inválido");

                if (taskAId.equals(taskBId)) {
                    continue;
                }

                AiSemanticDuplicateResultEntity result = new AiSemanticDuplicateResultEntity();

                result.setRunId(run.getRunId());
                result.setProjectId(run.getProjectId());
                result.setTaskAId(taskAId);
                result.setTaskBId(taskBId);
                result.setTaskATitle(safeTitle(duplicate.getTaskATitle(), duplicate.getTaskAId()));
                result.setTaskBTitle(safeTitle(duplicate.getTaskBTitle(), duplicate.getTaskBId()));
                result.setSimilarityScore(safeScore(duplicate.getSimilarityScore()));
                result.setReason(truncate(duplicate.getReason(), 1000));
                result.setCreatedAt(LocalDateTime.now());

                resultsToSave.add(result);
            }

            resultRepository.saveAll(resultsToSave);
            savedCount = resultsToSave.size();
        }

        run.setStatus(STATUS_COMPLETED);
        run.setCompletedAt(LocalDateTime.now());
        run.setErrorMessage(null);
        runRepository.save(run);

        return savedCount;
    }

    private void saveEmbeddings(
            AiSemanticDuplicateDetectionResponseEvent event,
            AiSemanticDuplicateDetectionRunEntity run
    ) {
        if (event.getEmbeddings() == null) {
            return;
        }

        String embeddingModel = run.getEmbeddingModel() != null
                ? run.getEmbeddingModel()
                : DEFAULT_EMBEDDING_MODEL;

        for (AiSemanticDuplicateDetectionResponseEvent.Embedding embedding : event.getEmbeddings()) {
            if (embedding.getTaskId() == null || embedding.getTaskId().isBlank()) {
                continue;
            }

            UUID taskUuid = parseId(embedding.getTaskId(), "TaskId inválido");

            TaskSemanticEmbeddingEntity entity = embeddingRepository.findById(taskUuid)
                    .orElseGet(TaskSemanticEmbeddingEntity::new);

            boolean isNew = entity.getTaskId() == null;

            entity.setTaskId(taskUuid);
            entity.setProjectId(run.getProjectId());
            entity.setEmbeddingText(truncate(embedding.getEmbeddingText(), 1000));
            entity.setEmbeddingJson(embedding.getEmbeddingJson());
            entity.setEmbeddingModel(truncate(embeddingModel, 100));

            if (isNew) {
                entity.setCreatedAt(LocalDateTime.now());
            }

            entity.setUpdatedAt(LocalDateTime.now());

            embeddingRepository.save(entity);
        }
    }

    private AiSemanticDuplicateDetectionRequestEvent buildRequestEvent(
            AiSemanticDuplicateDetectionRunEntity run,
            List<TaskResponseDTO> tasks
    ) {
        AiSemanticDuplicateDetectionRequestEvent event = new AiSemanticDuplicateDetectionRequestEvent();

        event.setRunId(uuidToHex(run.getRunId()));
        event.setProjectId(uuidToHex(run.getProjectId()));
        event.setThreshold(run.getThreshold());
        event.setEmbeddingModel(run.getEmbeddingModel());

        List<AiSemanticDuplicateDetectionRequestEvent.Task> eventTasks = tasks.stream()
                .map(task -> {
                    AiSemanticDuplicateDetectionRequestEvent.Task eventTask =
                            new AiSemanticDuplicateDetectionRequestEvent.Task();

                    eventTask.setTaskId(task.getTaskId());
                    eventTask.setTitulo(task.getTitulo());
                    eventTask.setDescripcion(task.getDescripcion());

                    return eventTask;
                })
                .collect(Collectors.toList());

        event.setTasks(eventTasks);

        return event;
    }

    private Double resolveThreshold(AiDuplicateDetectionRequestDTO request) {
        if (request == null || request.getThreshold() == null) {
            return 0.80;
        }

        Double threshold = request.getThreshold();

        if (threshold < 0 || threshold > 1) {
            throw new IllegalArgumentException("threshold must be between 0 and 1");
        }

        return threshold;
    }

    private AiSemanticDuplicateDetectionRunResponseDTO mapRunToResponse(
            AiSemanticDuplicateDetectionRunEntity run
    ) {
        AiSemanticDuplicateDetectionRunResponseDTO dto =
                new AiSemanticDuplicateDetectionRunResponseDTO();

        dto.setRunId(uuidToHex(run.getRunId()));
        dto.setProjectId(uuidToHex(run.getProjectId()));
        dto.setStatus(run.getStatus());
        dto.setThreshold(run.getThreshold());
        dto.setTasksAnalyzed(run.getTasksAnalyzed());
        dto.setEmbeddingModel(run.getEmbeddingModel());
        dto.setDetectionEngine(run.getDetectionEngine());

        if (run.getCreatedAt() != null) {
            dto.setCreatedAt(run.getCreatedAt().toString());
        }

        if (run.getCompletedAt() != null) {
            dto.setCompletedAt(run.getCompletedAt().toString());
        }

        dto.setErrorMessage(run.getErrorMessage());

        return dto;
    }

    private AiSemanticDuplicateDetectionResultResponseDTO mapResultToResponse(
            AiSemanticDuplicateResultEntity result
    ) {
        AiSemanticDuplicateDetectionResultResponseDTO dto =
                new AiSemanticDuplicateDetectionResultResponseDTO();

        dto.setResultId(uuidToHex(result.getResultId()));
        dto.setRunId(uuidToHex(result.getRunId()));
        dto.setProjectId(uuidToHex(result.getProjectId()));
        dto.setTaskAId(uuidToHex(result.getTaskAId()));
        dto.setTaskBId(uuidToHex(result.getTaskBId()));
        dto.setTaskATitle(result.getTaskATitle());
        dto.setTaskBTitle(result.getTaskBTitle());
        dto.setSimilarityScore(result.getSimilarityScore());
        dto.setReason(result.getReason());

        if (result.getCreatedAt() != null) {
            dto.setCreatedAt(result.getCreatedAt().toString());
        }

        return dto;
    }

    private String safeTitle(String title, String taskId) {
        if (title == null || title.isBlank()) {
            return truncate("Task " + taskId, 120);
        }

        return truncate(title, 120);
    }

    private Double safeScore(Double score) {
        if (score == null) {
            return 0.0;
        }

        if (score < 0) {
            return 0.0;
        }

        if (score > 1) {
            return 1.0;
        }

        return score;
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
