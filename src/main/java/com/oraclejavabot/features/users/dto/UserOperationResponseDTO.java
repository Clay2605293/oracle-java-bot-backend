package com.oraclejavabot.features.users.dto;

public class UserOperationResponseDTO {

    private String message;
    private String userId;
    private String email;
    private Integer estadoId;

    public UserOperationResponseDTO() {
    }

    public UserOperationResponseDTO(String message, String userId, String email, Integer estadoId) {
        this.message = message;
        this.userId = userId;
        this.email = email;
        this.estadoId = estadoId;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Integer getEstadoId() {
        return estadoId;
    }
}