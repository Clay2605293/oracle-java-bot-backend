package com.oraclejavabot.features.kpis.repository;

import com.oraclejavabot.features.tasks.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DeveloperPerformanceRepository extends JpaRepository<TaskEntity, UUID> {
    // =====================================
    // Query 1 - Rendimiento global
    // =====================================

    @Query(value = """
        SELECT
            RAWTOHEX(u.USER_ID) AS userId,
            u.PRIMER_NOMBRE || ' ' || u.APELLIDO AS nombre,
            COUNT(t.TASK_ID) AS asignadas,
            SUM(CASE WHEN t.ESTADO_ID = 3 THEN 1 ELSE 0 END) AS completadas,
            CASE 
                WHEN COUNT(t.TASK_ID) = 0 THEN 0
                ELSE ROUND(
                    (SUM(CASE WHEN t.ESTADO_ID = 3 THEN 1 ELSE 0 END) / COUNT(t.TASK_ID)) * 100,
                    2
                )
            END AS porcentajeCompletadas
        FROM PROYECTO p
        JOIN EQUIPO e ON e.TEAM_ID = p.TEAM_ID
        JOIN USUARIO_A_EQUIPO ue ON ue.TEAM_ID = e.TEAM_ID
        JOIN USUARIO u ON u.USER_ID = ue.USER_ID
        LEFT JOIN USUARIO_A_TAREA uat ON uat.USER_ID = u.USER_ID
        LEFT JOIN TAREA t ON t.TASK_ID = uat.TASK_ID
            AND t.PROJECT_ID = p.PROJECT_ID
        WHERE p.PROJECT_ID = HEXTORAW(:projectId)
        GROUP BY u.USER_ID, u.PRIMER_NOMBRE, u.APELLIDO
        """, nativeQuery = true)
    List<Object[]> getGlobalPerformance(@Param("projectId") String projectId);



    // =====================================
    // Query 2 - Histórico por sprint
    // =====================================

    @Query(value = """
        SELECT
            RAWTOHEX(u.USER_ID) AS userId,
            RAWTOHEX(s.SPRINT_ID) AS sprintId,
            s.NOMBRE AS sprintNombre,
            SUM(CASE WHEN t.ESTADO_ID = 3 THEN 1 ELSE 0 END) AS tareasTerminadas,
            SUM(CASE WHEN t.ESTADO_ID = 3 THEN NVL(t.TIEMPO_REAL,0) ELSE 0 END) AS horasReales
        FROM PROYECTO p
        JOIN EQUIPO e ON e.TEAM_ID = p.TEAM_ID
        JOIN USUARIO_A_EQUIPO ue ON ue.TEAM_ID = e.TEAM_ID
        JOIN USUARIO u ON u.USER_ID = ue.USER_ID
        LEFT JOIN USUARIO_A_TAREA uat ON uat.USER_ID = u.USER_ID
        LEFT JOIN TAREA t ON t.TASK_ID = uat.TASK_ID
            AND t.PROJECT_ID = p.PROJECT_ID
        LEFT JOIN SPRINT s ON s.SPRINT_ID = t.SPRINT_ID
        WHERE p.PROJECT_ID = HEXTORAW(:projectId)
        GROUP BY
            u.USER_ID,
            s.SPRINT_ID,
            s.NOMBRE
        ORDER BY
            u.USER_ID,
            s.SPRINT_ID
        """, nativeQuery = true)
    List<Object[]> getSprintPerformance(@Param("projectId") String projectId);

}