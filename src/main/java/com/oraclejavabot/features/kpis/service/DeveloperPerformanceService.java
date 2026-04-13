package com.oraclejavabot.features.kpis.service;

import com.oraclejavabot.features.kpis.dto.DeveloperGlobalPerformanceDTO;
import com.oraclejavabot.features.kpis.dto.DeveloperPerformanceResponseDTO;
import com.oraclejavabot.features.kpis.dto.DeveloperSprintPerformanceDTO;
import com.oraclejavabot.features.kpis.repository.DeveloperPerformanceRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeveloperPerformanceService {

    private final DeveloperPerformanceRepository repository;

    public DeveloperPerformanceService(DeveloperPerformanceRepository repository) {
        this.repository = repository;
    }

    public List<DeveloperPerformanceResponseDTO> getDeveloperPerformance(String projectId) {

        // =========================
        // 1. Ejecutar queries
        // =========================
        List<Object[]> globalResults = repository.getGlobalPerformance(projectId);
        List<Object[]> sprintResults = repository.getSprintPerformance(projectId);

        // =========================
        // 2. Map por usuario
        // =========================
        Map<String, DeveloperPerformanceResponseDTO> userMap = new LinkedHashMap<>();

        // =========================
        // 3. Procesar global
        // =========================
        for (Object[] row : globalResults) {

            String userId = (String) row[0];
            String nombre = (String) row[1];

            int asignadas = ((Number) row[2]).intValue();
            int completadas = ((Number) row[3]).intValue();
            double porcentaje = ((Number) row[4]).doubleValue();

            DeveloperGlobalPerformanceDTO globalDTO =
                    new DeveloperGlobalPerformanceDTO(asignadas, completadas, porcentaje);

            DeveloperPerformanceResponseDTO responseDTO =
                    new DeveloperPerformanceResponseDTO();

            responseDTO.setUserId(userId);
            responseDTO.setNombre(nombre);
            responseDTO.setRendimientoGlobal(globalDTO);
            responseDTO.setHistoricoSprints(new ArrayList<>());

            userMap.put(userId, responseDTO);
        }

        // =========================
        // 4. Procesar histórico
        // =========================
        for (Object[] row : sprintResults) {

            String userId = (String) row[0];

            // Puede haber null si no tiene tareas
            if (row[1] == null) continue;

            String sprintId = (String) row[1];
            String sprintNombre = (String) row[2];

            int tareasTerminadas = ((Number) row[3]).intValue();
            double horasReales = ((Number) row[4]).doubleValue();

            DeveloperSprintPerformanceDTO sprintDTO =
                    new DeveloperSprintPerformanceDTO(
                            sprintId,
                            sprintNombre,
                            tareasTerminadas,
                            horasReales
                    );

            DeveloperPerformanceResponseDTO userDTO = userMap.get(userId);

            if (userDTO != null) {
                userDTO.getHistoricoSprints().add(sprintDTO);
            }
        }

        // =========================
        // 5. Resultado final
        // =========================
        return new ArrayList<>(userMap.values());
    }
}