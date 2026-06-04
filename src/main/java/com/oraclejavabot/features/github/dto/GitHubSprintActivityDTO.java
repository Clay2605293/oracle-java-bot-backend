package com.oraclejavabot.features.github.dto;

public class GitHubSprintActivityDTO {

  private String sprintId;
  private String sprintName;
  private long totalCommits;
  private long openedIssues;
  private long closedIssues;

  public GitHubSprintActivityDTO(
      String sprintId,
      String sprintName,
      long totalCommits,
      long openedIssues,
      long closedIssues) {
    this.sprintId = sprintId;
    this.sprintName = sprintName;
    this.totalCommits = totalCommits;
    this.openedIssues = openedIssues;
    this.closedIssues = closedIssues;
  }

  public String getSprintId() {
    return sprintId;
  }

  public String getSprintName() {
    return sprintName;
  }

  public long getTotalCommits() {
    return totalCommits;
  }

  public long getOpenedIssues() {
    return openedIssues;
  }

  public long getClosedIssues() {
    return closedIssues;
  }
}