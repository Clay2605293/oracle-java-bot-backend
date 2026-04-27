package com.oraclejavabot.features.ai.dto;

public class AiBacklogGenerationResponseDTO {

    private String message;
    private String projectId;
    private Integer documentsSent;

    public AiBacklogGenerationResponseDTO() {}

    public AiBacklogGenerationResponseDTO(String message, String projectId, Integer documentsSent) {
        this.message = message;
        this.projectId = projectId;
        this.documentsSent = documentsSent;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setDocumentsSent(Integer documentsSent) {
        this.documentsSent = documentsSent;
    }
}