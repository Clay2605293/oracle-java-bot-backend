package com.oraclejavabot.features.auth.dto;

import java.util.UUID;

public class LoginResponseDTO {

    private String token;
    private UUID userId;
    private String email;
    private Integer rolId;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, UUID userId, String email, Integer rolId) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.rolId = rolId;
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Integer getRolId() {
        return rolId;
    }
}