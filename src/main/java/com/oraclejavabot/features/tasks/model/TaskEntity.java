package com.oraclejavabot.features.tasks.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "TAREA")
public class TaskEntity {

    @Id
    @Column(name = "TASK_ID")
    private UUID taskId;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "FECHA_CREACION")
    private Instant fechaCreacion;

    @Column(name = "FECHA_LIMITE")
    private Instant fechaLimite;

    @Column(name = "FECHA_FINALIZACION")
    private Instant fechaFinalizacion;

    @Column(name = "ESTADO_ID")
    private Integer estadoId;

    @Column(name = "PRIORIDAD_ID")
    private Integer prioridadId;

    @Column(name = "PROYECT_ID")
    private UUID projectId;

    @Column(name = "SPRINT_ID")
    private UUID sprintId;

    @Column(name = "TIEMPO_ESTIMADO")
    private Double tiempoEstimado;

    @Column(name = "TIEMPO_REAL")
    private Double tiempoReal;

}