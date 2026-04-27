package com.oraclejavabot.features.ai.controller;

import com.oraclejavabot.features.ai.dto.AiBacklogGenerationResponseDTO;
import com.oraclejavabot.features.ai.dto.AiTaskSuggestionResponseDTO;
import com.oraclejavabot.features.ai.service.AiBacklogGenerationService;
import com.oraclejavabot.features.ai.service.AiTaskSuggestionService;
import org.springframework.web.bind.annotation.*;

import com.oraclejavabot.features.ai.dto.AiTaskApprovalRequestDTO;
import com.oraclejavabot.features.ai.dto.AiTaskApprovalResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class AiController {

    private final AiBacklogGenerationService aiBacklogGenerationService;
    private final AiTaskSuggestionService aiTaskSuggestionService;

    public AiController(
            AiBacklogGenerationService aiBacklogGenerationService,
            AiTaskSuggestionService aiTaskSuggestionService
    ) {
        this.aiBacklogGenerationService = aiBacklogGenerationService;
        this.aiTaskSuggestionService = aiTaskSuggestionService;
    }

    @PostMapping("/{projectId}/ai/generate-backlog")
    public AiBacklogGenerationResponseDTO generateBacklog(@PathVariable String projectId) {
        return aiBacklogGenerationService.generateBacklog(projectId);
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
}