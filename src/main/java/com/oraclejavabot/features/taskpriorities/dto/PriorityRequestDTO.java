package com.oraclejavabot.features.taskpriorities.dto;

public class PriorityRequestDTO {

    private Integer prioridadId;
    private String nombre;
    private String descripcion;
    private Integer orden;

    /**
     * Obtiene el identificador de la prioridad.
     *
     * @return id de la prioridad
     */
    public Integer getPrioridadId() {
        return prioridadId;
    }

    public void setPrioridadId(Integer prioridadId) {
        this.prioridadId = prioridadId;
    }

    /**
     * Obtiene el nombre de la prioridad.
     *
     * @return nombre de la prioridad
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
     * @return descripción de la prioridad
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
     * @return orden de la prioridad
     */
    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}