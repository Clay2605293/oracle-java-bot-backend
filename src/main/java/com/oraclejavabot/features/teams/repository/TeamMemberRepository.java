package com.oraclejavabot.features.teams.repository;

import com.oraclejavabot.features.teams.model.TeamMemberEntity;
import com.oraclejavabot.features.teams.model.TeamMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, TeamMemberId> {

    List<TeamMemberEntity> findByIdTeamId(UUID teamId);

    void deleteByIdUserIdAndIdTeamId(UUID userId, UUID teamId);

    boolean existsByIdUserIdAndIdTeamId(UUID userId, UUID teamId);

}