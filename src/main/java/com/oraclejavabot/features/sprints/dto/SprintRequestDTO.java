package com.oraclejavabot.features.sprints.dto;

import jakarta.validation.constraints.NotBlank;

public class SprintRequestDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String fechaInicio;

    @NotBlank
    private String fechaFin;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }
}