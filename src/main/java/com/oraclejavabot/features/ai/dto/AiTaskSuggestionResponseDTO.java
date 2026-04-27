package com.oraclejavabot.features.ai.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AiTaskSuggestionResponseDTO {

    private UUID aiTaskId;
    private UUID projectId;
    private String titulo;
    private String descripcion;
    private Double tiempoEstimado;
    private String status;
    private LocalDateTime createdAt;

    public AiTaskSuggestionResponseDTO() {}

    public AiTaskSuggestionResponseDTO(
            UUID aiTaskId,
            UUID projectId,
            String titulo,
            String descripcion,
            Double tiempoEstimado,
            String status,
            LocalDateTime createdAt
    ) {
        this.aiTaskId = aiTaskId;
        this.projectId = projectId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tiempoEstimado = tiempoEstimado;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getAiTaskId() {
        return aiTaskId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getTiempoEstimado() {
        return tiempoEstimado;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setAiTaskId(UUID aiTaskId) {
        this.aiTaskId = aiTaskId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setTiempoEstimado(Double tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}