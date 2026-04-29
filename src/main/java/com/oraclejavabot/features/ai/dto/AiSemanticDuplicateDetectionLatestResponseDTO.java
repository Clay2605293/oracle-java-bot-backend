package com.oraclejavabot.features.ai.dto;

import java.util.List;

public class AiSemanticDuplicateDetectionLatestResponseDTO {

    private AiSemanticDuplicateDetectionRunResponseDTO run;
    private List<AiSemanticDuplicateDetectionResultResponseDTO> results;

    public AiSemanticDuplicateDetectionLatestResponseDTO() {}

    public AiSemanticDuplicateDetectionRunResponseDTO getRun() {
        return run;
    }

    public void setRun(AiSemanticDuplicateDetectionRunResponseDTO run) {
        this.run = run;
    }

    public List<AiSemanticDuplicateDetectionResultResponseDTO> getResults() {
        return results;
    }

    public void setResults(List<AiSemanticDuplicateDetectionResultResponseDTO> results) {
        this.results = results;
    }
}
