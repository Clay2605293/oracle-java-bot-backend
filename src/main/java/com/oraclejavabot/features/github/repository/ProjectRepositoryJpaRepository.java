package com.oraclejavabot.features.github.repository;

import com.oraclejavabot.features.github.model.ProjectRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepositoryJpaRepository extends JpaRepository<ProjectRepositoryEntity, UUID> {

  List<ProjectRepositoryEntity> findByProjectIdAndIsActive(UUID projectId, Integer isActive);

  Optional<ProjectRepositoryEntity> findByOwnerIgnoreCaseAndRepoNameIgnoreCaseAndIsActive(
      String owner,
      String repoName,
      Integer isActive);
}