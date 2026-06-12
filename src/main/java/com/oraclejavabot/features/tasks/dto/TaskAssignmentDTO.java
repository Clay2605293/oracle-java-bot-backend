package com.oraclejavabot.features.tasks.dto;

public class TaskAssignmentDTO {

    private String taskId;
    private String titulo;
    private String developerId;
    private String developerNombre;
    private String sprintId;
    private Double tiempoEstimado;
    private Double tiempoReal;
    private Integer estadoId;
    private Integer prioridadId;

    public TaskAssignmentDTO(String taskId,
                             String titulo,
                             String developerId,
                             String developerNombre,
                             String sprintId,
                             Double tiempoEstimado,
                             Double tiempoReal,
                             Integer estadoId,
                             Integer prioridadId) {
        this.taskId = taskId;
        this.titulo = titulo;
        this.developerId = developerId;
        this.developerNombre = developerNombre;
        this.sprintId = sprintId;
        this.tiempoEstimado = tiempoEstimado;
        this.tiempoReal = tiempoReal;
        this.estadoId = estadoId;
        this.prioridadId = prioridadId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public String getDeveloperNombre() {
        return developerNombre;
    }

    public String getSprintId() {
        return sprintId;
    }

    public Double getTiempoEstimado() {
        return tiempoEstimado;
    }

    public Double getTiempoReal() {
        return tiempoReal;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public Integer getPrioridadId() {
        return prioridadId;
    }
}