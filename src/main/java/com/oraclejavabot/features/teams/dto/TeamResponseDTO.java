package com.oraclejavabot.features.teams.dto;

public class TeamResponseDTO {

    private String teamId;
    private String nombre;
    private String descripcion;
    private String ownerId;

    // 🔹 NUEVOS
    private String ownerNombre;
    private int totalMembers;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    // 🔹 NUEVOS getters/setters
    public String getOwnerNombre() {
        return ownerNombre;
    }

    public void setOwnerNombre(String ownerNombre) {
        this.ownerNombre = ownerNombre;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }
}