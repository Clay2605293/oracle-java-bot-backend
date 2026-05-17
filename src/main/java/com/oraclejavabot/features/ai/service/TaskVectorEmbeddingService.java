package com.oraclejavabot.features.ai.service;

import com.oraclejavabot.features.tasks.model.TaskEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TaskVectorEmbeddingService {

    private static final String EMBEDDING_MODEL = "MULTILINGUAL_E5_BASE";

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void upsertTaskVectorEmbedding(TaskEntity task) {
        if (task == null || task.getTaskId() == null || task.getProjectId() == null) {
            return;
        }

        if (task.getTitulo() == null || task.getTitulo().isBlank()) {
            return;
        }

        String sql = """
            MERGE INTO CHATBOT_USER.TASK_VECTOR_EMBEDDING v
            USING (
                SELECT
                    :taskId AS TASK_ID,
                    :projectId AS PROJECT_ID,
                    SUBSTR(
                        CASE
                            WHEN :descriptionText IS NULL OR TRIM(:descriptionText) IS NULL THEN
                                'Título: ' || :titleText
                            ELSE
                                'Título: ' || :titleText || CHR(10) || 'Descripción: ' || :descriptionText
                        END,
                        1,
                        1000
                    ) AS EMBEDDING_TEXT,
                    VECTOR_EMBEDDING(
                        MULTILINGUAL_E5_BASE
                        USING
                            CASE
                                WHEN :descriptionText IS NULL OR TRIM(:descriptionText) IS NULL THEN
                                    'Título: ' || :titleText
                                ELSE
                                    'Título: ' || :titleText || CHR(10) || 'Descripción: ' || :descriptionText
                            END
                        AS DATA
                    ) AS EMBEDDING
                FROM dual
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

        entityManager.createNativeQuery(sql)
                .setParameter("taskId", task.getTaskId())
                .setParameter("projectId", task.getProjectId())
                .setParameter("titleText", task.getTitulo())
                .setParameter("descriptionText", task.getDescripcion())
                .setParameter("embeddingModel", EMBEDDING_MODEL)
                .executeUpdate();
    }
}