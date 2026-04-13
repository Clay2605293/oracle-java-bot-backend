package com.oraclejavabot.features.kpis.dto;

public class DeveloperSprintPerformanceDTO {

    private String sprintId;
    private String sprintNombre;
    private int tareasTerminadas;
    private double horasReales;

    public DeveloperSprintPerformanceDTO() {}

    public DeveloperSprintPerformanceDTO(
            String sprintId,
            String sprintNombre,
            int tareasTerminadas,
            double horasReales) {
        this.sprintId = sprintId;
        this.sprintNombre = sprintNombre;
        this.tareasTerminadas = tareasTerminadas;
        this.horasReales = horasReales;
    }

    public String getSprintId() {
        return sprintId;
    }

    public String getSprintNombre() {
        return sprintNombre;
    }

    public int getTareasTerminadas() {
        return tareasTerminadas;
    }

    public double getHorasReales() {
        return horasReales;
    }

    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }

    public void setSprintNombre(String sprintNombre) {
        this.sprintNombre = sprintNombre;
    }

    public void setTareasTerminadas(int tareasTerminadas) {
        this.tareasTerminadas = tareasTerminadas;
    }

    public void setHorasReales(double horasReales) {
        this.horasReales = horasReales;
    }
}