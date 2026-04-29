package com.oraclejavabot.features.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TASK_SEMANTIC_EMBEDDING")
public class TaskSemanticEmbeddingEntity {

    @Id
    @Column(name = "TASK_ID")
    private UUID taskId;

    @Column(name = "PROJECT_ID", nullable = false)
    private UUID projectId;

    @Column(name = "EMBEDDING_TEXT", nullable = false)
    private String embeddingText;

    @Lob
    @Column(name = "EMBEDDING_JSON", nullable = false)
    private String embeddingJson;

    @Column(name = "EMBEDDING_MODEL", nullable = false)
    private String embeddingModel;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    public TaskSemanticEmbeddingEntity() {}

    public UUID getTaskId() {
        return taskId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getEmbeddingText() {
        return embeddingText;
    }

    public String getEmbeddingJson() {
        return embeddingJson;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public void setEmbeddingText(String embeddingText) {
        this.embeddingText = embeddingText;
    }

    public void setEmbeddingJson(String embeddingJson) {
        this.embeddingJson = embeddingJson;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
