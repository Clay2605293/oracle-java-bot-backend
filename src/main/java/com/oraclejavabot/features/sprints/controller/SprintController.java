package com.oraclejavabot.features.sprints.controller;

import com.oraclejavabot.features.sprints.dto.SprintRequestDTO;
import com.oraclejavabot.features.sprints.dto.SprintResponseDTO;
import com.oraclejavabot.features.sprints.service.SprintService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SprintController {

    private final SprintService service;

    public SprintController(SprintService service) {
        this.service = service;
    }

    // =============================
    // CREATE SPRINT
    // =============================
    @PostMapping("/projects/{projectId}/sprints")
    @ResponseStatus(HttpStatus.CREATED)
    public SprintResponseDTO createSprint(
            @PathVariable String projectId,
            @Valid @RequestBody SprintRequestDTO request) {

        return service.createSprint(projectId, request);
    }

    // =============================
    // GET SPRINTS BY PROJECT
    // =============================
    @GetMapping("/projects/{projectId}/sprints")
    public List<SprintResponseDTO> getSprints(@PathVariable String projectId) {
        return service.getSprintsByProject(projectId);
    }

    // =============================
    // GET SPRINT BY ID
    // =============================
    @GetMapping("/sprints/{sprintId}")
    public SprintResponseDTO getSprint(@PathVariable String sprintId) {
        return service.getSprintById(sprintId);
    }

    // =============================
    // UPDATE SPRINT
    // =============================
    @PutMapping("/sprints/{sprintId}")
    public SprintResponseDTO updateSprint(
            @PathVariable String sprintId,
            @Valid @RequestBody SprintRequestDTO request) {

        return service.updateSprint(sprintId, request);
    }

    // =============================
    // DELETE SPRINT
    // =============================
    @DeleteMapping("/sprints/{sprintId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSprint(@PathVariable String sprintId) {
        service.deleteSprint(sprintId);
    }
}