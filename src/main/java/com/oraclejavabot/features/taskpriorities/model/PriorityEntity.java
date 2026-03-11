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

    /**
     * Obtiene el nombre de la prioridad.
     *
     * @return nombre (no nulo)
     */
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción de la prioridad.
     *
     * @return descripción (puede ser nula)
     */
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el orden de visualización de la prioridad.
     *
     * @return orden (no nulo)
     */
    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}