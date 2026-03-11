package com.oraclejavabot.features.taskstatus.dto;

public class TaskStatusRequestDTO {

    private Integer estadoId;
    private String nombre;
    private String descripcion;
    private Integer esActivo;

    /**
     * Obtiene el identificador del estado.
     *
     * @return id del estado
     */
    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    /**
     * Obtiene el nombre del estado.
     *
     * @return nombre del estado
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
     * @return descripción del estado
     */
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Indica si el estado está activo (1) o no (0).
     *
     * @return 1 si está activo, 0 si no
     */
    public Integer getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(Integer esActivo) {
        this.esActivo = esActivo;
    }
}