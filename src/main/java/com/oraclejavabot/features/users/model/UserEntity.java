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

    /**
     * Obtiene el primer nombre del usuario.
     *
     * @return primer nombre (no nulo)
     */
    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    /**
     * Obtiene el apellido del usuario.
     *
     * @return apellido (no nulo)
     */
    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el teléfono del usuario.
     *
     * @return teléfono (puede ser nulo)
     */
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return email (no nulo)
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
     * @return telegramId (no nulo)
     */
    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    /**
     * Obtiene el identificador del rol asignado al usuario.
     *
     * @return rolId (no nulo)
     */
    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    /**
     * Obtiene el identificador del estado del usuario.
     *
     * @return estadoId (no nulo)
     */
    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    /**
     * Obtiene el identificador del manager asignado al usuario.
     *
     * @return managerId (puede ser nulo)
     */
    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }
}