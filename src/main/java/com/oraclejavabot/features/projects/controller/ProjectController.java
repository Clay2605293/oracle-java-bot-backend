package com.oraclejavabot.features.projects.controller;

import com.oraclejavabot.features.projects.dto.ProjectProgressResponseDTO;
import com.oraclejavabot.features.projects.dto.ProjectRequestDTO;
import com.oraclejavabot.features.projects.dto.ProjectResponseDTO;
import com.oraclejavabot.features.projects.service.ProjectService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    // =============================
    // CREATE PROJECT
    // =============================
    @PostMapping("/teams/{teamId}/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDTO createProject(
            @PathVariable String teamId,
            @Valid @RequestBody ProjectRequestDTO request) {

        return service.createProject(teamId, request);
    }

    // =============================
    // GET PROJECTS BY TEAM
    // =============================
    @GetMapping("/teams/{teamId}/projects")
    public List<ProjectResponseDTO> getProjectsByTeam(@PathVariable String teamId) {
        return service.getProjectsByTeam(teamId);
    }

    // =============================
    // 🔥 NUEVO — PROJECTS BY MANAGER
    // =============================
    @GetMapping("/users/{managerId}/managed-projects")
    public List<ProjectResponseDTO> getProjectsByManager(@PathVariable String managerId) {
        return service.getProjectsByManager(managerId);
    }

    // =============================
    // GET PROJECT BY ID
    // =============================
    @GetMapping("/projects/{projectId}")
    public ProjectResponseDTO getProjectById(@PathVariable String projectId) {
        return service.getProjectById(projectId);
    }

    // =============================
    // UPDATE PROJECT
    // =============================
    @PutMapping("/projects/{projectId}")
    public ProjectResponseDTO updateProject(
            @PathVariable String projectId,
            @Valid @RequestBody ProjectRequestDTO request) {

        return service.updateProject(projectId, request);
    }

    // =============================
    // DELETE PROJECT
    // =============================
    @DeleteMapping("/projects/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable String projectId) {
        service.deleteProject(projectId);
    }

    // =============================
    // PROGRESS
    // =============================
    @GetMapping("/projects/{projectId}/progress")
    public ProjectProgressResponseDTO getProjectProgress(@PathVariable String projectId) {
        return service.getProjectProgress(projectId);
    }
}