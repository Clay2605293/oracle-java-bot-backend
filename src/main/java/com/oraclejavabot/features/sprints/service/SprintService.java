package com.oraclejavabot.features.sprints.service;

import com.oraclejavabot.features.sprints.dto.SprintRequestDTO;
import com.oraclejavabot.features.sprints.dto.SprintResponseDTO;
import com.oraclejavabot.features.sprints.model.SprintEntity;
import com.oraclejavabot.features.sprints.repository.SprintRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SprintService {

    private final SprintRepository repository;

    public SprintService(SprintRepository repository) {
        this.repository = repository;
    }

    // =============================
    // CREATE
    // =============================
    public SprintResponseDTO createSprint(String projectId, SprintRequestDTO request) {

        LocalDateTime inicio = parseDate(request.getFechaInicio());
        LocalDateTime fin = parseDate(request.getFechaFin());

        // 🔥 VALIDACIÓN 1: fechas
        if (inicio.isAfter(fin) || inicio.isEqual(fin)) {
            throw new IllegalArgumentException("fechaInicio must be before fechaFin");
        }

        // 🔥 VALIDACIÓN 2: overlap
        int overlap = repository.countOverlappingSprints(projectId, inicio, fin);
        if (overlap > 0) {
            throw new IllegalArgumentException("Sprint dates overlap with existing sprint");
        }

        SprintEntity sprint = new SprintEntity();
        sprint.setNombre(request.getNombre());
        sprint.setFechaInicio(inicio);
        sprint.setFechaFin(fin);
        sprint.setProjectId(hexToUuid(projectId));

        SprintEntity saved = repository.save(sprint);

        return mapToResponse(saved);
    }

    // =============================
    // GET BY PROJECT
    // =============================
    public List<SprintResponseDTO> getSprintsByProject(String projectId) {

        return repository.findByProjectId(hexToUuid(projectId))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =============================
    // GET BY ID
    // =============================
    public SprintResponseDTO getSprintById(String sprintId) {

        SprintEntity sprint = repository.findById(hexToUuid(sprintId))
                .orElseThrow(() -> new IllegalArgumentException("Sprint not found"));

        return mapToResponse(sprint);
    }

    // =============================
    // UPDATE
    // =============================
    public SprintResponseDTO updateSprint(String sprintId, SprintRequestDTO request) {

        SprintEntity sprint = repository.findById(hexToUuid(sprintId))
                .orElseThrow(() -> new IllegalArgumentException("Sprint not found"));

        LocalDateTime inicio = parseDate(request.getFechaInicio());
        LocalDateTime fin = parseDate(request.getFechaFin());

        // 🔥 VALIDACIÓN 1
        if (inicio.isAfter(fin) || inicio.isEqual(fin)) {
            throw new IllegalArgumentException("fechaInicio must be before fechaFin");
        }

        // 🔥 VALIDACIÓN 2 (overlap excluyendo el mismo sprint)
        int overlap = repository.countOverlappingSprints(
                uuidToHex(sprint.getProjectId()),
                inicio,
                fin
        );

        if (overlap > 1) { // porque se cuenta a sí mismo
            throw new IllegalArgumentException("Sprint dates overlap with existing sprint");
        }

        sprint.setNombre(request.getNombre());
        sprint.setFechaInicio(inicio);
        sprint.setFechaFin(fin);

        return mapToResponse(repository.save(sprint));
    }

    // =============================
    // DELETE
    // =============================
    public void deleteSprint(String sprintId) {

        int tasks = repository.countTasksBySprint(sprintId);

        if (tasks > 0) {
            throw new IllegalArgumentException("Cannot delete sprint with tasks");
        }

        repository.deleteById(hexToUuid(sprintId));
    }

    // =============================
    // MAPPERS
    // =============================
    private SprintResponseDTO mapToResponse(SprintEntity sprint) {

        SprintResponseDTO dto = new SprintResponseDTO();

        dto.setSprintId(uuidToHex(sprint.getSprintId()));
        dto.setNombre(sprint.getNombre());
        dto.setFechaInicio(toString(sprint.getFechaInicio()));
        dto.setFechaFin(toString(sprint.getFechaFin()));
        dto.setProjectId(uuidToHex(sprint.getProjectId()));

        return dto;
    }

    // =============================
    // HELPERS
    // =============================
    private LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date);
    }

    private String toString(LocalDateTime date) {
        return date.toString();
    }

    private UUID hexToUuid(String hex) {
        return UUID.fromString(
                hex.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"
                )
        );
    }

    private String uuidToHex(UUID uuid) {
        return uuid.toString().replace("-", "").toUpperCase();
    }
}