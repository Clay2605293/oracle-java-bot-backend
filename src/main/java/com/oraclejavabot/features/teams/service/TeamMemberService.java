package com.oraclejavabot.features.teams.service;

import com.oraclejavabot.features.teams.dto.TeamMemberDTO;
import com.oraclejavabot.features.teams.model.TeamMemberEntity;
import com.oraclejavabot.features.teams.model.TeamMemberId;
import com.oraclejavabot.features.teams.repository.TeamMemberRepository;
import com.oraclejavabot.features.users.repository.UserRepository; // 🔹 NUEVO

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamMemberService {

    private final TeamMemberRepository repository;
    private final UserRepository userRepository; // 🔹 NUEVO

    public TeamMemberService(TeamMemberRepository repository,
                             UserRepository userRepository) { // 🔹 NUEVO
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<TeamMemberDTO> getMembers(String teamId) {

        UUID teamUuid = hexToUuid(teamId);

        return repository.findByIdTeamId(teamUuid)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void addMember(String teamId, String userId) {

        UUID teamUuid = hexToUuid(teamId);
        UUID userUuid = hexToUuid(userId);

        if (repository.existsByIdUserIdAndIdTeamId(userUuid, teamUuid)) {
            throw new IllegalArgumentException("User already in team");
        }

        TeamMemberId id = new TeamMemberId(userUuid, teamUuid);
        repository.save(new TeamMemberEntity(id));
    }

    public void removeMember(String teamId, String userId) {

        UUID teamUuid = hexToUuid(teamId);
        UUID userUuid = hexToUuid(userId);

        TeamMemberId id = new TeamMemberId(userUuid, teamUuid);

        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Member not found in team");
        }

        repository.deleteById(id);
    }

    private TeamMemberDTO mapToDTO(TeamMemberEntity entity) {

        TeamMemberDTO dto = new TeamMemberDTO();

        UUID userId = entity.getId().getUserId();

        dto.setUserId(uuidToHex(userId));
        dto.setTeamId(uuidToHex(entity.getId().getTeamId()));

        // 🔹 NUEVO: nombre del usuario
        userRepository.findById(userId)
                .ifPresentOrElse(
                        user -> dto.setNombre(
                                user.getPrimerNombre() + " " + user.getApellido()
                        ),
                        () -> dto.setNombre("—")
                );

        return dto;
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