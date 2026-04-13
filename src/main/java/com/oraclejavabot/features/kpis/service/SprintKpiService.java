package com.oraclejavabot.features.kpis.service;

import com.oraclejavabot.features.kpis.dto.SprintKpiResponseDTO;
import com.oraclejavabot.features.kpis.repository.SprintKpiRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SprintKpiService {

    private final SprintKpiRepository repository;

    public SprintKpiService(SprintKpiRepository repository) {
        this.repository = repository;
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value.toString());
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        return Double.parseDouble(value.toString());
    }

    public SprintKpiResponseDTO getSprintKpis(String sprintId) {

        List<Object[]> rows = repository.getSprintKpis(sprintId);

        if (rows == null || rows.isEmpty()) {
            return new SprintKpiResponseDTO(0,0,0,0,0.0,0.0);
        }

        Object[] result = rows.get(0);

        return new SprintKpiResponseDTO(
                toInt(result[0]),
                toInt(result[1]),
                toInt(result[2]),
                toInt(result[3]),
                toDouble(result[4]),
                toDouble(result[5])
        );
    }
}