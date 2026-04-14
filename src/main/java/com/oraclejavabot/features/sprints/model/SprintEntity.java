package com.oraclejavabot.features.sprints.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "SPRINT")
public class SprintEntity {

    @Id
    @GeneratedValue
    @Column(name = "SPRINT_ID")
    private UUID sprintId;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "PROJECT_ID", nullable = false)
    private UUID projectId;

    public SprintEntity() {}

    // =============================
    // GETTERS
    // =============================
    public UUID getSprintId() {
        return sprintId;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public UUID getProjectId() {
        return projectId;
    }

    // =============================
    // SETTERS
    // =============================
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}