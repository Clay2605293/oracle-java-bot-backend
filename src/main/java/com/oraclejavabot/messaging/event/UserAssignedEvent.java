package com.oraclejavabot.messaging.event;

public class UserAssignedEvent {

    private String taskId;
    private String userId;

    // 🔥 NUEVO: datos enriquecidos para notificación
    private String taskTitle;
    private String projectName;
    private String priority;
    private String dueDate;

    public UserAssignedEvent() {
    }

    public UserAssignedEvent(String taskId,
                             String userId,
                             String taskTitle,
                             String projectName,
                             String priority,
                             String dueDate) {
        this.taskId = taskId;
        this.userId = userId;
        this.taskTitle = taskTitle;
        this.projectName = projectName;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    // =============================
    // GETTERS & SETTERS
    // =============================

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

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}