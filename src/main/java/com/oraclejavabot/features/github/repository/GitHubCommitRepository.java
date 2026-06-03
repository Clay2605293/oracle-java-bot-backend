package com.oraclejavabot.features.github.repository;

import com.oraclejavabot.features.github.model.GitHubCommitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GitHubCommitRepository extends JpaRepository<GitHubCommitEntity, String> {

  long countByProjectIdAndAuthorUsernameIgnoreCase(UUID projectId, String authorUsername);

  long countByRepositoryIdAndAuthorUsernameIgnoreCase(UUID repositoryId, String authorUsername);
}