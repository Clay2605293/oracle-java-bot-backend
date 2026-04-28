package com.oraclejavabot.features.ai.dto;

import java.util.List;

public class AiDuplicateDetectionLatestResponseDTO {

    private AiDuplicateDetectionRunResponseDTO run;
    private List<AiDuplicateDetectionResultResponseDTO> results;

    public AiDuplicateDetectionLatestResponseDTO() {}

    public AiDuplicateDetectionRunResponseDTO getRun() {
        return run;
    }

    public void setRun(AiDuplicateDetectionRunResponseDTO run) {
        this.run = run;
    }

    public List<AiDuplicateDetectionResultResponseDTO> getResults() {
        return results;
    }

    public void setResults(List<AiDuplicateDetectionResultResponseDTO> results) {
        this.results = results;
    }
}