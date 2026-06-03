package com.oraclejavabot.features.github.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "GITHUB_COMMIT")
public class GitHubCommitEntity {

  @Id
  @Column(name = "COMMIT_SHA", nullable = false, length = 100)
  private String commitSha;

  @Column(name = "REPOSITORY_ID", nullable = false, columnDefinition = "RAW(16)")
  private UUID repositoryId;

  @Column(name = "PROJECT_ID", nullable = false, columnDefinition = "RAW(16)")
  private UUID projectId;

  @Column(name = "AUTHOR_USERNAME", length = 100)
  private String authorUsername;

  @Column(name = "AUTHOR_EMAIL", length = 320)
  private String authorEmail;

  @Column(name = "AUTHOR_NAME", length = 200)
  private String authorName;

  @Column(name = "COMMIT_MESSAGE", length = 500)
  private String commitMessage;

  @Column(name = "COMMIT_DATE")
  private LocalDateTime commitDate;

  @Column(name = "BRANCH_NAME", length = 100)
  private String branchName;

  @Column(name = "COMMIT_URL", length = 500)
  private String commitUrl;

  @Column(name = "IS_MERGE_COMMIT", nullable = false)
  private Integer isMergeCommit;

  @Column(name = "CREATED_AT")
  private LocalDateTime createdAt;

  public String getCommitSha() {
    return commitSha;
  }

  public void setCommitSha(String commitSha) {
    this.commitSha = commitSha;
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

  public String getAuthorUsername() {
    return authorUsername;
  }

  public void setAuthorUsername(String authorUsername) {
    this.authorUsername = authorUsername;
  }

  public String getAuthorEmail() {
    return authorEmail;
  }

  public void setAuthorEmail(String authorEmail) {
    this.authorEmail = authorEmail;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getCommitMessage() {
    return commitMessage;
  }

  public void setCommitMessage(String commitMessage) {
    this.commitMessage = commitMessage;
  }

  public LocalDateTime getCommitDate() {
    return commitDate;
  }

  public void setCommitDate(LocalDateTime commitDate) {
    this.commitDate = commitDate;
  }

  public String getBranchName() {
    return branchName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public String getCommitUrl() {
    return commitUrl;
  }

  public void setCommitUrl(String commitUrl) {
    this.commitUrl = commitUrl;
  }

  public Integer getIsMergeCommit() {
    return isMergeCommit;
  }

  public void setIsMergeCommit(Integer isMergeCommit) {
    this.isMergeCommit = isMergeCommit;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}