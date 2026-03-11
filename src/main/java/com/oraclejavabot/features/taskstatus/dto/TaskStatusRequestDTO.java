package com.oraclejavabot.features.taskstatus.dto;

public class TaskStatusRequestDTO {

    private Integer estadoId;
    private String nombre;
    private String descripcion;
    private Integer esActivo;

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
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

    public Integer getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Integer esActivo) {
        this.esActivo = esActivo;
    }
}