package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.TaskEmbeddingStatusResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TaskEmbeddingStatusService {

    @PersistenceContext
    private EntityManager entityManager;

    public TaskEmbeddingStatusResponseDTO getStatus(String projectId) {

        UUID projectUuid = parseProjectId(projectId);

        int totalTasks = count("""
            SELECT COUNT(*)
            FROM CHATBOT_USER.TAREA
            WHERE PROJECT_ID = :projectId
        """, projectUuid);

        int semanticEmbeddings = count("""
            SELECT COUNT(*)
            FROM CHATBOT_USER.TASK_SEMANTIC_EMBEDDING
            WHERE PROJECT_ID = :projectId
        """, projectUuid);

        int vectorEmbeddings = count("""
            SELECT COUNT(*)
            FROM CHATBOT_USER.TASK_VECTOR_EMBEDDING
            WHERE PROJECT_ID = :projectId
        """, projectUuid);

        boolean ready = semanticEmbeddings >= totalTasks && totalTasks > 0;

        return new TaskEmbeddingStatusResponseDTO(
                projectId,
                totalTasks,
                semanticEmbeddings,
                vectorEmbeddings,
                ready
        );
    }

    private int count(String sql, UUID projectId) {
        Object result = entityManager.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .getSingleResult();

        if (result instanceof BigDecimal bd) {
            return bd.intValue();
        }

        return ((Number) result).intValue();
    }

    private UUID parseProjectId(String projectId) {
        try {
            return UUID.fromString(projectId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid projectId format");
        }
    }
}