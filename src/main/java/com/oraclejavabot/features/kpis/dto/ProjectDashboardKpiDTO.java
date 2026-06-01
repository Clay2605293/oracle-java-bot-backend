package com.oraclejavabot.features.kpis.dto;

import java.util.List;

public record ProjectDashboardKpiDTO(
        String projectId,
        String sprintId,
        String sprintName,
        String developerId,
        String developerName,
        String scopeLabel,
        DashboardSummaryDTO summary,
        List<DashboardSprintHistoryPointDTO> sprintHistory
) {
}