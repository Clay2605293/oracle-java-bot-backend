package com.oraclejavabot.features.users.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "USUARIO")
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(name = "USER_ID", columnDefinition = "RAW(16)")
    private UUID userId;

    @Column(name = "PRIMER_NOMBRE", nullable = false, length = 500)
    private String primerNombre;

    @Column(name = "APELLIDO", nullable = false, length = 500)
    private String apellido;

    @Column(name = "TELEFONO", length = 50)
    private String telefono;

    @Column(name = "EMAIL", nullable = false, length = 320)
    private String email;

    @Column(name = "TELEGRAM_ID", nullable = false, length = 50)
    private String telegramId;

    @Column(name = "ROL_ID", nullable = false)
    private Integer rolId;

    @Column(name = "ESTADO_ID", nullable = false)
    private Integer estadoId;

    @Column(name = "MANAGER_ID", columnDefinition = "RAW(16)")
    private UUID managerId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

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