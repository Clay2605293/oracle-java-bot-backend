package com.oraclejavabot.features.projects.repository;

import com.oraclejavabot.features.projects.model.ProjectMemberEntity;
import com.oraclejavabot.features.projects.model.ProjectMemberId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, ProjectMemberId> {

    List<ProjectMemberEntity> findByIdProjectId(UUID projectId);

    boolean existsByIdUserIdAndIdProjectId(UUID userId, UUID projectId);
}