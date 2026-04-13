package com.oraclejavabot.features.projects.dto;

public class ProjectProgressResponseDTO {

    private String projectId;
    private Double progress;

    public ProjectProgressResponseDTO() {}

    public ProjectProgressResponseDTO(String projectId, Double progress) {
        this.projectId = projectId;
        this.progress = progress;
    }

    public String getProjectId() {
        return projectId;
    }

    public Double getProgress() {
        return progress;
    }
}