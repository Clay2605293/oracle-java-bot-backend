package com.oraclejavabot.features.ai.controller;

import com.oraclejavabot.features.ai.dto.AiBacklogGenerationRequestDTO;
import com.oraclejavabot.features.ai.dto.AiBacklogGenerationResponseDTO;
import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionLatestResponseDTO;
import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionRequestDTO;
import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionResultResponseDTO;
import com.oraclejavabot.features.ai.dto.AiDuplicateDetectionRunResponseDTO;
import com.oraclejavabot.features.ai.dto.AiTaskApprovalRequestDTO;
import com.oraclejavabot.features.ai.dto.AiTaskApprovalResponseDTO;
import com.oraclejavabot.features.ai.dto.AiTaskSuggestionResponseDTO;
import com.oraclejavabot.features.ai.service.AiBacklogGenerationService;
import com.oraclejavabot.features.ai.service.AiDuplicateDetectionService;
import com.oraclejavabot.features.ai.service.AiTaskSuggestionService;
import org.springframework.web.bind.annotation.*;
import com.oraclejavabot.features.ai.dto.AiTaskSuggestionClearResponseDTO;

import com.oraclejavabot.features.ai.dto.AiSemanticDuplicateDetectionLatestResponseDTO;
import com.oraclejavabot.features.ai.dto.AiSemanticDuplicateDetectionResultResponseDTO;
import com.oraclejavabot.features.ai.dto.AiSemanticDuplicateDetectionRunResponseDTO;
import com.oraclejavabot.features.ai.service.AiSemanticDuplicateDetectionService;

import com.oraclejavabot.features.ai.dto.TaskEmbeddingBackfillResponseDTO;
import com.oraclejavabot.features.ai.service.TaskEmbeddingBackfillService;

import com.oraclejavabot.features.ai.dto.AiVectorDuplicateDetectionLatestResponseDTO;
import com.oraclejavabot.features.ai.dto.AiVectorDuplicateDetectionResultResponseDTO;
import com.oraclejavabot.features.ai.dto.AiVectorDuplicateDetectionRunResponseDTO;
import com.oraclejavabot.features.ai.service.AiVectorDuplicateDetectionService;

import com.oraclejavabot.features.ai.dto.TaskVectorEmbeddingBackfillResponseDTO;
import com.oraclejavabot.features.ai.service.TaskVectorEmbeddingBackfillService;

