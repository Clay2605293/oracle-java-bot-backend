package com.oraclejavabot.features.taskstatus.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CAT_ESTADO_TAREA")
public class TaskStatusEntity {

    @Id
    @Column(name = "ESTADO_ID")
    private Integer estadoId;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "ES_ACTIVO", nullable = false)
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