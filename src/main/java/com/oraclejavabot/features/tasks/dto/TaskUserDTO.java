package com.oraclejavabot.features.tasks.dto;

public class TaskUserDTO {

    private String taskId;
    private String userId;

    // 🔹 NUEVO
    private String nombre;

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

    // 🔹 NUEVO
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}