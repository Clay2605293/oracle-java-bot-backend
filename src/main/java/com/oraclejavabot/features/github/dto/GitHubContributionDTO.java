package com.oraclejavabot.features.github.dto;

public class GitHubContributionDTO {

  private String userId;
  private String name;
  private String email;
  private String githubUsername;
  private long totalCommits;
  private long openedIssues;
  private long closedIssues;

  public GitHubContributionDTO(
      String userId,
      String name,
      String email,
      String githubUsername,
      long totalCommits,
      long openedIssues,
      long closedIssues) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.githubUsername = githubUsername;
    this.totalCommits = totalCommits;
    this.openedIssues = openedIssues;
    this.closedIssues = closedIssues;
  }

  public String getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getGithubUsername() {
    return githubUsername;
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