package com.oraclejavabot.features.kpis.repository;

import com.oraclejavabot.features.kpis.dto.DashboardDeveloperOptionDTO;
import com.oraclejavabot.features.kpis.dto.DashboardSprintHistoryPointDTO;
import com.oraclejavabot.features.kpis.dto.DashboardSprintOptionDTO;
import com.oraclejavabot.features.kpis.dto.DashboardSummaryDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectDashboardKpiRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectDashboardKpiRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DashboardSprintOptionDTO> findSprintOptions(String projectId) {
        String sql = """
            SELECT
                RAWTOHEX(s."SPRINT_ID") AS "SPRINT_ID",
                s."NOMBRE" AS "SPRINT_NAME",
                TO_CHAR(s."FECHA_INICIO", 'YYYY-MM-DD"T"HH24:MI:SS') AS "START_DATE",
                TO_CHAR(s."FECHA_FIN", 'YYYY-MM-DD"T"HH24:MI:SS') AS "END_DATE"
            FROM "CHATBOT_USER"."SPRINT" s
            WHERE s."PROJECT_ID" = HEXTORAW(?)
            ORDER BY s."FECHA_INICIO", s."NOMBRE"
            """;

        return jdbcTemplate.query(
                sql,
                this::mapSprintOption,
                toOracleRawHex(projectId)
        );
    }

    public List<DashboardDeveloperOptionDTO> findDeveloperOptions(String projectId) {
        String sql = """
            SELECT
                RAWTOHEX(u."USER_ID") AS "USER_ID",
                u."PRIMER_NOMBRE" || ' ' || u."APELLIDO" AS "NAME",
                u."EMAIL" AS "EMAIL"
            FROM "CHATBOT_USER"."PROYECTO" p
            JOIN "CHATBOT_USER"."EQUIPO" e
                ON e."TEAM_ID" = p."TEAM_ID"
            JOIN "CHATBOT_USER"."USUARIO_A_EQUIPO" ue
                ON ue."TEAM_ID" = e."TEAM_ID"
            JOIN "CHATBOT_USER"."USUARIO" u
                ON u."USER_ID" = ue."USER_ID"
            WHERE p."PROJECT_ID" = HEXTORAW(?)
              AND u."ESTADO_ID" = 1
              AND u."ROL_ID" = 2
            ORDER BY u."PRIMER_NOMBRE", u."APELLIDO"
            """;

        return jdbcTemplate.query(
                sql,
                this::mapDeveloperOption,
                toOracleRawHex(projectId)
        );
    }

    public DashboardSummaryDTO calculateSummary(
            String projectId,
            String sprintId,
            String developerId,
            int developerCount
    ) {
        List<Object> params = new ArrayList<>();
        params.add(toOracleRawHex(projectId));

        StringBuilder sql = new StringBuilder();

        sql.append("""
            SELECT
                COUNT(DISTINCT t."TASK_ID") AS "TOTAL_TASKS",
                COUNT(DISTINCT CASE WHEN t."ESTADO_ID" = 3 THEN t."TASK_ID" END) AS "COMPLETED_TASKS",
                COUNT(DISTINCT CASE
                    WHEN t."ESTADO_ID" = 3
                     AND t."FECHA_FINALIZACION" IS NOT NULL
                     AND t."FECHA_FINALIZACION" <= t."FECHA_LIMITE"
                    THEN t."TASK_ID"
                END) AS "ON_TIME_TASKS",
                COUNT(DISTINCT CASE
                    WHEN t."ESTADO_ID" = 3
                     AND t."FECHA_FINALIZACION" IS NOT NULL
                     AND t."FECHA_FINALIZACION" > t."FECHA_LIMITE"
                    THEN t."TASK_ID"
                END) AS "DELAYED_TASKS",
                NVL(SUM(t."TIEMPO_ESTIMADO"), 0) AS "TOTAL_ESTIMATED_HOURS",
                NVL(SUM(t."TIEMPO_REAL"), 0) AS "TOTAL_REAL_HOURS"
            FROM "CHATBOT_USER"."TAREA" t
            """);

        if (developerId != null) {
            sql.append("""
                JOIN "CHATBOT_USER"."USUARIO_A_TAREA" uat
                    ON uat."TASK_ID" = t."TASK_ID"
                """);
        }

        sql.append("""
            WHERE t."PROJECT_ID" = HEXTORAW(?)
            """);

        if (sprintId != null) {
            sql.append("""
              AND t."SPRINT_ID" = HEXTORAW(?)
            """);
            params.add(toOracleRawHex(sprintId));
        }

        if (developerId != null) {
            sql.append("""
              AND uat."USER_ID" = HEXTORAW(?)
            """);
            params.add(toOracleRawHex(developerId));
        }

        return jdbcTemplate.queryForObject(
                sql.toString(),
                (rs, rowNum) -> mapSummary(rs, developerCount),
                params.toArray()
        );
    }

    public List<DashboardSprintHistoryPointDTO> findSprintHistory(
            String projectId,
            String developerId
    ) {
        if (developerId == null) {
            return findTeamSprintHistory(projectId);
        }

        return findDeveloperSprintHistory(projectId, developerId);
    }

    private List<DashboardSprintHistoryPointDTO> findTeamSprintHistory(String projectId) {
        String sql = """
            SELECT
                RAWTOHEX(s."SPRINT_ID") AS "SPRINT_ID",
                s."NOMBRE" AS "SPRINT_NAME",
                COUNT(DISTINCT t."TASK_ID") AS "TOTAL_TASKS",
                COUNT(DISTINCT CASE WHEN t."ESTADO_ID" = 3 THEN t."TASK_ID" END) AS "COMPLETED_TASKS",
                COUNT(DISTINCT CASE
                    WHEN t."ESTADO_ID" = 3
                     AND t."FECHA_FINALIZACION" IS NOT NULL
                     AND t."FECHA_FINALIZACION" <= t."FECHA_LIMITE"
                    THEN t."TASK_ID"
                END) AS "ON_TIME_TASKS",
                COUNT(DISTINCT CASE
                    WHEN t."ESTADO_ID" = 3
                     AND t."FECHA_FINALIZACION" IS NOT NULL
                     AND t."FECHA_FINALIZACION" > t."FECHA_LIMITE"
                    THEN t."TASK_ID"
                END) AS "DELAYED_TASKS",
                NVL(SUM(t."TIEMPO_ESTIMADO"), 0) AS "TOTAL_ESTIMATED_HOURS",
                NVL(SUM(t."TIEMPO_REAL"), 0) AS "TOTAL_REAL_HOURS"
            FROM "CHATBOT_USER"."SPRINT" s
            LEFT JOIN "CHATBOT_USER"."TAREA" t
                ON t."SPRINT_ID" = s."SPRINT_ID"
               AND t."PROJECT_ID" = HEXTORAW(?)
            WHERE s."PROJECT_ID" = HEXTORAW(?)
            GROUP BY s."SPRINT_ID", s."NOMBRE", s."FECHA_INICIO"
            ORDER BY s."FECHA_INICIO", s."NOMBRE"
            """;

        String projectHex = toOracleRawHex(projectId);

        return jdbcTemplate.query(
                sql,
                this::mapSprintHistoryPoint,
                projectHex,
                projectHex
        );
    }

    private List<DashboardSprintHistoryPointDTO> findDeveloperSprintHistory(
            String projectId,
            String developerId
    ) {
        String sql = """
            SELECT
                RAWTOHEX(s."SPRINT_ID") AS "SPRINT_ID",
                s."NOMBRE" AS "SPRINT_NAME",
                COUNT(DISTINCT t."TASK_ID") AS "TOTAL_TASKS",
                COUNT(DISTINCT CASE WHEN t."ESTADO_ID" = 3 THEN t."TASK_ID" END) AS "COMPLETED_TASKS",
                COUNT(DISTINCT CASE
                    WHEN t."ESTADO_ID" = 3
                     AND t."FECHA_FINALIZACION" IS NOT NULL
                     AND t."FECHA_FINALIZACION" <= t."FECHA_LIMITE"
                    THEN t."TASK_ID"
                END) AS "ON_TIME_TASKS",
                COUNT(DISTINCT CASE
                    WHEN t."ESTADO_ID" = 3
                     AND t."FECHA_FINALIZACION" IS NOT NULL
                     AND t."FECHA_FINALIZACION" > t."FECHA_LIMITE"
                    THEN t."TASK_ID"
                END) AS "DELAYED_TASKS",
                NVL(SUM(t."TIEMPO_ESTIMADO"), 0) AS "TOTAL_ESTIMATED_HOURS",
                NVL(SUM(t."TIEMPO_REAL"), 0) AS "TOTAL_REAL_HOURS"
            FROM "CHATBOT_USER"."SPRINT" s
            LEFT JOIN (
                SELECT t_inner.*
                FROM "CHATBOT_USER"."TAREA" t_inner
                JOIN "CHATBOT_USER"."USUARIO_A_TAREA" uat_inner
                    ON uat_inner."TASK_ID" = t_inner."TASK_ID"
                WHERE t_inner."PROJECT_ID" = HEXTORAW(?)
                  AND uat_inner."USER_ID" = HEXTORAW(?)
            ) t
                ON t."SPRINT_ID" = s."SPRINT_ID"
            WHERE s."PROJECT_ID" = HEXTORAW(?)
            GROUP BY s."SPRINT_ID", s."NOMBRE", s."FECHA_INICIO"
            ORDER BY s."FECHA_INICIO", s."NOMBRE"
            """;

        return jdbcTemplate.query(
                sql,
                this::mapSprintHistoryPoint,
                toOracleRawHex(projectId),
                toOracleRawHex(developerId),
                toOracleRawHex(projectId)
        );
    }

    public String findSprintName(String sprintId) {
        if (sprintId == null) {
            return null;
        }

        String sql = """
            SELECT s."NOMBRE"
            FROM "CHATBOT_USER"."SPRINT" s
            WHERE s."SPRINT_ID" = HEXTORAW(?)
            """;

        List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("NOMBRE"),
                toOracleRawHex(sprintId)
        );

        return result.isEmpty() ? null : result.get(0);
    }

    public String findDeveloperName(String developerId) {
        if (developerId == null) {
            return null;
        }

        String sql = """
            SELECT u."PRIMER_NOMBRE" || ' ' || u."APELLIDO" AS "NAME"
            FROM "CHATBOT_USER"."USUARIO" u
            WHERE u."USER_ID" = HEXTORAW(?)
            """;

        List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("NAME"),
                toOracleRawHex(developerId)
        );

        return result.isEmpty() ? null : result.get(0);
    }

    public int countActiveDevelopersInProjectTeam(String projectId) {
        String sql = """
            SELECT COUNT(*)
            FROM "CHATBOT_USER"."PROYECTO" p
            JOIN "CHATBOT_USER"."EQUIPO" e
                ON e."TEAM_ID" = p."TEAM_ID"
            JOIN "CHATBOT_USER"."USUARIO_A_EQUIPO" ue
                ON ue."TEAM_ID" = e."TEAM_ID"
            JOIN "CHATBOT_USER"."USUARIO" u
                ON u."USER_ID" = ue."USER_ID"
            WHERE p."PROJECT_ID" = HEXTORAW(?)
              AND u."ESTADO_ID" = 1
              AND u."ROL_ID" = 2
            """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                toOracleRawHex(projectId)
        );

        return count == null ? 0 : count;
    }

    private DashboardSprintOptionDTO mapSprintOption(ResultSet rs, int rowNum) throws SQLException {
        return new DashboardSprintOptionDTO(
                rs.getString("SPRINT_ID"),
                rs.getString("SPRINT_NAME"),
                rs.getString("START_DATE"),
                rs.getString("END_DATE")
        );
    }

    private DashboardDeveloperOptionDTO mapDeveloperOption(ResultSet rs, int rowNum) throws SQLException {
        return new DashboardDeveloperOptionDTO(
                rs.getString("USER_ID"),
                rs.getString("NAME"),
                rs.getString("EMAIL")
        );
    }

    private DashboardSprintHistoryPointDTO mapSprintHistoryPoint(ResultSet rs, int rowNum) throws SQLException {
        return new DashboardSprintHistoryPointDTO(
                rs.getString("SPRINT_ID"),
                rs.getString("SPRINT_NAME"),
                getInt(rs, "TOTAL_TASKS"),
                getInt(rs, "COMPLETED_TASKS"),
                getInt(rs, "ON_TIME_TASKS"),
                getInt(rs, "DELAYED_TASKS"),
                getDouble(rs, "TOTAL_ESTIMATED_HOURS"),
                getDouble(rs, "TOTAL_REAL_HOURS")
        );
    }

    private DashboardSummaryDTO mapSummary(ResultSet rs, int developerCount) throws SQLException {
        int totalTasks = getInt(rs, "TOTAL_TASKS");
        int completedTasks = getInt(rs, "COMPLETED_TASKS");
        int onTimeTasks = getInt(rs, "ON_TIME_TASKS");
        int delayedTasks = getInt(rs, "DELAYED_TASKS");
        double totalEstimatedHours = getDouble(rs, "TOTAL_ESTIMATED_HOURS");
        double totalRealHours = getDouble(rs, "TOTAL_REAL_HOURS");

        double completionRate = percentage(completedTasks, totalTasks);
        double onTimeRate = percentage(onTimeTasks, completedTasks);
        double estimationAccuracy = totalRealHours == 0
                ? 0.0
                : round(totalEstimatedHours / totalRealHours, 2);

        int denominator = Math.max(developerCount, 1);

        double avgTasksPerDeveloper = round((double) completedTasks / denominator, 2);
        double avgHoursPerDeveloper = round(totalRealHours / denominator, 2);

        return new DashboardSummaryDTO(
                totalTasks,
                completedTasks,
                onTimeTasks,
                delayedTasks,
                round(totalEstimatedHours, 2),
                round(totalRealHours, 2),
                completionRate,
                onTimeRate,
                estimationAccuracy,
                avgTasksPerDeveloper,
                avgHoursPerDeveloper
        );
    }

    private int getInt(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return value == null ? 0 : value.intValue();
    }

    private double getDouble(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return value == null ? 0.0 : value.doubleValue();
    }

    private double percentage(int numerator, int denominator) {
        if (denominator == 0) {
            return 0.0;
        }

        return round(((double) numerator / denominator) * 100.0, 2);
    }

    private double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }

    private String toOracleRawHex(String id) {
        return id.replace("-", "").toUpperCase();
    }
}