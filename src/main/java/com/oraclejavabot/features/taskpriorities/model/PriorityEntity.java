package com.oraclejavabot.features.taskpriorities.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CAT_PRIORIDAD")
public class PriorityEntity {

    @Id
    @Column(name = "PRIORIDAD_ID")
    private Integer prioridadId;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "ORDEN", nullable = false)
    private Integer orden;

    public Integer getPrioridadId() {
        return prioridadId;
    }

    public void setPrioridadId(Integer prioridadId) {
        this.prioridadId = prioridadId;
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

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}