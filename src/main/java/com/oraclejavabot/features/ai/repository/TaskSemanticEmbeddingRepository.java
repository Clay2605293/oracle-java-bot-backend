package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.TaskSemanticEmbeddingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskSemanticEmbeddingRepository
        extends JpaRepository<TaskSemanticEmbeddingEntity, UUID> {

    List<TaskSemanticEmbeddingEntity> findByProjectId(UUID projectId);

    void deleteByProjectId(UUID projectId);
}
