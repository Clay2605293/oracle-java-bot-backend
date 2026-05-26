package com.oraclejavabot.features.users.repository;

import com.oraclejavabot.features.users.dto.SkillCategory;
import com.oraclejavabot.features.users.dto.UserSkillProfileDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.oraclejavabot.features.users.dto.SkillCategoryOptionDTO;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserSkillProfileRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserSkillProfileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserSkillProfileDTO> findAllPrimarySkillProfiles() {
        String sql = """
            SELECT
                RAWTOHEX("USER_ID") AS "USER_ID",
                "PRIMER_NOMBRE",
                "APELLIDO",
                "EMAIL",
                "PRIMARY_SKILL_CATEGORY",
                "PRIMARY_SKILL_CODE",
                "PRIMARY_SKILL_NAME",
                "PRIMARY_SKILL_LEVEL",
                "PRIMARY_SKILL_YEARS",
                "CARD_TYPE"
            FROM "CHATBOT_USER"."VW_USUARIO_SKILL_GRAPHQL"
            WHERE "PRIMARY_SKILL_CATEGORY" IS NOT NULL
            ORDER BY "PRIMARY_SKILL_CATEGORY", "PRIMER_NOMBRE", "APELLIDO"
            """;

        return jdbcTemplate.query(sql, this::mapRow);
    }

    public List<UserSkillProfileDTO> findByPrimarySkillCategory(SkillCategory category) {
        String sql = """
            SELECT
                RAWTOHEX("USER_ID") AS "USER_ID",
                "PRIMER_NOMBRE",
                "APELLIDO",
                "EMAIL",
                "PRIMARY_SKILL_CATEGORY",
                "PRIMARY_SKILL_CODE",
                "PRIMARY_SKILL_NAME",
                "PRIMARY_SKILL_LEVEL",
                "PRIMARY_SKILL_YEARS",
                "CARD_TYPE"
            FROM "CHATBOT_USER"."VW_USUARIO_SKILL_GRAPHQL"
            WHERE "PRIMARY_SKILL_CATEGORY" = ?
            ORDER BY "PRIMER_NOMBRE", "APELLIDO"
            """;

        return jdbcTemplate.query(sql, this::mapRow, category.name());
    }

    public Optional<UserSkillProfileDTO> findByUserId(String userIdHex) {
        String sql = """
            SELECT
                RAWTOHEX("USER_ID") AS "USER_ID",
                "PRIMER_NOMBRE",
                "APELLIDO",
                "EMAIL",
                "PRIMARY_SKILL_CATEGORY",
                "PRIMARY_SKILL_CODE",
                "PRIMARY_SKILL_NAME",
                "PRIMARY_SKILL_LEVEL",
                "PRIMARY_SKILL_YEARS",
                "CARD_TYPE"
            FROM "CHATBOT_USER"."VW_USUARIO_SKILL_GRAPHQL"
            WHERE "USER_ID" = HEXTORAW(?)
              AND "PRIMARY_SKILL_CATEGORY" IS NOT NULL
            """;

        List<UserSkillProfileDTO> result = jdbcTemplate.query(sql, this::mapRow, userIdHex);

        return result.stream().findFirst();
    }

    private UserSkillProfileDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserSkillProfileDTO(
                rs.getString("USER_ID"),
                rs.getString("PRIMER_NOMBRE"),
                rs.getString("APELLIDO"),
                rs.getString("EMAIL"),
                SkillCategory.valueOf(rs.getString("PRIMARY_SKILL_CATEGORY")),
                rs.getString("PRIMARY_SKILL_CODE"),
                rs.getString("PRIMARY_SKILL_NAME"),
                rs.getString("PRIMARY_SKILL_LEVEL"),
                getNullableDouble(rs, "PRIMARY_SKILL_YEARS"),
                rs.getString("CARD_TYPE")
        );
    }

    public List<UserSkillProfileDTO> searchSkillProfiles(String text) {
        String sql = """
            SELECT
                RAWTOHEX(v."USER_ID") AS "USER_ID",
                v."PRIMER_NOMBRE",
                v."APELLIDO",
                v."EMAIL",
                v."PRIMARY_SKILL_CATEGORY",
                v."PRIMARY_SKILL_CODE",
                v."PRIMARY_SKILL_NAME",
                v."PRIMARY_SKILL_LEVEL",
                v."PRIMARY_SKILL_YEARS",
                v."CARD_TYPE"
            FROM "CHATBOT_USER"."VW_USUARIO_SKILL_GRAPHQL" v
            WHERE v."PRIMARY_SKILL_CATEGORY" IS NOT NULL
            AND (
                    LOWER(v."PRIMER_NOMBRE") LIKE ?
                OR LOWER(v."APELLIDO") LIKE ?
                OR LOWER(v."EMAIL") LIKE ?
                OR LOWER(v."PRIMARY_SKILL_CATEGORY") LIKE ?
                OR LOWER(v."PRIMARY_SKILL_CODE") LIKE ?
                OR LOWER(v."PRIMARY_SKILL_NAME") LIKE ?
            )
            ORDER BY v."PRIMARY_SKILL_CATEGORY", v."PRIMER_NOMBRE", v."APELLIDO"
            """;

        String searchPattern = "%" + normalizeSearchText(text) + "%";

        return jdbcTemplate.query(
                sql,
                this::mapRow,
                searchPattern,
                searchPattern,
                searchPattern,
                searchPattern,
                searchPattern,
                searchPattern
        );
    }

    public List<SkillCategoryOptionDTO> findSkillCategories() {
        String sql = """
            SELECT
                "CODIGO",
                "NOMBRE",
                "DESCRIPCION",
                "CARD_TYPE"
            FROM "CHATBOT_USER"."CAT_SKILL_CATEGORIA"
            WHERE "ACTIVO" = 'S'
            ORDER BY "NOMBRE"
            """;

        return jdbcTemplate.query(sql, this::mapSkillCategoryOptionRow);
    }

    private Double getNullableDouble(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return value != null ? value.doubleValue() : null;
    }

        private SkillCategoryOptionDTO mapSkillCategoryOptionRow(ResultSet rs, int rowNum) throws SQLException {
        return new SkillCategoryOptionDTO(
                SkillCategory.valueOf(rs.getString("CODIGO")),
                rs.getString("NOMBRE"),
                rs.getString("DESCRIPCION"),
                rs.getString("CARD_TYPE")
        );
    }

    private String normalizeSearchText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        return text.trim().toLowerCase();
    }

    
}