package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionLatestResponseDTO;
import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionRequestDTO;
import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionResultResponseDTO;
import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionRunResponseDTO;
import com.oraclejavabot.features.ai.model.AiTaskDuplicateDetectionRunEntity;
import com.oraclejavabot.features.ai.model.AiTaskDuplicateResultEntity;
import com.oraclejavabot.features.ai.repository.AiTaskDuplicateDetectionRunRepository;
import com.oraclejavabot.features.ai.repository.AiTaskDuplicateResultRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.service.TaskService;
import com.oraclejavabot.messaging.event.AiDuplicateDetectionRequestEvent;
import com.oraclejavabot.messaging.event.AiDuplicateDetectionResponseEvent;
import com.oraclejavabot.messaging.producer.AiDuplicateDetectionProducer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AiDuplicateDetectionService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";

    private final AiTaskDuplicateDetectionRunRepository runRepository;
    private final AiTaskDuplicateResultRepository resultRepository;
    private final ProjectRepository projectRepository;
    private final TaskService taskService;
    private final AiDuplicateDetectionProducer producer;

    public AiDuplicateDetectionService(
            AiTaskDuplicateDetectionRunRepository runRepository,
            AiTaskDuplicateResultRepository resultRepository,
            ProjectRepository projectRepository,
            TaskService taskService,
            AiDuplicateDetectionProducer producer
    ) {
        this.runRepository = runRepository;
        this.resultRepository = resultRepository;
        this.projectRepository = projectRepository;
        this.taskService = taskService;
        this.producer = producer;
    }

    public AiDuplicateDetectionRunResponseDTO startDuplicateDetection(
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
            throw new IllegalArgumentException("At least 2 tasks are required to detect duplicates");
        }

        AiTaskDuplicateDetectionRunEntity run = new AiTaskDuplicateDetectionRunEntity();
        run.setProjectId(projectUuid);
        run.setStatus(STATUS_PENDING);
        run.setThreshold(threshold);
        run.setTasksAnalyzed(tasks.size());
        run.setCreatedAt(LocalDateTime.now());

        AiTaskDuplicateDetectionRunEntity savedRun = runRepository.save(run);

        AiDuplicateDetectionRequestEvent event = buildRequestEvent(savedRun, tasks);
        producer.sendDuplicateDetectionRequest(event);

        return mapRunToResponse(savedRun);
    }

    public List<AiDuplicateDetectionRunResponseDTO> getRunsByProject(String projectId) {
        UUID projectUuid = parseProjectId(projectId);

        return runRepository.findByProjectIdOrderByCreatedAtDesc(projectUuid)
                .stream()
                .map(this::mapRunToResponse)
                .collect(Collectors.toList());
    }

    public List<AiDuplicateDetectionResultResponseDTO> getResultsByRun(String runId) {
        UUID runUuid = parseId(runId, "RunId inválido");

        AiTaskDuplicateDetectionRunEntity run = runRepository.findById(runUuid)
                .orElseThrow(() -> new IllegalArgumentException("Duplicate detection run not found"));

        return resultRepository.findByRunId(run.getRunId())
                .stream()
                .map(this::mapResultToResponse)
                .collect(Collectors.toList());
    }

    public AiDuplicateDetectionLatestResponseDTO getLatestByProject(String projectId) {
        UUID projectUuid = parseProjectId(projectId);

        AiTaskDuplicateDetectionRunEntity latestRun =
                runRepository.findFirstByProjectIdOrderByCreatedAtDesc(projectUuid)
                        .orElseThrow(() -> new IllegalArgumentException("No duplicate detection runs found for project"));

        List<AiDuplicateDetectionResultResponseDTO> results =
                resultRepository.findByRunId(latestRun.getRunId())
                        .stream()
                        .map(this::mapResultToResponse)
                        .collect(Collectors.toList());

        AiDuplicateDetectionLatestResponseDTO response = new AiDuplicateDetectionLatestResponseDTO();
        response.setRun(mapRunToResponse(latestRun));
        response.setResults(results);

        return response;
    }

    @Transactional
    public int saveResultsFromAiResponse(AiDuplicateDetectionResponseEvent event) {

        if (event == null || event.getRunId() == null || event.getRunId().isBlank()) {
            throw new IllegalArgumentException("Invalid AI duplicate detection response: runId is required");
        }

        UUID runUuid = parseId(event.getRunId(), "RunId inválido");

        AiTaskDuplicateDetectionRunEntity run = runRepository.findById(runUuid)
                .orElseThrow(() -> new IllegalArgumentException("Duplicate detection run not found"));

        String responseStatus = event.getStatus();

        if (STATUS_FAILED.equalsIgnoreCase(responseStatus)) {
            run.setStatus(STATUS_FAILED);
            run.setCompletedAt(LocalDateTime.now());
            run.setErrorMessage(truncate(event.getErrorMessage(), 1000));
            runRepository.save(run);
            return 0;
        }

        resultRepository.deleteByRunId(run.getRunId());

        int savedCount = 0;

        if (event.getDuplicates() != null) {
            List<AiTaskDuplicateResultEntity> resultsToSave = new ArrayList<>();

            for (AiDuplicateDetectionResponseEvent.Duplicate duplicate : event.getDuplicates()) {

                if (duplicate.getTaskAId() == null || duplicate.getTaskBId() == null) {
                    continue;
                }

                UUID taskAId = parseId(duplicate.getTaskAId(), "TaskAId inválido");
                UUID taskBId = parseId(duplicate.getTaskBId(), "TaskBId inválido");

                if (taskAId.equals(taskBId)) {
                    continue;
                }

                AiTaskDuplicateResultEntity result = new AiTaskDuplicateResultEntity();

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

    private AiDuplicateDetectionRequestEvent buildRequestEvent(
            AiTaskDuplicateDetectionRunEntity run,
            List<TaskResponseDTO> tasks
    ) {
        AiDuplicateDetectionRequestEvent event = new AiDuplicateDetectionRequestEvent();

        event.setRunId(uuidToHex(run.getRunId()));
        event.setProjectId(uuidToHex(run.getProjectId()));
        event.setThreshold(run.getThreshold());

        List<AiDuplicateDetectionRequestEvent.Task> eventTasks = tasks.stream()
                .map(task -> {
                    AiDuplicateDetectionRequestEvent.Task eventTask =
                            new AiDuplicateDetectionRequestEvent.Task();

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

    private AiDuplicateDetectionRunResponseDTO mapRunToResponse(
            AiTaskDuplicateDetectionRunEntity run
    ) {
        AiDuplicateDetectionRunResponseDTO dto = new AiDuplicateDetectionRunResponseDTO();

        dto.setRunId(uuidToHex(run.getRunId()));
        dto.setProjectId(uuidToHex(run.getProjectId()));
        dto.setStatus(run.getStatus());
        dto.setThreshold(run.getThreshold());
        dto.setTasksAnalyzed(run.getTasksAnalyzed());

        if (run.getCreatedAt() != null) {
            dto.setCreatedAt(run.getCreatedAt().toString());
        }

        if (run.getCompletedAt() != null) {
            dto.setCompletedAt(run.getCompletedAt().toString());
        }

        dto.setErrorMessage(run.getErrorMessage());

        return dto;
    }

    private AiDuplicateDetectionResultResponseDTO mapResultToResponse(
            AiTaskDuplicateResultEntity result
    ) {
        AiDuplicateDetectionResultResponseDTO dto =
                new AiDuplicateDetectionResultResponseDTO();

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