package com.oraclejavabot.features.ai.dto;

import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;

public class AiTaskApprovalResponseDTO {

    private AiTaskSuggestionResponseDTO suggestion;
    private TaskResponseDTO createdTask;

    public AiTaskApprovalResponseDTO() {}

    public AiTaskApprovalResponseDTO(
            AiTaskSuggestionResponseDTO suggestion,
            TaskResponseDTO createdTask
    ) {
        this.suggestion = suggestion;
        this.createdTask = createdTask;
    }

    public AiTaskSuggestionResponseDTO getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(AiTaskSuggestionResponseDTO suggestion) {
        this.suggestion = suggestion;
    }

    public TaskResponseDTO getCreatedTask() {
        return createdTask;
    }

    public void setCreatedTask(TaskResponseDTO createdTask) {
        this.createdTask = createdTask;
    }
}
