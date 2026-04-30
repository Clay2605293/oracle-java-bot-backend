package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.AiVectorDuplicateDetectionRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiVectorDuplicateDetectionRunRepository
        extends JpaRepository<AiVectorDuplicateDetectionRunEntity, UUID> {

    List<AiVectorDuplicateDetectionRunEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    Optional<AiVectorDuplicateDetectionRunEntity> findTopByProjectIdOrderByCreatedAtDesc(UUID projectId);
}