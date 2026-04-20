package com.oraclejavabot.messaging.event;

public class UserAssignedEvent {

    private String taskId;
    private String userId;

    public UserAssignedEvent() {
    }

    public UserAssignedEvent(String taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}