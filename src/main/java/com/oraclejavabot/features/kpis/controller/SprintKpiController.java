package com.oraclejavabot.features.kpis.controller;

import com.oraclejavabot.features.kpis.dto.SprintKpiResponseDTO;
import com.oraclejavabot.features.kpis.service.SprintKpiService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sprints")
public class SprintKpiController {

    private final SprintKpiService service;

    public SprintKpiController(SprintKpiService service) {
        this.service = service;
    }

    @GetMapping("/{sprintId}/kpis")
    public SprintKpiResponseDTO getSprintKpis(@PathVariable String sprintId) {
        return service.getSprintKpis(sprintId);
    }
}