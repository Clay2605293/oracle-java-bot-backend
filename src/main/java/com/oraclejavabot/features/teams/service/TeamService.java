package com.oraclejavabot.features.teams.service;

import com.oraclejavabot.features.teams.dto.TeamRequestDTO;
import com.oraclejavabot.features.teams.dto.TeamResponseDTO;
import com.oraclejavabot.features.teams.model.TeamEntity;
import com.oraclejavabot.features.teams.model.TeamMemberEntity;
import com.oraclejavabot.features.teams.model.TeamMemberId;
import com.oraclejavabot.features.teams.repository.TeamMemberRepository;
import com.oraclejavabot.features.teams.repository.TeamRepository;
import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository; // 🔹 NUEVO

    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       UserRepository userRepository) { // 🔹 NUEVO
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    public TeamResponseDTO createTeam(TeamRequestDTO request) {

        UUID ownerUuid = hexToUuid(request.getOwnerId());

        TeamEntity team = new TeamEntity();
        team.setNombre(request.getNombre());
        team.setDescripcion(request.getDescripcion());
        team.setOwnerId(ownerUuid);

        TeamEntity savedTeam = teamRepository.save(team);

        TeamMemberId memberId = new TeamMemberId(
                ownerUuid,
                savedTeam.getTeamId()
        );

        TeamMemberEntity member = new TeamMemberEntity(memberId);
        teamMemberRepository.save(member);

        return mapToResponse(savedTeam);
    }

    public List<TeamResponseDTO> getTeams() {
        return teamRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TeamResponseDTO getTeamById(String teamId) {

        TeamEntity team = teamRepository.findById(hexToUuid(teamId))
                .orElseThrow(() -> new RuntimeException("Team not found"));

        return mapToResponse(team);
    }

    private TeamResponseDTO mapToResponse(TeamEntity team) {

        TeamResponseDTO response = new TeamResponseDTO();

        response.setTeamId(uuidToHex(team.getTeamId()));
        response.setNombre(team.getNombre());
        response.setDescripcion(team.getDescripcion());

        String ownerIdHex = uuidToHex(team.getOwnerId());
        response.setOwnerId(ownerIdHex);

        // 🔹 OWNER NOMBRE
        userRepository.findById(team.getOwnerId())
                .ifPresentOrElse(
                        user -> response.setOwnerNombre(
                                user.getPrimerNombre() + " " + user.getApellido()
                        ),
                        () -> response.setOwnerNombre("—")
                );

        // 🔹 TOTAL MEMBERS
        int totalMembers = teamMemberRepository
                .findByIdTeamId(team.getTeamId())
                .size();

        response.setTotalMembers(totalMembers);

        return response;
    }

    private UUID hexToUuid(String hex) {
        return UUID.fromString(
                hex.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"
                )
        );
    }

    private String uuidToHex(UUID uuid) {
        return uuid.toString().replace("-", "").toUpperCase();
    }
}