package com.oraclejavabot.features.projects.controller;

import com.oraclejavabot.features.projects.dto.ProjectProgressResponseDTO;
import com.oraclejavabot.features.projects.service.ProjectService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping("/{projectId}/progress")
    public ProjectProgressResponseDTO getProjectProgress(@PathVariable String projectId) {
        return service.getProjectProgress(projectId);
    }
}