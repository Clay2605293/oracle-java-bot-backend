package com.oraclejavabot.features.projects.controller;

import com.oraclejavabot.features.projects.dto.ProjectMemberDTO;
import com.oraclejavabot.features.projects.service.ProjectMemberService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectMemberController {

    private final ProjectMemberService service;

    public ProjectMemberController(ProjectMemberService service) {
        this.service = service;
    }

    @GetMapping("/{projectId}/members")
    public List<ProjectMemberDTO> getMembers(@PathVariable String projectId) {
        return service.getMembers(projectId);
    }

    @PostMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addMember(@PathVariable String projectId,
                          @PathVariable String userId) {
        service.addMember(projectId, userId);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable String projectId,
                             @PathVariable String userId) {
        service.removeMember(projectId, userId);
    }
}