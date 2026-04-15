package com.oraclejavabot.features.projects.service;

import com.oraclejavabot.features.projects.dto.*;
import com.oraclejavabot.features.projects.model.ProjectEntity;
import com.oraclejavabot.features.projects.repository.ProjectRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    // =============================
    // CREATE
    // =============================
    public ProjectResponseDTO createProject(String teamId, ProjectRequestDTO request) {

        UUID teamUuid = hexToUuid(teamId);

        ProjectEntity project = new ProjectEntity();
        project.setNombre(request.getNombre());
        project.setDescripcion(request.getDescripcion());
        project.setFechaInicio(parseDate(request.getFechaInicio()));
        project.setFechaFin(parseDate(request.getFechaFin()));
        project.setTeamId(teamUuid);

        project.setProgreso(0.0);

        ProjectEntity saved = repository.save(project);

        return mapToResponse(saved);
    }

    // =============================
    // GET BY TEAM
    // =============================
    public List<ProjectResponseDTO> getProjectsByTeam(String teamId) {

        UUID teamUuid = hexToUuid(teamId);

        return repository.findByTeamId(teamUuid)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =============================
    // GET PROJECTS BY USER
    // =============================
    public List<ProjectResponseDTO> getProjectsByUser(String userId) {

        return repository.findProjectsByUserTeam(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =============================
    // GET BY ID
    // =============================
    public ProjectResponseDTO getProjectById(String projectId) {

        ProjectEntity project = repository.findById(hexToUuid(projectId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        return mapToResponse(project);
    }

    // =============================
    // UPDATE
    // =============================
    public ProjectResponseDTO updateProject(String projectId, ProjectRequestDTO request) {

        ProjectEntity project = repository.findById(hexToUuid(projectId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        project.setNombre(request.getNombre());
        project.setDescripcion(request.getDescripcion());
        project.setFechaInicio(parseDate(request.getFechaInicio()));
        project.setFechaFin(parseDate(request.getFechaFin()));

        return mapToResponse(repository.save(project));
    }

    // =============================
    // DELETE (solo si vacío)
    // =============================
    public void deleteProject(String projectId) {

        int members = repository.countProjectMembers(projectId);

        if (members > 0) {
            throw new IllegalArgumentException("Cannot delete project with members");
        }

        repository.deleteById(hexToUuid(projectId));
    }

    // =============================
    // PROGRESS (ya lo tenías)
    // =============================
    public ProjectProgressResponseDTO getProjectProgress(String projectId) {

        Object progressValue = repository.getProjectProgress(projectId);

        double progress = 0.0;

        if (progressValue != null) {
            progress = Double.parseDouble(progressValue.toString());
        }

        return new ProjectProgressResponseDTO(projectId, progress);
    }

    // =============================
    // MAPPERS
    // =============================
    private ProjectResponseDTO mapToResponse(ProjectEntity project) {

        ProjectResponseDTO dto = new ProjectResponseDTO();

        dto.setProjectId(uuidToHex(project.getProjectId()));
        dto.setNombre(project.getNombre());
        dto.setDescripcion(project.getDescripcion());
        dto.setFechaInicio(toString(project.getFechaInicio()));
        dto.setFechaFin(toString(project.getFechaFin()));
        dto.setProgreso(project.getProgreso());
        dto.setTeamId(uuidToHex(project.getTeamId()));

        return dto;
    }

    // =============================
    // HELPERS
    // =============================
    private LocalDateTime parseDate(String date) {
        return date == null ? null : LocalDateTime.parse(date);
    }

    private String toString(LocalDateTime date) {
        return date == null ? null : date.toString();
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