package com.oraclejavabot.features.tasks.repository;

import com.oraclejavabot.features.tasks.model.TaskUserEntity;
import com.oraclejavabot.features.tasks.model.TaskUserId;
import com.oraclejavabot.features.tasks.dto.TaskAssignmentDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserRepository extends JpaRepository<TaskUserEntity, TaskUserId> {

    List<TaskUserEntity> findByIdTaskId(UUID taskId);

    boolean existsById(TaskUserId id);

    @Query(value = """
        SELECT
            RAWTOHEX(t.TASK_ID) AS taskId,
            t.TITULO AS titulo,
            RAWTOHEX(u.USER_ID) AS developerId,
            u.PRIMER_NOMBRE || ' ' || u.APELLIDO AS developerNombre,
            RAWTOHEX(t.SPRINT_ID) AS sprintId,
            t.TIEMPO_ESTIMADO AS tiempoEstimado,
            t.TIEMPO_REAL AS tiempoReal,
            t.ESTADO_ID AS estadoId,
            t.PRIORIDAD_ID AS prioridadId
        FROM USUARIO_A_TAREA uat
        JOIN TAREA t ON t.TASK_ID = uat.TASK_ID
        JOIN USUARIO u ON u.USER_ID = uat.USER_ID
        WHERE uat.USER_ID IN (:developerIds)
        AND t.SPRINT_ID IN (:sprintIds)
        ORDER BY u.PRIMER_NOMBRE, u.APELLIDO, t.FECHA_LIMITE
        """, nativeQuery = true)
    List<Object[]> findAssignmentsByDevelopersAndSprintsRaw(
            @Param("developerIds") List<UUID> developerIds,
            @Param("sprintIds") List<UUID> sprintIds
    );
}