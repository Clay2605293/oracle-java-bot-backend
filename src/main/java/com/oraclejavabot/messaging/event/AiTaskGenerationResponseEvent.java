package com.oraclejavabot.messaging.event;

import java.util.List;

public class AiTaskGenerationResponseEvent {

    private String projectId;
    private List<Task> tasks;

    public static class Task {

        private String titulo;
        private String descripcion;
        private Double tiempoEstimado;

        public Task() {}

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public Double getTiempoEstimado() { return tiempoEstimado; }
        public void setTiempoEstimado(Double tiempoEstimado) { this.tiempoEstimado = tiempoEstimado; }
    }

    public AiTaskGenerationResponseEvent() {}

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}
