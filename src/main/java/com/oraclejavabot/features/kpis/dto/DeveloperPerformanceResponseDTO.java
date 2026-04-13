package com.oraclejavabot.features.kpis.dto;

import java.util.List;

public class DeveloperPerformanceResponseDTO {

    private String userId;
    private String nombre;
    private DeveloperGlobalPerformanceDTO rendimientoGlobal;
    private List<DeveloperSprintPerformanceDTO> historicoSprints;

    public DeveloperPerformanceResponseDTO() {}

    public DeveloperPerformanceResponseDTO(
            String userId,
            String nombre,
            DeveloperGlobalPerformanceDTO rendimientoGlobal,
            List<DeveloperSprintPerformanceDTO> historicoSprints) {
        this.userId = userId;
        this.nombre = nombre;
        this.rendimientoGlobal = rendimientoGlobal;
        this.historicoSprints = historicoSprints;
    }

    public String getUserId() {
        return userId;
    }

    public String getNombre() {
        return nombre;
    }

    public DeveloperGlobalPerformanceDTO getRendimientoGlobal() {
        return rendimientoGlobal;
    }

    public List<DeveloperSprintPerformanceDTO> getHistoricoSprints() {
        return historicoSprints;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRendimientoGlobal(DeveloperGlobalPerformanceDTO rendimientoGlobal) {
        this.rendimientoGlobal = rendimientoGlobal;
    }

    public void setHistoricoSprints(List<DeveloperSprintPerformanceDTO> historicoSprints) {
        this.historicoSprints = historicoSprints;
    }
}