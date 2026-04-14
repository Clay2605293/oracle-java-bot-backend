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
}