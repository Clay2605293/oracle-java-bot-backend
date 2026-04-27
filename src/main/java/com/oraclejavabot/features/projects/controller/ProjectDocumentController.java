package com.oraclejavabot.features.projects.controller;

import com.oraclejavabot.features.projects.dto.ProjectDocumentResponseDTO;
import com.oraclejavabot.features.projects.service.ProjectDocumentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectDocumentController {

    private final ProjectDocumentService projectDocumentService;

    public ProjectDocumentController(ProjectDocumentService projectDocumentService) {
        this.projectDocumentService = projectDocumentService;
    }

    @PostMapping("/{projectId}/documents")
    public ProjectDocumentResponseDTO uploadDocument(
            @PathVariable String projectId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file
    ) {
        return projectDocumentService.uploadDocument(projectId, documentType, file);
    }

    @GetMapping("/{projectId}/documents")
    public List<ProjectDocumentResponseDTO> getDocumentsByProject(
            @PathVariable String projectId,
            @RequestParam(value = "documentType", required = false) String documentType
    ) {
        if (documentType == null || documentType.isBlank()) {
            return projectDocumentService.getDocumentsByProject(projectId);
        }

        return projectDocumentService.getDocumentsByProjectAndType(projectId, documentType);
    }

    @DeleteMapping("/documents/{documentId}")
    public void deleteDocument(@PathVariable String documentId) {
        projectDocumentService.deleteDocument(documentId);
}
}