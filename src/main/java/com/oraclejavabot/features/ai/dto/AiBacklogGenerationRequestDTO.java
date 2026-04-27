package com.oraclejavabot.features.ai.dto;

public class AiBacklogGenerationRequestDTO {

    private Double maxHours;

    public AiBacklogGenerationRequestDTO() {}

    public Double getMaxHours() {
        return maxHours;
    }

    public void setMaxHours(Double maxHours) {
        this.maxHours = maxHours;
    }
}