package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.TaskVectorEmbeddingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskVectorEmbeddingRepository
        extends JpaRepository<TaskVectorEmbeddingEntity, UUID> {

    List<TaskVectorEmbeddingEntity> findByProjectId(UUID projectId);

    void deleteByProjectId(UUID projectId);
}