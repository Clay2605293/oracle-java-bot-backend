package com.oraclejavabot.features.kpis.dto;

public record DashboardSummaryDTO(
        int totalTasks,
        int completedTasks,
        int onTimeTasks,
        int delayedTasks,
        double totalEstimatedHours,
        double totalRealHours,
        double completionRate,
        double onTimeRate,
        double estimationAccuracy,
        double avgTasksPerDeveloper,
        double avgHoursPerDeveloper
) {
}