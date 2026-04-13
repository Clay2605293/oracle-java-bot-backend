package com.oraclejavabot.features.kpis.service;

import com.oraclejavabot.features.kpis.dto.SprintKpiResponseDTO;
import com.oraclejavabot.features.kpis.repository.SprintKpiRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class SprintKpiService {

    private final SprintKpiRepository repository;

    public SprintKpiService(SprintKpiRepository repository) {
        this.repository = repository;
    }

    public SprintKpiResponseDTO getSprintKpis(UUID sprintId) {

        Map<String, Object> result = repository.getSprintKpis(sprintId);

        return new SprintKpiResponseDTO(
                ((Number) result.get("TOTAL_TAREAS")).intValue(),
                ((Number) result.get("TAREAS_COMPLETADAS")).intValue(),
                ((Number) result.get("A_TIEMPO")).intValue(),
                ((Number) result.get("CON_RETRASO")).intValue(),
                ((Number) result.get("TOTAL_ESTIMADO_HRS")).doubleValue(),
                ((Number) result.get("TOTAL_REAL_HRS")).doubleValue()
        );
    }
}