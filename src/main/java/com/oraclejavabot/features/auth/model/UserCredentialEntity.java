package com.oraclejavabot.features.auth.model;

import com.oraclejavabot.features.users.model.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USUARIO_CREDENCIAL", schema = "CHATBOT_USER")
public class UserCredentialEntity {

    @Id
    @Column(name = "CREDENCIAL_ID", columnDefinition = "RAW(16)")
    private UUID credencialId;

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "ACTIVO", nullable = false)
    private Integer activo;

    @Column(name = "ULTIMO_LOGIN")
    private LocalDateTime ultimoLogin;

    public UserCredentialEntity() {
    }

    public UUID getCredencialId() {
        return credencialId;
    }

    public void setCredencialId(UUID credencialId) {
        this.credencialId = credencialId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Integer getActivo() {
        return activo;
    }

    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }
}