import com.oraclejavabot.features.ai.dto.TaskEmbeddingStatusResponseDTO;
import com.oraclejavabot.features.ai.service.TaskEmbeddingStatusService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class AiController {

    private final AiBacklogGenerationService aiBacklogGenerationService;
    private final AiTaskSuggestionService aiTaskSuggestionService;
    private final AiDuplicateDetectionService aiDuplicateDetectionService;
    private final AiSemanticDuplicateDetectionService aiSemanticDuplicateDetectionService;
    private final TaskEmbeddingBackfillService taskEmbeddingBackfillService;
    private final AiVectorDuplicateDetectionService aiVectorDuplicateDetectionService;
    private final TaskVectorEmbeddingBackfillService taskVectorEmbeddingBackfillService;
    private final TaskEmbeddingStatusService taskEmbeddingStatusService;

    public AiController(
            AiBacklogGenerationService aiBacklogGenerationService,
            AiTaskSuggestionService aiTaskSuggestionService,
            AiDuplicateDetectionService aiDuplicateDetectionService,
            AiSemanticDuplicateDetectionService aiSemanticDuplicateDetectionService,
            TaskEmbeddingBackfillService taskEmbeddingBackfillService,
            AiVectorDuplicateDetectionService aiVectorDuplicateDetectionService,
            TaskVectorEmbeddingBackfillService taskVectorEmbeddingBackfillService,
            TaskEmbeddingStatusService taskEmbeddingStatusService
    ) {
        this.aiBacklogGenerationService = aiBacklogGenerationService;
        this.aiTaskSuggestionService = aiTaskSuggestionService;
        this.aiDuplicateDetectionService = aiDuplicateDetectionService;
        this.aiSemanticDuplicateDetectionService = aiSemanticDuplicateDetectionService;
        this.taskEmbeddingBackfillService = taskEmbeddingBackfillService;
        this.aiVectorDuplicateDetectionService = aiVectorDuplicateDetectionService;
        this.taskVectorEmbeddingBackfillService = taskVectorEmbeddingBackfillService;
        this.taskEmbeddingStatusService = taskEmbeddingStatusService;
    }

    @PostMapping("/{projectId}/ai/generate-backlog")
    public AiBacklogGenerationResponseDTO generateBacklog(
            @PathVariable String projectId,
            @RequestBody AiBacklogGenerationRequestDTO request
    ) {
        return aiBacklogGenerationService.generateBacklog(projectId, request);
    }

    @GetMapping("/{projectId}/ai/suggestions")
    public List<AiTaskSuggestionResponseDTO> getSuggestions(
            @PathVariable String projectId,
            @RequestParam(value = "status", required = false) String status
    ) {
        if (status == null || status.isBlank()) {
            return aiTaskSuggestionService.getSuggestionsByProject(projectId);
        }

        if (status.equalsIgnoreCase("PENDING")) {
            return aiTaskSuggestionService.getPendingSuggestionsByProject(projectId);
        }

        throw new IllegalArgumentException("Invalid status filter: " + status + ". Allowed value: PENDING");
    }

    @PatchMapping("/ai/suggestions/{aiTaskId}/reject")
    public AiTaskSuggestionResponseDTO rejectSuggestion(@PathVariable String aiTaskId) {
        return aiTaskSuggestionService.rejectSuggestion(aiTaskId);
    }

    @PostMapping("/ai/suggestions/{aiTaskId}/approve")
    public AiTaskApprovalResponseDTO approveSuggestion(
            @PathVariable String aiTaskId,
            @RequestBody AiTaskApprovalRequestDTO request
    ) {
        return aiTaskSuggestionService.approveSuggestion(aiTaskId, request);
    }

    @PostMapping("/{projectId}/ai/duplicate-detection")
    public AiDuplicateDetectionRunResponseDTO startDuplicateDetection(
            @PathVariable String projectId,
            @RequestBody(required = false) AiDuplicateDetectionRequestDTO request
    ) {
        return aiDuplicateDetectionService.startDuplicateDetection(projectId, request);
    }

    @GetMapping("/{projectId}/ai/duplicate-detection/runs")
    public List<AiDuplicateDetectionRunResponseDTO> getDuplicateDetectionRuns(
            @PathVariable String projectId
    ) {
        return aiDuplicateDetectionService.getRunsByProject(projectId);
    }

    @GetMapping("/{projectId}/ai/duplicate-detection/runs/{runId}/results")
    public List<AiDuplicateDetectionResultResponseDTO> getDuplicateDetectionResults(
            @PathVariable String projectId,
            @PathVariable String runId
    ) {
        return aiDuplicateDetectionService.getResultsByRun(runId);
    }

    @GetMapping("/{projectId}/ai/duplicate-detection/latest")
    public AiDuplicateDetectionLatestResponseDTO getLatestDuplicateDetection(
            @PathVariable String projectId
    ) {
        return aiDuplicateDetectionService.getLatestByProject(projectId);
    }

    @DeleteMapping("/{projectId}/ai/suggestions")
    public AiTaskSuggestionClearResponseDTO clearSuggestionsByProject(
            @PathVariable String projectId
    ) {
        return aiTaskSuggestionService.clearSuggestionsByProject(projectId);
    }

    @PostMapping("/{projectId}/ai/duplicate-detection/semantic")
public AiSemanticDuplicateDetectionRunResponseDTO startSemanticDuplicateDetection(
        @PathVariable String projectId,
        @RequestBody(required = false) AiDuplicateDetectionRequestDTO request
) {
    return aiSemanticDuplicateDetectionService.startSemanticDuplicateDetection(projectId, request);
}

    @GetMapping("/{projectId}/ai/duplicate-detection/semantic/runs")
    public List<AiSemanticDuplicateDetectionRunResponseDTO> getSemanticDuplicateDetectionRuns(
            @PathVariable String projectId
    ) {
        return aiSemanticDuplicateDetectionService.getRunsByProject(projectId);
    }

    @GetMapping("/{projectId}/ai/duplicate-detection/semantic/runs/{runId}/results")
    public List<AiSemanticDuplicateDetectionResultResponseDTO> getSemanticDuplicateDetectionResults(
            @PathVariable String projectId,
            @PathVariable String runId
    ) {
        return aiSemanticDuplicateDetectionService.getResultsByRun(runId);
    }

    @GetMapping("/{projectId}/ai/duplicate-detection/semantic/latest")
    public AiSemanticDuplicateDetectionLatestResponseDTO getLatestSemanticDuplicateDetection(
            @PathVariable String projectId
    ) {
        return aiSemanticDuplicateDetectionService.getLatestByProject(projectId);
    }

    @PostMapping("/{projectId}/ai/task-embeddings/backfill")
        public TaskEmbeddingBackfillResponseDTO backfillProjectTaskEmbeddings(
                @PathVariable String projectId
        ) {
            return taskEmbeddingBackfillService.backfillProjectTaskEmbeddings(projectId);
        }

    @PostMapping("/{projectId}/ai/duplicate-detection/vector")
    public AiVectorDuplicateDetectionRunResponseDTO startVectorDuplicateDetection(
            @PathVariable String projectId,
            @RequestBody(required = false) AiDuplicateDetectionRequestDTO request
    ) {
        return aiVectorDuplicateDetectionService.startVectorDuplicateDetection(projectId, request);
    }

    @GetMapping("/{projectId}/ai/duplicate-detection/vector/runs")
    public List<AiVectorDuplicateDetectionRunResponseDTO> getVectorDuplicateDetectionRuns(
            @PathVariable String projectId
    ) {
        return aiVectorDuplicateDetectionService.getRunsByProject(projectId);
    }

    @GetMapping("/{projectId}/ai/duplicate-detection/vector/runs/{runId}/results")
    public List<AiVectorDuplicateDetectionResultResponseDTO> getVectorDuplicateDetectionResults(
            @PathVariable String projectId,
            @PathVariable String runId
    ) {
        return aiVectorDuplicateDetectionService.getResultsByRun(runId);
    }

    @GetMapping("/{projectId}/ai/duplicate-detection/vector/latest")
    public AiVectorDuplicateDetectionLatestResponseDTO getLatestVectorDuplicateDetection(
            @PathVariable String projectId
    ) {
        return aiVectorDuplicateDetectionService.getLatestByProject(projectId);
    }

    @PostMapping("/{projectId}/ai/task-vector-embeddings/backfill")
    public TaskVectorEmbeddingBackfillResponseDTO backfillProjectTaskVectorEmbeddings(
            @PathVariable String projectId
    ) {
        return taskVectorEmbeddingBackfillService.backfillProjectTaskVectorEmbeddings(projectId);
    }

    @GetMapping("/{projectId}/ai/task-embeddings/status")
    public TaskEmbeddingStatusResponseDTO getTaskEmbeddingStatus(
            @PathVariable String projectId
    ) {
        return taskEmbeddingStatusService.getStatus(projectId);
    }

}