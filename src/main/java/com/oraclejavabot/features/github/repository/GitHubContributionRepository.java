package com.oraclejavabot.features.github.repository;

import com.oraclejavabot.features.github.dto.GitHubContributionDTO;
import com.oraclejavabot.features.github.dto.GitHubSprintActivityDTO;
import com.oraclejavabot.features.github.dto.GitHubRepositoryActivityDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GitHubContributionRepository {

  @PersistenceContext
  private EntityManager entityManager;

  public List<GitHubContributionDTO> findContributionsByProjectId(String projectIdHex) {
    String sql = """
            SELECT
                RAWTOHEX(u.USER_ID) AS USER_ID,
                u.PRIMER_NOMBRE || ' ' || u.APELLIDO AS NAME,
                u.EMAIL AS EMAIL,
                u.GITHUB_USERNAME AS GITHUB_USERNAME,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_COMMIT gc
                    WHERE gc.PROJECT_ID = HEXTORAW(:projectId)
                      AND LOWER(gc.AUTHOR_USERNAME) = LOWER(u.GITHUB_USERNAME)
                ) AS TOTAL_COMMITS,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_ISSUE gi
                    WHERE gi.PROJECT_ID = HEXTORAW(:projectId)
                      AND LOWER(gi.AUTHOR_USERNAME) = LOWER(u.GITHUB_USERNAME)
                ) AS OPENED_ISSUES,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_ISSUE gi
                    WHERE gi.PROJECT_ID = HEXTORAW(:projectId)
                      AND LOWER(gi.CLOSED_BY_USERNAME) = LOWER(u.GITHUB_USERNAME)
                ) AS CLOSED_ISSUES

            FROM PROYECTO p
            JOIN USUARIO_A_EQUIPO ue
              ON ue.TEAM_ID = p.TEAM_ID
            JOIN USUARIO u
              ON u.USER_ID = ue.USER_ID
            WHERE p.PROJECT_ID = HEXTORAW(:projectId)
              AND u.GITHUB_USERNAME IS NOT NULL
            ORDER BY TOTAL_COMMITS DESC, OPENED_ISSUES DESC, CLOSED_ISSUES DESC, NAME ASC
        """;

    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("projectId", projectIdHex);

    List<Object[]> rows = query.getResultList();
    List<GitHubContributionDTO> result = new ArrayList<>();

    for (Object[] row : rows) {
      result.add(new GitHubContributionDTO(
          row[0] != null ? row[0].toString() : null,
          row[1] != null ? row[1].toString() : null,
          row[2] != null ? row[2].toString() : null,
          row[3] != null ? row[3].toString() : null,
          row[4] != null ? ((Number) row[4]).longValue() : 0,
          row[5] != null ? ((Number) row[5]).longValue() : 0,
          row[6] != null ? ((Number) row[6]).longValue() : 0));
    }

    return result;
  }
  
  public List<GitHubSprintActivityDTO> findSprintActivityByProjectId(String projectIdHex) {
    String sql = """
            SELECT
                RAWTOHEX(s.SPRINT_ID) AS SPRINT_ID,
                s.NOMBRE AS SPRINT_NAME,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_COMMIT gc
                    WHERE gc.PROJECT_ID = s.PROJECT_ID
                      AND gc.COMMIT_DATE >= s.FECHA_INICIO
                      AND gc.COMMIT_DATE <= s.FECHA_FIN
                ) AS TOTAL_COMMITS,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_ISSUE gi
                    WHERE gi.PROJECT_ID = s.PROJECT_ID
                      AND gi.CREATED_AT_GITHUB >= s.FECHA_INICIO
                      AND gi.CREATED_AT_GITHUB <= s.FECHA_FIN
                ) AS OPENED_ISSUES,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_ISSUE gi
                    WHERE gi.PROJECT_ID = s.PROJECT_ID
                      AND gi.CLOSED_AT_GITHUB IS NOT NULL
                      AND gi.CLOSED_AT_GITHUB >= s.FECHA_INICIO
                      AND gi.CLOSED_AT_GITHUB <= s.FECHA_FIN
                ) AS CLOSED_ISSUES

            FROM SPRINT s
            WHERE s.PROJECT_ID = HEXTORAW(:projectId)
            ORDER BY s.FECHA_INICIO
        """;

    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("projectId", projectIdHex);

    List<Object[]> rows = query.getResultList();
    List<GitHubSprintActivityDTO> result = new ArrayList<>();

    for (Object[] row : rows) {
      result.add(new GitHubSprintActivityDTO(
          row[0] != null ? row[0].toString() : null,
          row[1] != null ? row[1].toString() : null,
          row[2] != null ? ((Number) row[2]).longValue() : 0,
          row[3] != null ? ((Number) row[3]).longValue() : 0,
          row[4] != null ? ((Number) row[4]).longValue() : 0));
    }

    return result;
  }
  
  public List<GitHubRepositoryActivityDTO> findRepositoryActivityByProjectId(String projectIdHex) {
    String sql = """
            SELECT
                RAWTOHEX(pr.REPOSITORY_ID) AS REPOSITORY_ID,
                pr.OWNER AS OWNER,
                pr.REPO_NAME AS REPO_NAME,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_COMMIT gc
                    WHERE gc.REPOSITORY_ID = pr.REPOSITORY_ID
                ) AS TOTAL_COMMITS,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_ISSUE gi
                    WHERE gi.REPOSITORY_ID = pr.REPOSITORY_ID
                ) AS OPENED_ISSUES,

                (
                    SELECT COUNT(*)
                    FROM GITHUB_ISSUE gi
                    WHERE gi.REPOSITORY_ID = pr.REPOSITORY_ID
                      AND gi.STATE = 'closed'
                ) AS CLOSED_ISSUES

            FROM PROJECT_REPOSITORY pr
            WHERE pr.PROJECT_ID = HEXTORAW(:projectId)
              AND pr.IS_ACTIVE = 1
            ORDER BY pr.REPO_NAME
        """;

    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("projectId", projectIdHex);

    List<Object[]> rows = query.getResultList();
    List<GitHubRepositoryActivityDTO> result = new ArrayList<>();

    for (Object[] row : rows) {
      result.add(new GitHubRepositoryActivityDTO(
          row[0] != null ? row[0].toString() : null,
          row[1] != null ? row[1].toString() : null,
          row[2] != null ? row[2].toString() : null,
          row[3] != null ? ((Number) row[3]).longValue() : 0,
          row[4] != null ? ((Number) row[4]).longValue() : 0,
          row[5] != null ? ((Number) row[5]).longValue() : 0));
    }

    return result;
  }
}