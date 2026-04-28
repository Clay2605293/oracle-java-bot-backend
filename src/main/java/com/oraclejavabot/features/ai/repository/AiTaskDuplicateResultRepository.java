package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.AiTaskDuplicateResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiTaskDuplicateResultRepository extends JpaRepository<AiTaskDuplicateResultEntity, UUID> {

    List<AiTaskDuplicateResultEntity> findByRunId(UUID runId);

    List<AiTaskDuplicateResultEntity> findByProjectId(UUID projectId);

    void deleteByRunId(UUID runId);
}