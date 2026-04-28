package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.AiTaskDuplicateDetectionRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiTaskDuplicateDetectionRunRepository extends JpaRepository<AiTaskDuplicateDetectionRunEntity, UUID> {

    List<AiTaskDuplicateDetectionRunEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    Optional<AiTaskDuplicateDetectionRunEntity> findFirstByProjectIdOrderByCreatedAtDesc(UUID projectId);
}