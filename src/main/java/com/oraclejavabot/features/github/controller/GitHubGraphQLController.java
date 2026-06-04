package com.oraclejavabot.features.github.controller;

import com.oraclejavabot.features.github.dto.GitHubContributionDTO;
import com.oraclejavabot.features.github.dto.GitHubRepositoryActivityDTO;
import com.oraclejavabot.features.github.dto.GitHubSprintActivityDTO;
import com.oraclejavabot.features.github.service.GitHubMetricsService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GitHubGraphQLController {

  private final GitHubMetricsService gitHubMetricsService;

  public GitHubGraphQLController(GitHubMetricsService gitHubMetricsService) {
    this.gitHubMetricsService = gitHubMetricsService;
  }

  @QueryMapping
  public List<GitHubContributionDTO> githubContributions(
      @Argument String projectId,
      @Argument String sprintId) {
    return gitHubMetricsService.getProjectContributions(projectId, sprintId);
  }

  @QueryMapping
  public List<GitHubSprintActivityDTO> githubSprintActivity(@Argument String projectId) {
    return gitHubMetricsService.getSprintActivity(projectId);
  }

  @QueryMapping
  public List<GitHubRepositoryActivityDTO> githubRepositoryActivity(
      @Argument String projectId,
      @Argument String sprintId,
      @Argument String developerId) {
    return gitHubMetricsService.getRepositoryActivity(projectId, sprintId, developerId);
  }
}
