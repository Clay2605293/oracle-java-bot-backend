package com.oraclejavabot.features.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AI_VECTOR_DUP_RESULT")
public class AiVectorDuplicateResultEntity {

    @Id
    @GeneratedValue
    @Column(name = "RESULT_ID")
    private UUID resultId;

    @Column(name = "RUN_ID", nullable = false)
    private UUID runId;

    @Column(name = "PROJECT_ID", nullable = false)
    private UUID projectId;

    @Column(name = "TASK_A_ID", nullable = false)
    private UUID taskAId;

    @Column(name = "TASK_B_ID", nullable = false)
    private UUID taskBId;

    @Column(name = "TASK_A_TITLE", nullable = false)
    private String taskATitle;

    @Column(name = "TASK_B_TITLE", nullable = false)
    private String taskBTitle;

    @Column(name = "SIMILARITY_SCORE", nullable = false)
    private Double similarityScore;

    @Column(name = "DISTANCE", nullable = false)
    private Double distance;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public AiVectorDuplicateResultEntity() {}

    public UUID getResultId() {
        return resultId;
    }

    public UUID getRunId() {
        return runId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getTaskAId() {
        return taskAId;
    }

    public UUID getTaskBId() {
        return taskBId;
    }

    public String getTaskATitle() {
        return taskATitle;
    }

    public String getTaskBTitle() {
        return taskBTitle;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public Double getDistance() {
        return distance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setRunId(UUID runId) {
        this.runId = runId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public void setTaskAId(UUID taskAId) {
        this.taskAId = taskAId;
    }

    public void setTaskBId(UUID taskBId) {
        this.taskBId = taskBId;
    }

    public void setTaskATitle(String taskATitle) {
        this.taskATitle = taskATitle;
    }

    public void setTaskBTitle(String taskBTitle) {
        this.taskBTitle = taskBTitle;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}