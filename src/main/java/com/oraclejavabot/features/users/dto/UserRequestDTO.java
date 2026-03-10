package com.oraclejavabot.features.users.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {

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
}