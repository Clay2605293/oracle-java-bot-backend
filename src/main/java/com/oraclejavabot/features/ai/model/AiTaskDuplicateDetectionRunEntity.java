package com.oraclejavabot.features.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AI_TASK_DUPLICATE_DETECTION_RUN")
public class AiTaskDuplicateDetectionRunEntity {

    @Id
    @GeneratedValue
    @Column(name = "RUN_ID")
    private UUID runId;

    @Column(name = "PROJECT_ID", nullable = false)
    private UUID projectId;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "THRESHOLD", nullable = false)
    private Double threshold;

    @Column(name = "TASKS_ANALYZED", nullable = false)
    private Integer tasksAnalyzed;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    public AiTaskDuplicateDetectionRunEntity() {}

    public UUID getRunId() {
        return runId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getStatus() {
        return status;
    }

    public Double getThreshold() {
        return threshold;
    }

    public Integer getTasksAnalyzed() {
        return tasksAnalyzed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void setTasksAnalyzed(Integer tasksAnalyzed) {
        this.tasksAnalyzed = tasksAnalyzed;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}