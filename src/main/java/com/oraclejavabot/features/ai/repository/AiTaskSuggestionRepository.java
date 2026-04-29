package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.AiTaskSuggestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiTaskSuggestionRepository extends JpaRepository<AiTaskSuggestionEntity, UUID> {

    List<AiTaskSuggestionEntity> findByProjectId(UUID projectId);

    List<AiTaskSuggestionEntity> findByProjectIdAndStatus(UUID projectId, String status);

    Long countByProjectId(UUID projectId);

    void deleteByProjectId(UUID projectId);
}