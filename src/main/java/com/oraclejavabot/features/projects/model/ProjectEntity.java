package com.oraclejavabot.features.projects.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PROYECTO")
public class ProjectEntity {

    @Id
    @GeneratedValue
    @Column(name = "PROJECT_ID")
    private UUID projectId;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "FECHA_INICIO")
    private LocalDateTime fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDateTime fechaFin;

    @Column(name = "PROGRESO")
    private Double progreso;

    @Column(name = "TEAM_ID", nullable = false)
    private UUID teamId;

    public ProjectEntity() {}

    public UUID getProjectId() {
        return projectId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public Double getProgreso() {
        return progreso;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public void setProgreso(Double progreso) {
    this.progreso = progreso;
}
}