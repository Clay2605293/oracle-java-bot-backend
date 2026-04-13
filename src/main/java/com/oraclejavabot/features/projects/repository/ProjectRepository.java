package com.oraclejavabot.features.projects.repository;

import com.oraclejavabot.features.projects.model.ProjectEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    @Query(value = """
        SELECT progreso
        FROM proyecto
        WHERE project_id = HEXTORAW(:projectId)
    """, nativeQuery = true)
    Object getProjectProgress(@Param("projectId") String projectId);
}