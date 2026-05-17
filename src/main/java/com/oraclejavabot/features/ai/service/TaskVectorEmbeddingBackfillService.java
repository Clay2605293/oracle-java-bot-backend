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

    private static final String EMBEDDING_MODEL = "MULTILINGUAL_E5_BASE";

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public TaskVectorEmbeddingBackfillResponseDTO backfillProjectTaskVectorEmbeddings(String projectId) {

        UUID projectUuid = parseProjectId(projectId);

        String mergeSql = """
            MERGE INTO CHATBOT_USER.TASK_VECTOR_EMBEDDING v
            USING (
                SELECT
                    t.TASK_ID,
                    t.PROJECT_ID,
                    SUBSTR(
                        CASE
                            WHEN t.DESCRIPCION IS NULL OR TRIM(t.DESCRIPCION) IS NULL THEN
                                'Título: ' || t.TITULO
                            ELSE
                                'Título: ' || t.TITULO || CHR(10) || 'Descripción: ' || t.DESCRIPCION
                        END,
                        1,
                        1000
                    ) AS EMBEDDING_TEXT,
                    VECTOR_EMBEDDING(
                        MULTILINGUAL_E5_BASE
                        USING
                            CASE
                                WHEN t.DESCRIPCION IS NULL OR TRIM(t.DESCRIPCION) IS NULL THEN
                                    'Título: ' || t.TITULO
                                ELSE
                                    'Título: ' || t.TITULO || CHR(10) || 'Descripción: ' || t.DESCRIPCION
                            END
                        AS DATA
                    ) AS EMBEDDING
                FROM CHATBOT_USER.TAREA t
                WHERE t.PROJECT_ID = :projectId
                  AND t.TITULO IS NOT NULL
            ) s
            ON (v.TASK_ID = s.TASK_ID)
            WHEN MATCHED THEN UPDATE SET
                v.PROJECT_ID = s.PROJECT_ID,
                v.EMBEDDING = s.EMBEDDING,
                v.EMBEDDING_TEXT = s.EMBEDDING_TEXT,
                v.EMBEDDING_MODEL = :embeddingModel,
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
                :embeddingModel,
                SYSTIMESTAMP,
                SYSTIMESTAMP
            )
        """;

        entityManager.createNativeQuery(mergeSql)
                .setParameter("projectId", projectUuid)
                .setParameter("embeddingModel", EMBEDDING_MODEL)
                .executeUpdate();

        String countSql = """
            SELECT COUNT(*)
            FROM CHATBOT_USER.TASK_VECTOR_EMBEDDING
            WHERE PROJECT_ID = :projectId
              AND EMBEDDING_MODEL = :embeddingModel
        """;

        Object result = entityManager.createNativeQuery(countSql)
                .setParameter("projectId", projectUuid)
                .setParameter("embeddingModel", EMBEDDING_MODEL)
                .getSingleResult();

        int total = (result instanceof BigDecimal bd)
                ? bd.intValue()
                : ((Number) result).intValue();

        return new TaskVectorEmbeddingBackfillResponseDTO(
                "Task vector embedding backfill completed using Oracle ONNX model",
                projectId,
                total
        );
    }

    private UUID parseProjectId(String projectId) {
        return parseId(projectId, "ProjectId inválido");
    }

    private UUID parseId(String value, String errorMessage) {
        try {
            if (value.contains("-")) {
                return UUID.fromString(value);
            }

            return hexToUuid(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private UUID hexToUuid(String hex) {
        return UUID.fromString(
                hex.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"
                )
        );
    }
}