package com.oraclejavabot.features.taskstatus.dto;

public class TaskStatusResponseDTO {

    private Integer estadoId;
    private String nombre;
    private String descripcion;
    private Integer esActivo;

    public TaskStatusResponseDTO(Integer estadoId, String nombre, String descripcion, Integer esActivo) {
        this.estadoId = estadoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esActivo = esActivo;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getEsActivo() {
        return esActivo;
    }
}