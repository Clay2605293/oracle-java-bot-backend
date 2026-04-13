package com.oraclejavabot.features.projects.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "PROYECTO")
public class ProjectEntity {

    @Id
    @Column(name = "PROJECT_ID")
    private UUID projectId;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "PROGRESO")
    private Double progreso;

    public UUID getProjectId() {
        return projectId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getProgreso() {
        return progreso;
    }
}