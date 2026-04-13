package com.oraclejavabot.features.kpis.dto;

public class DeveloperGlobalPerformanceDTO {

    private int asignadas;
    private int completadas;
    private double porcentajeCompletadas;

    public DeveloperGlobalPerformanceDTO() {}

    public DeveloperGlobalPerformanceDTO(
            int asignadas,
            int completadas,
            double porcentajeCompletadas) {
        this.asignadas = asignadas;
        this.completadas = completadas;
        this.porcentajeCompletadas = porcentajeCompletadas;
    }

    public int getAsignadas() {
        return asignadas;
    }

    public int getCompletadas() {
        return completadas;
    }

    public double getPorcentajeCompletadas() {
        return porcentajeCompletadas;
    }

    public void setAsignadas(int asignadas) {
        this.asignadas = asignadas;
    }

    public void setCompletadas(int completadas) {
        this.completadas = completadas;
    }

    public void setPorcentajeCompletadas(double porcentajeCompletadas) {
        this.porcentajeCompletadas = porcentajeCompletadas;
    }
}