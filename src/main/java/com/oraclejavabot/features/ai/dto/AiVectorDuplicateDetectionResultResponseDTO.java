package com.oraclejavabot.features.ai.dto;

public class AiVectorDuplicateDetectionResultResponseDTO {

    private String resultId;
    private String runId;
    private String projectId;

    private String taskAId;
    private String taskBId;

    private String taskATitle;
    private String taskBTitle;

    private Double similarityScore;
    private Double distance;

    private String createdAt;

    public AiVectorDuplicateDetectionResultResponseDTO() {}

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

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

    public String getTaskAId() {
        return taskAId;
    }

    public void setTaskAId(String taskAId) {
        this.taskAId = taskAId;
    }

    public String getTaskBId() {
        return taskBId;
    }

    public void setTaskBId(String taskBId) {
        this.taskBId = taskBId;
    }

    public String getTaskATitle() {
        return taskATitle;
    }

    public void setTaskATitle(String taskATitle) {
        this.taskATitle = taskATitle;
    }

    public String getTaskBTitle() {
        return taskBTitle;
    }

    public void setTaskBTitle(String taskBTitle) {
        this.taskBTitle = taskBTitle;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}