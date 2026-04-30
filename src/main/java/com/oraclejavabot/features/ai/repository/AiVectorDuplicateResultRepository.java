package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.AiVectorDuplicateResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiVectorDuplicateResultRepository
        extends JpaRepository<AiVectorDuplicateResultEntity, UUID> {

    List<AiVectorDuplicateResultEntity> findByRunId(UUID runId);

    List<AiVectorDuplicateResultEntity> findByProjectId(UUID projectId);

    void deleteByRunId(UUID runId);
}