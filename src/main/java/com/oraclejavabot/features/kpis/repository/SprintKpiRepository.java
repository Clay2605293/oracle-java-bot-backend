package com.oraclejavabot.features.kpis.repository;

import com.oraclejavabot.features.tasks.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SprintKpiRepository extends JpaRepository<TaskEntity, UUID> {

    @Query(value = """
        SELECT
            COUNT(*) AS total_tareas,
            SUM(CASE WHEN estado_id = 3 THEN 1 ELSE 0 END) AS tareas_completadas,
            SUM(CASE WHEN estado_id = 3 AND fecha_finalizacion <= fecha_limite THEN 1 ELSE 0 END) AS a_tiempo,
            SUM(CASE WHEN estado_id = 3 AND fecha_finalizacion > fecha_limite THEN 1 ELSE 0 END) AS con_retraso,
            NVL(SUM(tiempo_estimado),0) AS total_estimado_hrs,
            NVL(SUM(tiempo_real),0) AS total_real_hrs
        FROM tarea
        WHERE sprint_id = HEXTORAW(:sprintId)
    """, nativeQuery = true)
    List<Object[]> getSprintKpis(@Param("sprintId") String sprintId);
}