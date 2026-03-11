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

    /**
     * Obtiene el identificador de la prioridad.
     *
     * @return id de la prioridad
     */
    public Integer getPrioridadId() {
        return prioridadId;
    }

    /**
     * Obtiene el nombre de la prioridad.
     *
     * @return nombre de la prioridad
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la descripción de la prioridad.
     *
     * @return descripción de la prioridad
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el orden de visualización de la prioridad.
     *
     * @return orden de la prioridad
     */
    public Integer getOrden() {
        return orden;
    }
}