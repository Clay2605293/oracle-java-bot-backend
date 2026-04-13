package com.oraclejavabot.features.kpis.controller;

import com.oraclejavabot.features.kpis.dto.DeveloperPerformanceResponseDTO;
import com.oraclejavabot.features.kpis.service.DeveloperPerformanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class DeveloperPerformanceController {

    private final DeveloperPerformanceService service;

    public DeveloperPerformanceController(DeveloperPerformanceService service) {
        this.service = service;
    }

    @GetMapping("/{projectId}/developers/performance")
    public List<DeveloperPerformanceResponseDTO> getDeveloperPerformance(
            @PathVariable String projectId
    ) {
        return service.getDeveloperPerformance(projectId);
    }
}