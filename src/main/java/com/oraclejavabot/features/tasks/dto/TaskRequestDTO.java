package com.oraclejavabot.features.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskRequestDTO {

    @NotBlank
    private String titulo;

    private String descripcion;

    @NotBlank
    private String fechaLimite;

    @NotNull
    private Integer prioridadId;

    private String sprintId;

    private Double tiempoEstimado;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Integer getPrioridadId() {
        return prioridadId;
    }

    public void setPrioridadId(Integer prioridadId) {
        this.prioridadId = prioridadId;
    }

    public String getSprintId() {
        return sprintId;
    }

    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }

    public Double getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(Double tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }
}