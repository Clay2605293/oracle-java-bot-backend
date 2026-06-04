package com.oraclejavabot.features.github.controller;

import com.oraclejavabot.features.github.dto.GitHubContributionDTO;
import com.oraclejavabot.features.github.service.GitHubMetricsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/github")
public class GitHubMetricsController {

  private final GitHubMetricsService gitHubMetricsService;

  public GitHubMetricsController(GitHubMetricsService gitHubMetricsService) {
    this.gitHubMetricsService = gitHubMetricsService;
  }

  @GetMapping("/contributions")
  public List<GitHubContributionDTO> getProjectContributions(
      @PathVariable String projectId,
      @RequestParam(required = false) String sprintId) {
    return gitHubMetricsService.getProjectContributions(projectId, sprintId);
  }
}
