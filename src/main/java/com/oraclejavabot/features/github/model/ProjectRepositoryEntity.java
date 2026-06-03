package com.oraclejavabot.features.github.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PROJECT_REPOSITORY")
public class ProjectRepositoryEntity {

  @Id
  @Column(name = "REPOSITORY_ID", columnDefinition = "RAW(16)")
  private UUID repositoryId;

  @Column(name = "PROJECT_ID", nullable = false, columnDefinition = "RAW(16)")
  private UUID projectId;

  @Column(name = "OWNER", nullable = false, length = 100)
  private String owner;

  @Column(name = "REPO_NAME", nullable = false, length = 150)
  private String repoName;

  @Column(name = "DISPLAY_NAME", length = 150)
  private String displayName;

  @Column(name = "DEFAULT_BRANCH", nullable = false, length = 100)
  private String defaultBranch;

  @Column(name = "IS_ACTIVE", nullable = false)
  private Integer isActive;

  @Column(name = "CREATED_AT")
  private LocalDateTime createdAt;

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

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getRepoName() {
    return repoName;
  }

  public void setRepoName(String repoName) {
    this.repoName = repoName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDefaultBranch() {
    return defaultBranch;
  }

  public void setDefaultBranch(String defaultBranch) {
    this.defaultBranch = defaultBranch;
  }

  public Integer getIsActive() {
    return isActive;
  }

  public void setIsActive(Integer isActive) {
    this.isActive = isActive;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}