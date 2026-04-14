package com.oraclejavabot.features.teams.controller;

import com.oraclejavabot.features.teams.dto.TeamMemberDTO;
import com.oraclejavabot.features.teams.service.TeamMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamMemberController {

    private final TeamMemberService service;

    public TeamMemberController(TeamMemberService service) {
        this.service = service;
    }

    @GetMapping("/{teamId}/members")
    public List<TeamMemberDTO> getMembers(@PathVariable String teamId) {
        return service.getMembers(teamId);
    }

    @PostMapping("/{teamId}/members/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addMember(@PathVariable String teamId,
                          @PathVariable String userId) {
        service.addMember(teamId, userId);
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable String teamId,
                             @PathVariable String userId) {
        service.removeMember(teamId, userId);
    }
}