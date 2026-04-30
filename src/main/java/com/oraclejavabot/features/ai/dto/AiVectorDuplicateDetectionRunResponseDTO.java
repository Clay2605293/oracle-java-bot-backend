package com.oraclejavabot.features.ai.dto;

public class AiVectorDuplicateDetectionRunResponseDTO {

    private String runId;
    private String projectId;
    private String status;
    private Double threshold;
    private Integer tasksAnalyzed;
    private String embeddingModel;
    private String detectionEngine;
    private String createdAt;
    private String completedAt;
    private String errorMessage;

    public AiVectorDuplicateDetectionRunResponseDTO() {}

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Integer getTasksAnalyzed() {
        return tasksAnalyzed;
    }

    public void setTasksAnalyzed(Integer tasksAnalyzed) {
        this.tasksAnalyzed = tasksAnalyzed;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public String getDetectionEngine() {
        return detectionEngine;
    }

    public void setDetectionEngine(String detectionEngine) {
        this.detectionEngine = detectionEngine;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}