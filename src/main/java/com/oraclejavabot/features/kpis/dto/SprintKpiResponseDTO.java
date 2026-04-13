package com.oraclejavabot.features.kpis.dto;

public class SprintKpiResponseDTO {

    private Integer totalTareas;
    private Integer tareasCompletadas;
    private Integer aTiempo;
    private Integer conRetraso;
    private Double totalEstimadoHrs;
    private Double totalRealHrs;

    public SprintKpiResponseDTO() {}

    public SprintKpiResponseDTO(
            Integer totalTareas,
            Integer tareasCompletadas,
            Integer aTiempo,
            Integer conRetraso,
            Double totalEstimadoHrs,
            Double totalRealHrs) {

        this.totalTareas = totalTareas;
        this.tareasCompletadas = tareasCompletadas;
        this.aTiempo = aTiempo;
        this.conRetraso = conRetraso;
        this.totalEstimadoHrs = totalEstimadoHrs;
        this.totalRealHrs = totalRealHrs;
    }

    public Integer getTotalTareas() {
        return totalTareas;
    }

    public Integer getTareasCompletadas() {
        return tareasCompletadas;
    }

    public Integer getATiempo() {
        return aTiempo;
    }

    public Integer getConRetraso() {
        return conRetraso;
    }

    public Double getTotalEstimadoHrs() {
        return totalEstimadoHrs;
    }

    public Double getTotalRealHrs() {
        return totalRealHrs;
    }
}