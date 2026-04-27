package com.oraclejavabot.features.ai.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TAREA_AI_SUGERIDA")
public class AiTaskSuggestionEntity {

    @Id
    @GeneratedValue
    @Column(name = "AI_TASK_ID")
    private UUID aiTaskId;

    @Column(name = "PROJECT_ID", nullable = false)
    private UUID projectId;

    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "TIEMPO_ESTIMADO")
    private Double tiempoEstimado;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public AiTaskSuggestionEntity() {}

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