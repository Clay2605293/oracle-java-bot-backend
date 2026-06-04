package com.oraclejavabot.features.github.dto;

public class GitHubRepositoryActivityDTO {

  private String repositoryId;
  private String owner;
  private String repoName;
  private long totalCommits;
  private long openedIssues;
  private long activeIssues;
  private long closedIssues;

  public GitHubRepositoryActivityDTO(
      String repositoryId,
      String owner,
      String repoName,
      long totalCommits,
      long openedIssues,
      long activeIssues,
      long closedIssues) {
    this.repositoryId = repositoryId;
    this.owner = owner;
    this.repoName = repoName;
    this.totalCommits = totalCommits;
    this.openedIssues = openedIssues;
    this.activeIssues = activeIssues;
    this.closedIssues = closedIssues;
  }

  public String getRepositoryId() {
    return repositoryId;
  }

  public String getOwner() {
    return owner;
  }

  public String getRepoName() {
    return repoName;
  }

  public long getTotalCommits() {
    return totalCommits;
  }

  public long getOpenedIssues() {
    return openedIssues;
  }

  public long getActiveIssues() {
    return activeIssues;
  }

  public long getClosedIssues() {
    return closedIssues;
  }
}
