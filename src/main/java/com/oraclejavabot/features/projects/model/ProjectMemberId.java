package com.oraclejavabot.features.projects.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ProjectMemberId implements Serializable {

    private UUID userId;
    private UUID projectId;

    public ProjectMemberId() {}

    public ProjectMemberId(UUID userId, UUID projectId) {
        this.userId = userId;
        this.projectId = projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}