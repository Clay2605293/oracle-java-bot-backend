package com.oraclejavabot.features.teams.dto;

import jakarta.validation.constraints.NotBlank;

public class TeamRequestDTO {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotBlank
    private String ownerId;

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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}