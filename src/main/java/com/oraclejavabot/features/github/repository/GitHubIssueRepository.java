package com.oraclejavabot.features.github.repository;

import com.oraclejavabot.features.github.model.GitHubIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GitHubIssueRepository extends JpaRepository<GitHubIssueEntity, Long> {

  long countByProjectIdAndAuthorUsernameIgnoreCase(UUID projectId, String authorUsername);

  long countByProjectIdAndClosedByUsernameIgnoreCase(UUID projectId, String closedByUsername);

  long countByRepositoryIdAndAuthorUsernameIgnoreCase(UUID repositoryId, String authorUsername);

  long countByRepositoryIdAndClosedByUsernameIgnoreCase(UUID repositoryId, String closedByUsername);
}