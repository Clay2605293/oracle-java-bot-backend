package com.oraclejavabot.features.ai.repository;

import com.oraclejavabot.features.ai.model.AiSemanticDuplicateDetectionRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiSemanticDuplicateDetectionRunRepository
        extends JpaRepository<AiSemanticDuplicateDetectionRunEntity, UUID> {

    List<AiSemanticDuplicateDetectionRunEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    Optional<AiSemanticDuplicateDetectionRunEntity> findFirstByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
