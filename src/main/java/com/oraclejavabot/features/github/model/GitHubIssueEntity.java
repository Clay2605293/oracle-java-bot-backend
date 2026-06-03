package com.oraclejavabot.features.github.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "GITHUB_ISSUE")
public class GitHubIssueEntity {

  @Id
  @Column(name = "ISSUE_ID", nullable = false)
  private Long issueId;

  @Column(name = "REPOSITORY_ID", nullable = false, columnDefinition = "RAW(16)")
  private UUID repositoryId;

  @Column(name = "PROJECT_ID", nullable = false, columnDefinition = "RAW(16)")
  private UUID projectId;

  @Column(name = "ISSUE_NUMBER", nullable = false)
  private Long issueNumber;

  @Column(name = "TITLE", nullable = false, length = 500)
  private String title;

  @Lob
  @Column(name = "BODY")
  private String body;

  @Column(name = "STATE", nullable = false, length = 50)
  private String state;

  @Column(name = "AUTHOR_USERNAME", length = 100)
  private String authorUsername;

  @Column(name = "CLOSED_BY_USERNAME", length = 100)
  private String closedByUsername;

  @Column(name = "ASSIGNEE_USERNAME", length = 100)
  private String assigneeUsername;

  @Column(name = "CREATED_AT_GITHUB")
  private LocalDateTime createdAtGithub;

  @Column(name = "UPDATED_AT_GITHUB")
  private LocalDateTime updatedAtGithub;

  @Column(name = "CLOSED_AT_GITHUB")
  private LocalDateTime closedAtGithub;

  @Column(name = "ISSUE_URL", length = 500)
  private String issueUrl;

  @Column(name = "IS_PULL_REQUEST", nullable = false)
  private Integer isPullRequest;

  @Column(name = "SYNCED_AT")
  private LocalDateTime syncedAt;

  public Long getIssueId() {
    return issueId;
  }

  public void setIssueId(Long issueId) {
    this.issueId = issueId;
  }

  public UUID getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(UUID repositoryId) {
    this.repositoryId = repositoryId;
  }

  public UUID getProjectId() {
    return projectId;
  }

  public void setProjectId(UUID projectId) {
    this.projectId = projectId;
  }

  public Long getIssueNumber() {
    return issueNumber;
  }

  public void setIssueNumber(Long issueNumber) {
    this.issueNumber = issueNumber;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getAuthorUsername() {
    return authorUsername;
  }

  public void setAuthorUsername(String authorUsername) {
    this.authorUsername = authorUsername;
  }

  public String getClosedByUsername() {
    return closedByUsername;
  }

  public void setClosedByUsername(String closedByUsername) {
    this.closedByUsername = closedByUsername;
  }

  public String getAssigneeUsername() {
    return assigneeUsername;
  }

  public void setAssigneeUsername(String assigneeUsername) {
    this.assigneeUsername = assigneeUsername;
  }

  public LocalDateTime getCreatedAtGithub() {
    return createdAtGithub;
  }

  public void setCreatedAtGithub(LocalDateTime createdAtGithub) {
    this.createdAtGithub = createdAtGithub;
  }

  public LocalDateTime getUpdatedAtGithub() {
    return updatedAtGithub;
  }

  public void setUpdatedAtGithub(LocalDateTime updatedAtGithub) {
    this.updatedAtGithub = updatedAtGithub;
  }

  public LocalDateTime getClosedAtGithub() {
    return closedAtGithub;
  }

  public void setClosedAtGithub(LocalDateTime closedAtGithub) {
    this.closedAtGithub = closedAtGithub;
  }

  public String getIssueUrl() {
    return issueUrl;
  }

  public void setIssueUrl(String issueUrl) {
    this.issueUrl = issueUrl;
  }

  public Integer getIsPullRequest() {
    return isPullRequest;
  }

  public void setIsPullRequest(Integer isPullRequest) {
    this.isPullRequest = isPullRequest;
  }

  public LocalDateTime getSyncedAt() {
    return syncedAt;
  }

  public void setSyncedAt(LocalDateTime syncedAt) {
    this.syncedAt = syncedAt;
  }
}