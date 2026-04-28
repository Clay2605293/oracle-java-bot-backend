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

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class AiController {

    private final AiBacklogGenerationService aiBacklogGenerationService;
    private final AiTaskSuggestionService aiTaskSuggestionService;
    private final AiDuplicateDetectionService aiDuplicateDetectionService;

    public AiController(
            AiBacklogGenerationService aiBacklogGenerationService,
            AiTaskSuggestionService aiTaskSuggestionService,
            AiDuplicateDetectionService aiDuplicateDetectionService
    ) {
        this.aiBacklogGenerationService = aiBacklogGenerationService;
        this.aiTaskSuggestionService = aiTaskSuggestionService;
        this.aiDuplicateDetectionService = aiDuplicateDetectionService;
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
}