package com.oraclejavabot.features.users.dto;

public record SkillCategoryOptionDTO(
        SkillCategory code,
        String name,
        String description,
        String cardType
) {
}