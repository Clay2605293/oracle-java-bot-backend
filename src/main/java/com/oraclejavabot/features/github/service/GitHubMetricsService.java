package com.oraclejavabot.features.github.service;

import com.oraclejavabot.features.github.dto.GitHubContributionDTO;
import com.oraclejavabot.features.github.dto.GitHubRepositoryActivityDTO;
import com.oraclejavabot.features.github.dto.GitHubSprintActivityDTO;
import com.oraclejavabot.features.github.repository.GitHubContributionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitHubMetricsService {

  private final GitHubContributionRepository contributionRepository;

  public GitHubMetricsService(GitHubContributionRepository contributionRepository) {
    this.contributionRepository = contributionRepository;
  }

  public List<GitHubContributionDTO> getProjectContributions(String projectId, String sprintId) {
    return contributionRepository.findContributionsByProjectId(projectId, sprintId);
  }
  
  public List<GitHubSprintActivityDTO> getSprintActivity(String projectId) {
    return contributionRepository.findSprintActivityByProjectId(projectId);
  }

  public List<GitHubRepositoryActivityDTO> getRepositoryActivity(String projectId, String sprintId, String developerId) {
    return contributionRepository.findRepositoryActivityByProjectId(projectId, sprintId, developerId);
  }
}
