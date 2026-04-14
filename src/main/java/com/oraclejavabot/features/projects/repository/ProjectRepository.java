package com.oraclejavabot.features.projects.repository;

import com.oraclejavabot.features.projects.model.ProjectEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    @Query(value = """
        SELECT progreso
        FROM proyecto
        WHERE project_id = HEXTORAW(:projectId)
    """, nativeQuery = true)
    Object getProjectProgress(@Param("projectId") String projectId);

    List<ProjectEntity> findByTeamId(UUID teamId);

    @Query(value = """
        SELECT COUNT(*)
        FROM USUARIO_A_PROYECTO
        WHERE PROJECT_ID = HEXTORAW(:projectId)
    """, nativeQuery = true)
    int countProjectMembers(@Param("projectId") String projectId);

    @Query(value = """
        SELECT COUNT(*)
        FROM USUARIO_A_EQUIPO uae
        JOIN PROYECTO p ON p.TEAM_ID = uae.TEAM_ID
        WHERE p.PROJECT_ID = HEXTORAW(:projectId)
        AND uae.USER_ID = HEXTORAW(:userId)
    """, nativeQuery = true)
    int existsUserInProjectTeam(@Param("projectId") String projectId,
                                @Param("userId") String userId);
}