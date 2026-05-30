package com.oraclejavabot.features.users.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserDependencyRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserDependencyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasAssignedTasks(UUID userId) {
        String sql = """
            SELECT COUNT(*)
            FROM "CHATBOT_USER"."USUARIO_A_TAREA"
            WHERE "USER_ID" = HEXTORAW(?)
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, toOracleRawHex(userId));
        return count != null && count > 0;
    }

    public boolean hasActiveDirectReports(UUID userId) {
        String sql = """
            SELECT COUNT(*)
            FROM "CHATBOT_USER"."USUARIO"
            WHERE "MANAGER_ID" = HEXTORAW(?)
              AND "ESTADO_ID" = 1
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, toOracleRawHex(userId));
        return count != null && count > 0;
    }

    private String toOracleRawHex(UUID uuid) {
        return uuid.toString().replace("-", "").toUpperCase();
    }
}