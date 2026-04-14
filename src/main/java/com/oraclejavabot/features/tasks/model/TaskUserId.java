package com.oraclejavabot.features.tasks.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TaskUserId implements Serializable {

    private UUID userId;
    private UUID taskId;

    public TaskUserId() {}

    public TaskUserId(UUID userId, UUID taskId) {
        this.userId = userId;
        this.taskId = taskId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskUserId)) return false;
        TaskUserId that = (TaskUserId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, taskId);
    }
}