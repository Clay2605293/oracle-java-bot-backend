package com.oraclejavabot.messaging.event;

import java.util.List;

public class AiTaskGenerationResponseEvent {

    private String projectId;
    private List<Task> tasks;

    public static class Task {

        private String titulo;
        private String descripcion;
        private String priority;
        private Double estimatedHours;
        private Integer suggestedDeadlineOffsetDays;
        private String source;

        public Task() {}

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

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public Double getEstimatedHours() {
            return estimatedHours;
        }

        public void setEstimatedHours(Double estimatedHours) {
            this.estimatedHours = estimatedHours;
        }

        public Integer getSuggestedDeadlineOffsetDays() {
            return suggestedDeadlineOffsetDays;
        }

        public void setSuggestedDeadlineOffsetDays(Integer suggestedDeadlineOffsetDays) {
            this.suggestedDeadlineOffsetDays = suggestedDeadlineOffsetDays;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    public AiTaskGenerationResponseEvent() {}

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}