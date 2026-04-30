package com.oraclejavabot.features.ai.dto;

public class TaskEmbeddingBackfillResponseDTO {

    private String message;
    private String projectId;
    private Integer tasksSent;

    public TaskEmbeddingBackfillResponseDTO() {}

    public TaskEmbeddingBackfillResponseDTO(
            String message,
            String projectId,
            Integer tasksSent
    ) {
        this.message = message;
        this.projectId = projectId;
        this.tasksSent = tasksSent;
    }

    public String getMessage() {
        return message;
    }

    public String getProjectId() {
        return projectId;
    }

    public Integer getTasksSent() {
        return tasksSent;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setTasksSent(Integer tasksSent) {
        this.tasksSent = tasksSent;
    }
}