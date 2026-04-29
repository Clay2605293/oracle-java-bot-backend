package com.oraclejavabot.features.ai.dto;

public class AiTaskSuggestionClearResponseDTO {

    private String message;
    private String projectId;
    private Long deletedSuggestions;

    public AiTaskSuggestionClearResponseDTO() {}

    public AiTaskSuggestionClearResponseDTO(
            String message,
            String projectId,
            Long deletedSuggestions
    ) {
        this.message = message;
        this.projectId = projectId;
        this.deletedSuggestions = deletedSuggestions;
    }

    public String getMessage() {
        return message;
    }

    public String getProjectId() {
        return projectId;
    }

    public Long getDeletedSuggestions() {
        return deletedSuggestions;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setDeletedSuggestions(Long deletedSuggestions) {
        this.deletedSuggestions = deletedSuggestions;
    }
}