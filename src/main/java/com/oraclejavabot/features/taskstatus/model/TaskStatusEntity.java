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

    /**
     * Obtiene el nombre del estado de la tarea.
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
     * Obtiene la descripción del estado.
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
     * Indica si el estado está activo (1) o inactivo (0).
     *
     * @return 1 si activo, 0 si inactivo
     */
    public Integer getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Integer esActivo) {
        this.esActivo = esActivo;
    }
}