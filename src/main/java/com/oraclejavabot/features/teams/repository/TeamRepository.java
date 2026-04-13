package com.oraclejavabot.features.teams.repository;

import com.oraclejavabot.features.teams.model.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {

    List<TeamEntity> findByOwnerId(UUID ownerId);

}