package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.ai.dto.TaskVectorEmbeddingBackfillResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TaskVectorEmbeddingBackfillService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public TaskVectorEmbeddingBackfillResponseDTO backfillProjectTaskVectorEmbeddings(String projectId) {

        UUID projectUuid = parseProjectId(projectId);

        // 🔹 MERGE en Oracle
        String mergeSql = """
            MERGE INTO CHATBOT_USER.TASK_VECTOR_EMBEDDING v
            USING (
                SELECT
                    TASK_ID,
                    PROJECT_ID,
                    TO_VECTOR(EMBEDDING_JSON) AS EMBEDDING,
                    EMBEDDING_TEXT,
                    EMBEDDING_MODEL
                FROM CHATBOT_USER.TASK_SEMANTIC_EMBEDDING
                WHERE PROJECT_ID = :projectId
            ) s
            ON (v.TASK_ID = s.TASK_ID)
            WHEN MATCHED THEN UPDATE SET
                v.PROJECT_ID = s.PROJECT_ID,
                v.EMBEDDING = s.EMBEDDING,
                v.EMBEDDING_TEXT = s.EMBEDDING_TEXT,
                v.EMBEDDING_MODEL = s.EMBEDDING_MODEL,
                v.UPDATED_AT = SYSTIMESTAMP
            WHEN NOT MATCHED THEN INSERT (
                TASK_ID,
                PROJECT_ID,
                EMBEDDING,
                EMBEDDING_TEXT,
                EMBEDDING_MODEL,
                CREATED_AT,
                UPDATED_AT
            ) VALUES (
                s.TASK_ID,
                s.PROJECT_ID,
                s.EMBEDDING,
                s.EMBEDDING_TEXT,
                s.EMBEDDING_MODEL,
                SYSTIMESTAMP,
                SYSTIMESTAMP
            )
        """;

        entityManager.createNativeQuery(mergeSql)
                .setParameter("projectId", projectUuid)
                .executeUpdate();

        // 🔹 Contar embeddings
        String countSql = """
            SELECT COUNT(*)
            FROM CHATBOT_USER.TASK_VECTOR_EMBEDDING
            WHERE PROJECT_ID = :projectId
        """;

        Object result = entityManager.createNativeQuery(countSql)
                .setParameter("projectId", projectUuid)
                .getSingleResult();

        int total = (result instanceof BigDecimal bd)
                ? bd.intValue()
                : ((Number) result).intValue();

        return new TaskVectorEmbeddingBackfillResponseDTO(
                "Task vector embedding backfill completed",
                projectId,
                total
        );
    }

    private UUID parseProjectId(String projectId) {
        try {
            return UUID.fromString(projectId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid projectId format");
        }
    }
}