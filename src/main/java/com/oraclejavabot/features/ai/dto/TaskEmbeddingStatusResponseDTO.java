package com.oraclejavabot.features.ai.dto;

public class TaskEmbeddingStatusResponseDTO {

    private String projectId;
    private int totalTasks;
    private int semanticEmbeddings;
    private int vectorEmbeddings;
    private boolean readyForVectorSearch;

    public TaskEmbeddingStatusResponseDTO(
            String projectId,
            int totalTasks,
            int semanticEmbeddings,
            int vectorEmbeddings,
            boolean readyForVectorSearch
    ) {
        this.projectId = projectId;
        this.totalTasks = totalTasks;
        this.semanticEmbeddings = semanticEmbeddings;
        this.vectorEmbeddings = vectorEmbeddings;
        this.readyForVectorSearch = readyForVectorSearch;
    }

    public String getProjectId() { return projectId; }
    public int getTotalTasks() { return totalTasks; }
    public int getSemanticEmbeddings() { return semanticEmbeddings; }
    public int getVectorEmbeddings() { return vectorEmbeddings; }
    public boolean isReadyForVectorSearch() { return readyForVectorSearch; }
}