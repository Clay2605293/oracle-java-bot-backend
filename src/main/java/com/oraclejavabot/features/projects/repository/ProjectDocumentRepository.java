package com.oraclejavabot.features.projects.repository;

import com.oraclejavabot.features.projects.model.ProjectDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectDocumentRepository extends JpaRepository<ProjectDocumentEntity, UUID> {

    List<ProjectDocumentEntity> findByProjectId(UUID projectId);

    List<ProjectDocumentEntity> findByProjectIdAndDocumentType(UUID projectId, String documentType);
}