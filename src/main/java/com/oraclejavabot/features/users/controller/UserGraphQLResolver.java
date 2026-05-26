package com.oraclejavabot.features.users.controller;

import com.oraclejavabot.features.users.dto.SkillCategory;
import com.oraclejavabot.features.users.dto.UserSkillProfileDTO;
import com.oraclejavabot.features.users.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import com.oraclejavabot.features.users.dto.SkillCategoryOptionDTO;

import java.util.List;

@Controller
public class UserGraphQLResolver {

    private final UserService userService;

    public UserGraphQLResolver(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<UserSkillProfileDTO> usersSkillProfiles() {
        return userService.getUsersSkillProfiles();
    }

    @QueryMapping
    public List<UserSkillProfileDTO> usersByPrimarySkillCategory(@Argument SkillCategory category) {
        return userService.getUsersByPrimarySkillCategory(category);
    }

    @QueryMapping
    public UserSkillProfileDTO userSkillProfileById(@Argument String id) {
        return userService.getUserSkillProfileById(id).orElse(null);
    }

    @QueryMapping
    public List<UserSkillProfileDTO> searchUserSkillProfiles(@Argument String text) {
        return userService.searchUserSkillProfiles(text);
    }

    @QueryMapping
    public List<SkillCategoryOptionDTO> skillCategories() {
        return userService.getSkillCategories();
    }
    
}