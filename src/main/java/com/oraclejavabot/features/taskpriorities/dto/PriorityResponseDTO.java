package com.oraclejavabot.features.taskpriorities.dto;

public class PriorityResponseDTO {

    private Integer prioridadId;
    private String nombre;
    private String descripcion;
    private Integer orden;

    public PriorityResponseDTO(Integer prioridadId, String nombre, String descripcion, Integer orden) {
        this.prioridadId = prioridadId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden;
    }

    public Integer getPrioridadId() {
        return prioridadId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getOrden() {
        return orden;
    }
}