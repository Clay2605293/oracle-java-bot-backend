package com.oraclejavabot.features.users.dto;

public record UserSkillProfileDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        SkillCategory primarySkillCategory,
        String primarySkillCode,
        String primarySkillName,
        String primarySkillLevel,
        Double primarySkillYears,
        String cardType
) {
}