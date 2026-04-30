package com.oraclejavabot.features.ai.dto;

import java.util.List;

public class AiVectorDuplicateDetectionLatestResponseDTO {

    private AiVectorDuplicateDetectionRunResponseDTO run;
    private List<AiVectorDuplicateDetectionResultResponseDTO> results;

    public AiVectorDuplicateDetectionLatestResponseDTO() {}

    public AiVectorDuplicateDetectionRunResponseDTO getRun() {
        return run;
    }

    public void setRun(AiVectorDuplicateDetectionRunResponseDTO run) {
        this.run = run;
    }

    public List<AiVectorDuplicateDetectionResultResponseDTO> getResults() {
        return results;
    }

    public void setResults(List<AiVectorDuplicateDetectionResultResponseDTO> results) {
        this.results = results;
    }
}