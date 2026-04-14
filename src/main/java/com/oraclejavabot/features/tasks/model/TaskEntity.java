package com.oraclejavabot.features.tasks.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TAREA")
public class TaskEntity {

    @Id
    @GeneratedValue
    @Column(name = "TASK_ID")
    private UUID taskId;

    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_LIMITE", nullable = false)
    private LocalDateTime fechaLimite;

    @Column(name = "FECHA_FINALIZACION")
    private LocalDateTime fechaFinalizacion;

    @Column(name = "ESTADO_ID", nullable = false)
    private Integer estadoId;

    @Column(name = "PRIORIDAD_ID", nullable = false)
    private Integer prioridadId;

    @Column(name = "PROJECT_ID", nullable = false)
    private UUID projectId;

    @Column(name = "SPRINT_ID")
    private UUID sprintId;

    @Column(name = "TIEMPO_ESTIMADO")
    private Double tiempoEstimado;

    @Column(name = "TIEMPO_REAL")
    private Double tiempoReal;

    public TaskEntity() {}

    // GETTERS
    public UUID getTaskId() { return taskId; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public LocalDateTime getFechaFinalizacion() { return fechaFinalizacion; }
    public Integer getEstadoId() { return estadoId; }
    public Integer getPrioridadId() { return prioridadId; }
    public UUID getProjectId() { return projectId; }
    public UUID getSprintId() { return sprintId; }
    public Double getTiempoEstimado() { return tiempoEstimado; }
    public Double getTiempoReal() { return tiempoReal; }

    // SETTERS
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }
    public void setPrioridadId(Integer prioridadId) { this.prioridadId = prioridadId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public void setSprintId(UUID sprintId) { this.sprintId = sprintId; }
    public void setTiempoEstimado(Double tiempoEstimado) { this.tiempoEstimado = tiempoEstimado; }
    public void setTiempoReal(Double tiempoReal) { this.tiempoReal = tiempoReal; }
}