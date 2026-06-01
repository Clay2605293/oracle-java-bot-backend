package com.oraclejavabot.features.kpis.dto;

public record DashboardSprintHistoryPointDTO(
        String sprintId,
        String sprintName,
        int totalTasks,
        int completedTasks,
        int onTimeTasks,
        int delayedTasks,
        double totalEstimatedHours,
        double totalRealHours
) {
}