package com.oraclejavabot.features.teams.controller;

import com.oraclejavabot.features.teams.dto.TeamRequestDTO;
import com.oraclejavabot.features.teams.dto.TeamResponseDTO;
import com.oraclejavabot.features.teams.service.TeamService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponseDTO createTeam(@Valid @RequestBody TeamRequestDTO request) {
        return teamService.createTeam(request);
    }

    @GetMapping
    public List<TeamResponseDTO> getTeams() {
        return teamService.getTeams();
    }

    @GetMapping("/{teamId}")
    public TeamResponseDTO getTeamById(@PathVariable String teamId) {
        return teamService.getTeamById(teamId);
    }
}