package com.oraclejavabot.features.kpis.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.UUID;

public interface SprintKpiRepository extends Repository<Object, UUID> {

    @Query(value = """
        SELECT
            COUNT(*) total_tareas,
            SUM(CASE WHEN estado_id = 3 THEN 1 ELSE 0 END) tareas_completadas,
            SUM(CASE WHEN estado_id = 3 AND fecha_finalizacion <= fecha_limite THEN 1 ELSE 0 END) a_tiempo,
            SUM(CASE WHEN estado_id = 3 AND fecha_finalizacion > fecha_limite THEN 1 ELSE 0 END) con_retraso,
            NVL(SUM(tiempo_estimado),0) total_estimado_hrs,
            NVL(SUM(tiempo_real),0) total_real_hrs
        FROM tarea
        WHERE sprint_id = :sprintId
        """,
        nativeQuery = true)
    Map<String, Object> getSprintKpis(@Param("sprintId") UUID sprintId);
}