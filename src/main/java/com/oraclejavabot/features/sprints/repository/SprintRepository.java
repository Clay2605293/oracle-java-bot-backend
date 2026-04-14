package com.oraclejavabot.features.sprints.repository;

import com.oraclejavabot.features.sprints.model.SprintEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SprintRepository extends JpaRepository<SprintEntity, UUID> {

    // =============================
    // GET sprints por proyecto
    // =============================
    List<SprintEntity> findByProjectId(UUID projectId);

    // =============================
    // VALIDACIÓN: overlap de fechas
    // =============================
    @Query(value = """
        SELECT COUNT(*)
        FROM SPRINT
        WHERE PROJECT_ID = HEXTORAW(:projectId)
        AND (
            (:fechaInicio BETWEEN FECHA_INICIO AND FECHA_FIN)
            OR
            (:fechaFin BETWEEN FECHA_INICIO AND FECHA_FIN)
            OR
            (FECHA_INICIO BETWEEN :fechaInicio AND :fechaFin)
        )
    """, nativeQuery = true)
    int countOverlappingSprints(
            @Param("projectId") String projectId,
            @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
            @Param("fechaFin") java.time.LocalDateTime fechaFin
    );

    // =============================
    // VALIDACIÓN: tareas asociadas
    // =============================
    @Query(value = """
        SELECT COUNT(*)
        FROM TAREA
        WHERE SPRINT_ID = HEXTORAW(:sprintId)
    """, nativeQuery = true)
    int countTasksBySprint(@Param("sprintId") String sprintId);
}