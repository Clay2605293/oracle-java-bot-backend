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

    /**
     * Obtiene el identificador del estado.
     *
     * @return id del estado
     */
    public Integer getEstadoId() {
        return estadoId;
    }

    /**
     * Obtiene el nombre del estado.
     *
     * @return nombre del estado
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la descripción del estado.
     *
     * @return descripción del estado
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Indica si el estado está activo (1) o no (0).
     *
     * @return 1 si está activo, 0 si no
     */
    public Integer getEsActivo() {
        return esActivo;
    }
}