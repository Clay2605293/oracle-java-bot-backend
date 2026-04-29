package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.AiSemanticDuplicateResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiSemanticDuplicateResultRepository
        extends JpaRepository<AiSemanticDuplicateResultEntity, UUID> {

    List<AiSemanticDuplicateResultEntity> findByRunId(UUID runId);

    List<AiSemanticDuplicateResultEntity> findByProjectId(UUID projectId);

    void deleteByRunId(UUID runId);
}
