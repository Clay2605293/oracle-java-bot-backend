package com.oraclejavabot.features.tasks.dto;

public class TaskResponseDTO {

    private String taskId;
    private String titulo;
    private String descripcion;

    private String fechaCreacion;
    private String fechaLimite;
    private String fechaFinalizacion;

    private Integer estadoId;
    private Integer prioridadId;

    private String projectId;
    private String sprintId;

    // 🔹 NUEVO
    private String sprintNombre;

    private Double tiempoEstimado;
    private Double tiempoReal;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

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

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(String fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public Integer getPrioridadId() {
        return prioridadId;
    }

    public void setPrioridadId(Integer prioridadId) {
        this.prioridadId = prioridadId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSprintId() {
        return sprintId;
    }

    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }

    // 🔹 NUEVO
    public String getSprintNombre() {
        return sprintNombre;
    }

    public void setSprintNombre(String sprintNombre) {
        this.sprintNombre = sprintNombre;
    }

    public Double getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(Double tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public Double getTiempoReal() {
        return tiempoReal;
    }

    public void setTiempoReal(Double tiempoReal) {
        this.tiempoReal = tiempoReal;
    }
}