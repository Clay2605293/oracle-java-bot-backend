package com.oraclejavabot.features.projects.service;

import com.oraclejavabot.features.projects.dto.ProjectProgressResponseDTO;
import com.oraclejavabot.features.projects.repository.ProjectRepository;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public ProjectProgressResponseDTO getProjectProgress(String projectId) {

        Object progressValue = repository.getProjectProgress(projectId);

        double progress = 0.0;

        if (progressValue != null) {
            progress = Double.parseDouble(progressValue.toString());
        }

        return new ProjectProgressResponseDTO(projectId, progress);
    }
}