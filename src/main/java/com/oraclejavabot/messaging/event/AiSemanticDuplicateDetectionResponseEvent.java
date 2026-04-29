package com.oraclejavabot.messaging.event;

import java.util.List;

public class AiSemanticDuplicateDetectionResponseEvent {

    private String runId;
    private String projectId;
    private String status;
    private String errorMessage;
    private String embeddingModel;
    private List<Embedding> embeddings;
    private List<Duplicate> duplicates;

    public static class Embedding {

        private String taskId;
        private String embeddingText;
        private String embeddingJson;

        public Embedding() {}

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getEmbeddingText() {
            return embeddingText;
        }

        public void setEmbeddingText(String embeddingText) {
            this.embeddingText = embeddingText;
        }

        public String getEmbeddingJson() {
            return embeddingJson;
        }

        public void setEmbeddingJson(String embeddingJson) {
            this.embeddingJson = embeddingJson;
        }
    }

    public static class Duplicate {

        private String taskAId;
        private String taskBId;
        private String taskATitle;
        private String taskBTitle;
        private Double similarityScore;
        private String reason;

        public Duplicate() {}

        public String getTaskAId() {
            return taskAId;
        }

        public void setTaskAId(String taskAId) {
            this.taskAId = taskAId;
        }

        public String getTaskBId() {
            return taskBId;
        }

        public void setTaskBId(String taskBId) {
            this.taskBId = taskBId;
        }

        public String getTaskATitle() {
            return taskATitle;
        }

        public void setTaskATitle(String taskATitle) {
            this.taskATitle = taskATitle;
        }

        public String getTaskBTitle() {
            return taskBTitle;
        }

        public void setTaskBTitle(String taskBTitle) {
            this.taskBTitle = taskBTitle;
        }

        public Double getSimilarityScore() {
            return similarityScore;
        }

        public void setSimilarityScore(Double similarityScore) {
            this.similarityScore = similarityScore;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public AiSemanticDuplicateDetectionResponseEvent() {}

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<Embedding> getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(List<Embedding> embeddings) {
        this.embeddings = embeddings;
    }

    public List<Duplicate> getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(List<Duplicate> duplicates) {
        this.duplicates = duplicates;
    }
}
