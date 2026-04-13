package com.oraclejavabot.features.teams.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "EQUIPO")
public class TeamEntity {

    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private UUID teamId;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "USER_ID", nullable = false)
    private UUID ownerId;

    public TeamEntity() {}

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}