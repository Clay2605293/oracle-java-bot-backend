package com.oraclejavabot.features.ai.dto;

public class AiBacklogGenerationResponseDTO {

    private String message;
    private String projectId;
    private Integer documentsSent;
    private Double maxHours;

    public AiBacklogGenerationResponseDTO() {}

    public AiBacklogGenerationResponseDTO(
            String message,
            String projectId,
            Integer documentsSent,
            Double maxHours
    ) {
        this.message = message;
        this.projectId = projectId;
        this.documentsSent = documentsSent;
        this.maxHours = maxHours;
    }

    public String getMessage() {
        return message;
    }

    public String getProjectId() {
        return projectId;
    }

    public Integer getDocumentsSent() {
        return documentsSent;
    }

    public Double getMaxHours() {
        return maxHours;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setDocumentsSent(Integer documentsSent) {
        this.documentsSent = documentsSent;
    }

    public void setMaxHours(Double maxHours) {
        this.maxHours = maxHours;
    }
}