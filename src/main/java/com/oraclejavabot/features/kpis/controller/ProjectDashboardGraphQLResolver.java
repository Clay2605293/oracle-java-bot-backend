package com.oraclejavabot.features.kpis.controller;

import com.oraclejavabot.features.kpis.dto.DashboardDeveloperOptionDTO;
import com.oraclejavabot.features.kpis.dto.DashboardSprintOptionDTO;
import com.oraclejavabot.features.kpis.dto.ProjectDashboardKpiDTO;
import com.oraclejavabot.features.kpis.service.ProjectDashboardKpiService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProjectDashboardGraphQLResolver {

    private final ProjectDashboardKpiService service;

    public ProjectDashboardGraphQLResolver(ProjectDashboardKpiService service) {
        this.service = service;
    }

    @QueryMapping
    public List<DashboardSprintOptionDTO> dashboardSprintOptions(@Argument String projectId) {
        return service.getSprintOptions(projectId);
    }

    @QueryMapping
    public List<DashboardDeveloperOptionDTO> dashboardDeveloperOptions(@Argument String projectId) {
        return service.getDeveloperOptions(projectId);
    }

    @QueryMapping
    public ProjectDashboardKpiDTO projectDashboardKpis(
            @Argument String projectId,
            @Argument String sprintId,
            @Argument String developerId
    ) {
        return service.getProjectDashboardKpis(projectId, sprintId, developerId);
    }
}