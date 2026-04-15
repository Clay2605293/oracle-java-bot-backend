package com.oraclejavabot.features.teams.dto;

public class TeamMemberDTO {

    private String userId;
    private String teamId;

    // 🔹 NUEVO
    private String nombre;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    // 🔹 NUEVO
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}