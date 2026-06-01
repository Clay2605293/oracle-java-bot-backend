package com.oraclejavabot.features.kpis.service;

import com.oraclejavabot.features.kpis.dto.DashboardDeveloperOptionDTO;
import com.oraclejavabot.features.kpis.dto.DashboardSprintHistoryPointDTO;
import com.oraclejavabot.features.kpis.dto.DashboardSprintOptionDTO;
import com.oraclejavabot.features.kpis.dto.DashboardSummaryDTO;
import com.oraclejavabot.features.kpis.dto.ProjectDashboardKpiDTO;
import com.oraclejavabot.features.kpis.repository.ProjectDashboardKpiRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectDashboardKpiService {

    private final ProjectDashboardKpiRepository repository;

    public ProjectDashboardKpiService(ProjectDashboardKpiRepository repository) {
        this.repository = repository;
    }

    public List<DashboardSprintOptionDTO> getSprintOptions(String projectId) {
        return repository.findSprintOptions(projectId);
    }

    public List<DashboardDeveloperOptionDTO> getDeveloperOptions(String projectId) {
        return repository.findDeveloperOptions(projectId);
    }

    public ProjectDashboardKpiDTO getProjectDashboardKpis(
            String projectId,
            String sprintId,
            String developerId
    ) {
        String normalizedSprintId = normalizeOptionalId(sprintId);
        String normalizedDeveloperId = normalizeOptionalId(developerId);

        int developerCount = normalizedDeveloperId == null
                ? repository.countActiveDevelopersInProjectTeam(projectId)
                : 1;

        DashboardSummaryDTO summary = repository.calculateSummary(
                projectId,
                normalizedSprintId,
                normalizedDeveloperId,
                developerCount
        );

        List<DashboardSprintHistoryPointDTO> sprintHistory = repository.findSprintHistory(
                projectId,
                normalizedDeveloperId
        );

        String sprintName = repository.findSprintName(normalizedSprintId);
        String developerName = repository.findDeveloperName(normalizedDeveloperId);

        String scopeLabel = buildScopeLabel(sprintName, developerName);

        return new ProjectDashboardKpiDTO(
                projectId,
                normalizedSprintId,
                sprintName,
                normalizedDeveloperId,
                developerName,
                scopeLabel,
                summary,
                sprintHistory
        );
    }

    private String normalizeOptionalId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        if ("ALL".equalsIgnoreCase(value.trim())) {
            return null;
        }

        return value.trim();
    }

    private String buildScopeLabel(String sprintName, String developerName) {
        String sprintLabel = sprintName == null ? "todos los sprints" : sprintName;
        String developerLabel = developerName == null ? "todo el equipo" : developerName;

        return "Mostrando métricas de " + sprintLabel + " para " + developerLabel;
    }
}