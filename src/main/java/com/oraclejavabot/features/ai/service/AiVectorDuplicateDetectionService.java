package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionRequestDTO;
import com.oraclejavabot.features.ai.dto.AiVectorDuplicateDetectionLatestResponseDTO;
import com.oraclejavabot.features.ai.dto.AiVectorDuplicateDetectionResultResponseDTO;
import com.oraclejavabot.features.ai.dto.AiVectorDuplicateDetectionRunResponseDTO;
import com.oraclejavabot.features.ai.model.AiVectorDuplicateDetectionRunEntity;
import com.oraclejavabot.features.ai.model.AiVectorDuplicateResultEntity;
import com.oraclejavabot.features.ai.repository.AiVectorDuplicateDetectionRunRepository;
import com.oraclejavabot.features.ai.repository.AiVectorDuplicateResultRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.service.TaskService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AiVectorDuplicateDetectionService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";

    private static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-3-small";
    private static final String DETECTION_ENGINE = "ORACLE_VECTOR";

    private final AiVectorDuplicateDetectionRunRepository runRepository;
    private final AiVectorDuplicateResultRepository resultRepository;
    private final ProjectRepository projectRepository;
    private final TaskService taskService;

    @PersistenceContext
    private EntityManager entityManager;

    public AiVectorDuplicateDetectionService(
            AiVectorDuplicateDetectionRunRepository runRepository,
            AiVectorDuplicateResultRepository resultRepository,
            ProjectRepository projectRepository,
            TaskService taskService
    ) {
        this.runRepository = runRepository;
        this.resultRepository = resultRepository;
        this.projectRepository = projectRepository;
        this.taskService = taskService;
    }

    @Transactional
    public AiVectorDuplicateDetectionRunResponseDTO startVectorDuplicateDetection(
            String projectId,
            AiDuplicateDetectionRequestDTO request
    ) {
        UUID projectUuid = parseProjectId(projectId);

        if (!projectRepository.existsById(projectUuid)) {
            throw new IllegalArgumentException("Project not found");
        }

        Double threshold = resolveThreshold(request);
        String projectHex = uuidToHex(projectUuid);

        List<TaskResponseDTO> tasks = taskService.getTasksByProject(projectHex);

        if (tasks.size() < 2) {
            throw new IllegalArgumentException("At least 2 tasks are required to detect vector duplicates");
        }

        int embeddingsCount = countEmbeddingsByProject(projectUuid);

        if (embeddingsCount < 2) {
            throw new IllegalArgumentException(
                    "At least 2 task embeddings are required. Run task embedding backfill first."
            );
        }

        AiVectorDuplicateDetectionRunEntity run = new AiVectorDuplicateDetectionRunEntity();
        run.setProjectId(projectUuid);
        run.setStatus(STATUS_PENDING);
        run.setThreshold(threshold);
        run.setTasksAnalyzed(embeddingsCount);
        run.setEmbeddingModel(DEFAULT_EMBEDDING_MODEL);
        run.setDetectionEngine(DETECTION_ENGINE);
        run.setCreatedAt(LocalDateTime.now());

        AiVectorDuplicateDetectionRunEntity savedRun = runRepository.save(run);

        try {
            int savedResults = calculateAndPersistVectorResults(savedRun);

            savedRun.setStatus(STATUS_COMPLETED);
            savedRun.setTasksAnalyzed(embeddingsCount);
            savedRun.setCompletedAt(LocalDateTime.now());
            savedRun.setErrorMessage(null);

            runRepository.save(savedRun);

            System.out.println(
                    "✅ Oracle Vector duplicate detection completed. runId="
                            + uuidToHex(savedRun.getRunId())
                            + ", results="
                            + savedResults
            );

        } catch (Exception e) {
            savedRun.setStatus(STATUS_FAILED);
            savedRun.setCompletedAt(LocalDateTime.now());
            savedRun.setErrorMessage(truncate(e.getMessage(), 1000));
            runRepository.save(savedRun);

            throw e;
        }

        return mapRunToResponse(savedRun);
    }

    public List<AiVectorDuplicateDetectionRunResponseDTO> getRunsByProject(String projectId) {
        UUID projectUuid = parseProjectId(projectId);

        return runRepository.findByProjectIdOrderByCreatedAtDesc(projectUuid)
                .stream()
                .map(this::mapRunToResponse)
                .collect(Collectors.toList());
    }

    public List<AiVectorDuplicateDetectionResultResponseDTO> getResultsByRun(String runId) {
        UUID runUuid = parseId(runId, "RunId inválido");

        AiVectorDuplicateDetectionRunEntity run = runRepository.findById(runUuid)
                .orElseThrow(() -> new IllegalArgumentException("Vector duplicate detection run not found"));

        return resultRepository.findByRunId(run.getRunId())
                .stream()
                .map(this::mapResultToResponse)
                .collect(Collectors.toList());
    }

    public AiVectorDuplicateDetectionLatestResponseDTO getLatestByProject(String projectId) {
        UUID projectUuid = parseProjectId(projectId);

        AiVectorDuplicateDetectionRunEntity latestRun =
                runRepository.findTopByProjectIdOrderByCreatedAtDesc(projectUuid)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No vector duplicate detection runs found for project"
                        ));

        List<AiVectorDuplicateDetectionResultResponseDTO> results =
                resultRepository.findByRunId(latestRun.getRunId())
                        .stream()
                        .map(this::mapResultToResponse)
                        .collect(Collectors.toList());

        AiVectorDuplicateDetectionLatestResponseDTO response =
                new AiVectorDuplicateDetectionLatestResponseDTO();

        response.setRun(mapRunToResponse(latestRun));
        response.setResults(results);

        return response;
    }

    private int calculateAndPersistVectorResults(AiVectorDuplicateDetectionRunEntity run) {
        resultRepository.deleteByRunId(run.getRunId());

        String sql = """
                INSERT INTO CHATBOT_USER.AI_VECTOR_DUP_RESULT (
                    RUN_ID,
                    PROJECT_ID,
                    TASK_A_ID,
                    TASK_B_ID,
                    TASK_A_TITLE,
                    TASK_B_TITLE,
                    SIMILARITY_SCORE,
                    DISTANCE,
                    CREATED_AT
                )
                SELECT
                    :runId,
                    e1.PROJECT_ID,
                    e1.TASK_ID,
                    e2.TASK_ID,
                    SUBSTR(t1.TITULO, 1, 120),
                    SUBSTR(t2.TITULO, 1, 120),
                    ROUND(1 - VECTOR_DISTANCE(
                        e1.EMBEDDING,
                        e2.EMBEDDING,
                        COSINE
                    ), 6),
                    ROUND(VECTOR_DISTANCE(
                        e1.EMBEDDING,
                        e2.EMBEDDING,
                        COSINE
                    ), 6),
                    SYSTIMESTAMP
                FROM CHATBOT_USER.TASK_VECTOR_EMBEDDING e1
                JOIN CHATBOT_USER.TASK_VECTOR_EMBEDDING e2
                    ON e1.PROJECT_ID = e2.PROJECT_ID
                AND e1.TASK_ID < e2.TASK_ID
                JOIN CHATBOT_USER.TAREA t1
                    ON t1.TASK_ID = e1.TASK_ID
                JOIN CHATBOT_USER.TAREA t2
                    ON t2.TASK_ID = e2.TASK_ID
                WHERE e1.PROJECT_ID = :projectId
                AND e1.EMBEDDING_MODEL = :embeddingModel
                AND e2.EMBEDDING_MODEL = :embeddingModel
                AND (1 - VECTOR_DISTANCE(
                        e1.EMBEDDING,
                        e2.EMBEDDING,
                        COSINE
                )) >= :threshold
                """;

        return entityManager.createNativeQuery(sql)
                .setParameter("runId", run.getRunId())
                .setParameter("projectId", run.getProjectId())
                .setParameter("embeddingModel", run.getEmbeddingModel())
                .setParameter("threshold", run.getThreshold())
                .executeUpdate();
    }

    private int countEmbeddingsByProject(UUID projectUuid) {
        String sql = """
                SELECT COUNT(*)
                FROM CHATBOT_USER.TASK_VECTOR_EMBEDDING
                WHERE PROJECT_ID = :projectId
                AND EMBEDDING_MODEL = :embeddingModel
                """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("projectId", projectUuid)
                .setParameter("embeddingModel", DEFAULT_EMBEDDING_MODEL)
                .getSingleResult();

        if (result instanceof BigDecimal value) {
            return value.intValue();
        }

        if (result instanceof Number value) {
            return value.intValue();
        }

        return Integer.parseInt(result.toString());
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

    private AiVectorDuplicateDetectionRunResponseDTO mapRunToResponse(
            AiVectorDuplicateDetectionRunEntity run
    ) {
        AiVectorDuplicateDetectionRunResponseDTO dto =
                new AiVectorDuplicateDetectionRunResponseDTO();

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

    private AiVectorDuplicateDetectionResultResponseDTO mapResultToResponse(
            AiVectorDuplicateResultEntity result
    ) {
        AiVectorDuplicateDetectionResultResponseDTO dto =
                new AiVectorDuplicateDetectionResultResponseDTO();

        dto.setResultId(uuidToHex(result.getResultId()));
        dto.setRunId(uuidToHex(result.getRunId()));
        dto.setProjectId(uuidToHex(result.getProjectId()));
        dto.setTaskAId(uuidToHex(result.getTaskAId()));
        dto.setTaskBId(uuidToHex(result.getTaskBId()));
        dto.setTaskATitle(result.getTaskATitle());
        dto.setTaskBTitle(result.getTaskBTitle());
        dto.setSimilarityScore(result.getSimilarityScore());
        dto.setDistance(result.getDistance());

        if (result.getCreatedAt() != null) {
            dto.setCreatedAt(result.getCreatedAt().toString());
        }

        return dto;
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