package com.oraclejavabot.features.tasks.repository;

import com.oraclejavabot.features.tasks.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findByProjectId(UUID projectId);

    List<TaskEntity> findBySprintId(UUID sprintId);

    @Query(value = """
        SELECT COUNT(*)
        FROM SPRINT
        WHERE SPRINT_ID = HEXTORAW(:sprintId)
          AND PROJECT_ID = HEXTORAW(:projectId)
        """, nativeQuery = true)
    int validateSprintInProject(
            @Param("sprintId") String sprintId,
            @Param("projectId") String projectId
    );

    @Query(value = """
        SELECT t.*
        FROM TAREA t
        JOIN USUARIO_A_TAREA ut ON ut.TASK_ID = t.TASK_ID
        WHERE ut.USER_ID = HEXTORAW(:userId)
          AND t.PROJECT_ID = HEXTORAW(:projectId)
        ORDER BY t.FECHA_CREACION DESC
        """, nativeQuery = true)
    List<TaskEntity> findAssignedTasksByUserAndProject(
            @Param("userId") String userId,
            @Param("projectId") String projectId
    );
}