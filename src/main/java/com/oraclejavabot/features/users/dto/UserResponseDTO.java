package com.oraclejavabot.features.users.dto;

public class UserResponseDTO {

    private String message;
    private String email;
    private String telegramId;

    public UserResponseDTO() {
    }

    public UserResponseDTO(String message, String email, String telegramId) {
        this.message = message;
        this.email = email;
        this.telegramId = telegramId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }
}