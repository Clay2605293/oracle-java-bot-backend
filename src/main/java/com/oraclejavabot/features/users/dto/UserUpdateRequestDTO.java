package com.oraclejavabot.features.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class UserUpdateRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 500, message = "El nombre no puede exceder 500 caracteres")
    private String primerNombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 500, message = "El apellido no puede exceder 500 caracteres")
    private String apellido;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Size(max = 320, message = "El email no puede exceder 320 caracteres")
    private String email;

    @NotBlank(message = "El telegramId es obligatorio")
    @Size(max = 50, message = "El telegramId no puede exceder 50 caracteres")
    private String telegramId;

    @Size(max = 50, message = "El telegramChatId no puede exceder 50 caracteres")
    private String telegramChatId;

    @NotNull(message = "El rolId es obligatorio")
    private Integer rolId;

    @NotNull(message = "El estadoId es obligatorio")
    private Integer estadoId;

    private UUID managerId;

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public String getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }
}