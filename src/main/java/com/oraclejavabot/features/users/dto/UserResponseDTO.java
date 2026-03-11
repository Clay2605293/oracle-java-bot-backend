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

    /**
     * Obtiene el mensaje de respuesta.
     *
     * @return mensaje de respuesta
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return email del usuario
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el identificador de Telegram del usuario.
     *
     * @return telegramId del usuario
     */
    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }
}