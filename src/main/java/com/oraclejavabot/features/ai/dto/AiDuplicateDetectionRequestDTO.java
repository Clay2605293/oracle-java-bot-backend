package com.oraclejavabot.features.ai.dto;

public class AiDuplicateDetectionRequestDTO {

    private Double threshold;

    public AiDuplicateDetectionRequestDTO() {}

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
}