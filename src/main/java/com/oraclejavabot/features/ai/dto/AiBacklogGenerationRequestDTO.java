package com.oraclejavabot.features.ai.dto;

import java.util.List;

public class AiBacklogGenerationRequestDTO {

    private Double maxHours;
    private List<String> documentIds;

    public AiBacklogGenerationRequestDTO() {}

    public Double getMaxHours() {
        return maxHours;
    }

    public void setMaxHours(Double maxHours) {
        this.maxHours = maxHours;
    }

    public List<String> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<String> documentIds) {
        this.documentIds = documentIds;
    }
}