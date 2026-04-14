package com.oraclejavabot.features.projects.service;

import com.oraclejavabot.features.projects.dto.ProjectMemberDTO;
import com.oraclejavabot.features.projects.model.ProjectMemberEntity;
import com.oraclejavabot.features.projects.model.ProjectMemberId;
import com.oraclejavabot.features.projects.repository.ProjectMemberRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository repository;
    private final ProjectRepository projectRepository;

    public ProjectMemberService(ProjectMemberRepository repository,
                                ProjectRepository projectRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;
    }

    // =============================
    // GET MEMBERS
    // =============================
    public List<ProjectMemberDTO> getMembers(String projectId) {

        UUID projectUuid = hexToUuid(projectId);

        return repository.findByIdProjectId(projectUuid)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // =============================
    // ADD MEMBER
    // =============================
    public void addMember(String projectId, String userId) {

        UUID projectUuid = hexToUuid(projectId);
        UUID userUuid = hexToUuid(userId);

        // validar duplicado
        if (repository.existsByIdUserIdAndIdProjectId(userUuid, projectUuid)) {
            throw new IllegalArgumentException("User already in project");
        }

        // 🔥 VALIDACIÓN CRUZADA (IMPORTANTE)
        boolean belongsToTeam = projectRepository.existsUserInProjectTeam(projectId, userId) > 0;

        if (!belongsToTeam) {
            throw new IllegalArgumentException("User does not belong to the project's team");
        }

        ProjectMemberId id = new ProjectMemberId(userUuid, projectUuid);
        repository.save(new ProjectMemberEntity(id));
    }

    // =============================
    // REMOVE MEMBER
    // =============================
    public void removeMember(String projectId, String userId) {

        UUID projectUuid = hexToUuid(projectId);
        UUID userUuid = hexToUuid(userId);

        ProjectMemberId id = new ProjectMemberId(userUuid, projectUuid);

        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Member not found in project");
        }

        repository.deleteById(id);
    }

    // =============================
    // HELPERS
    // =============================
    private ProjectMemberDTO mapToDTO(ProjectMemberEntity entity) {

        ProjectMemberDTO dto = new ProjectMemberDTO();

        dto.setUserId(uuidToHex(entity.getId().getUserId()));
        dto.setProjectId(uuidToHex(entity.getId().getProjectId()));

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