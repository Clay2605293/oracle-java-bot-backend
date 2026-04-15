package com.oraclejavabot.features.tasks.service;

import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.model.TaskEntity;
import com.oraclejavabot.features.tasks.repository.TaskRepository;
import com.oraclejavabot.features.sprints.repository.SprintRepository; // 🔹 NUEVO

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final SprintRepository sprintRepository; // 🔹 NUEVO

    public TaskService(TaskRepository repository,
                       SprintRepository sprintRepository) { // 🔹 NUEVO
        this.repository = repository;
        this.sprintRepository = sprintRepository;
    }

    public TaskResponseDTO createTask(String projectId, TaskRequestDTO request) {

        System.out.println("========== CREATE TASK ==========");
        System.out.println("ProjectId (HEX): " + projectId);
        System.out.println("Titulo: " + request.getTitulo());
        System.out.println("SprintId: " + request.getSprintId());
        System.out.println("FechaLimite: " + request.getFechaLimite());

        // =============================
        // VALIDACIÓN SPRINT (DEBUG SAFE)
        // =============================
        try {
            if (request.getSprintId() != null && !request.getSprintId().isBlank()) {

                System.out.println("Validando sprint...");

                int exists = repository.validateSprintInProject(
                        request.getSprintId(),
                        projectId
                );

                System.out.println("Resultado validación sprint: " + exists);

                if (exists == 0) {
                    System.out.println("⚠ Sprint NO pertenece al proyecto (se ignora en DEBUG)");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error validando sprint: " + e.getMessage());
        }

        // =============================
        // VALIDACIÓN FECHAS
        // =============================
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fechaLimite;

        try {
            fechaLimite = LocalDateTime.parse(request.getFechaLimite());
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido");
        }

        if (!fechaLimite.isAfter(now)) {
            throw new IllegalArgumentException("fechaLimite must be after fechaCreacion");
        }

        // =============================
        // BUILD ENTITY
        // =============================
        TaskEntity task = new TaskEntity();

        task.setTitulo(request.getTitulo());
        task.setDescripcion(request.getDescripcion());
        task.setFechaCreacion(now);
        task.setFechaLimite(fechaLimite);
        task.setPrioridadId(request.getPrioridadId());
        task.setEstadoId(1);

        try {
            task.setProjectId(hexToUuid(projectId));
        } catch (Exception e) {
            throw new IllegalArgumentException("ProjectId inválido");
        }

        if (request.getSprintId() != null && !request.getSprintId().isBlank()) {
            try {
                task.setSprintId(hexToUuid(request.getSprintId()));
            } catch (Exception e) {
                System.out.println("⚠ SprintId inválido, se ignora");
            }
        }

        task.setTiempoEstimado(request.getTiempoEstimado());
        task.setTiempoReal(0.0);

        // =============================
        // SAVE
        // =============================
        System.out.println("Intentando guardar task...");

        TaskEntity saved = repository.save(task);

        System.out.println("✅ Task guardada con ID: " + saved.getTaskId());

        return mapToResponse(saved);
    }

    public List<TaskResponseDTO> getTasksByProject(String projectId) {
        return repository.findByProjectId(hexToUuid(projectId))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getAssignedTasksByUserAndProject(String userId, String projectId) {
        return repository.findAssignedTasksByUserAndProject(userId, projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getTaskById(String taskId) {
        TaskEntity task = repository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        return mapToResponse(task);
    }

    public TaskResponseDTO updateTask(String taskId, TaskRequestDTO request) {

        TaskEntity task = repository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        System.out.println("Actualizando task: " + taskId);

        task.setTitulo(request.getTitulo());
        task.setDescripcion(request.getDescripcion());

        LocalDateTime fechaLimite = LocalDateTime.parse(request.getFechaLimite());

        if (!fechaLimite.isAfter(task.getFechaCreacion())) {
            throw new IllegalArgumentException("fechaLimite must be after fechaCreacion");
        }

        task.setFechaLimite(fechaLimite);
        task.setPrioridadId(request.getPrioridadId());
        task.setTiempoEstimado(request.getTiempoEstimado());

        TaskEntity updated = repository.save(task);

        System.out.println("✅ Task actualizada");

        return mapToResponse(updated);
    }

    public void deleteTask(String taskId) {
        TaskEntity task = repository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (task.getEstadoId() == 3) {
            throw new IllegalArgumentException("Cannot delete completed task");
        }

        repository.delete(task);

        System.out.println("🗑 Task eliminada: " + taskId);
    }

    public TaskResponseDTO changeStatus(String taskId, Integer estadoId) {

        TaskEntity task = repository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        System.out.println("Cambiando estado a: " + estadoId);

        task.setEstadoId(estadoId);

        if (estadoId == 3) {
            task.setFechaFinalizacion(LocalDateTime.now());
        } else {
            task.setFechaFinalizacion(null);
        }

        TaskEntity updated = repository.save(task);

        return mapToResponse(updated);
    }

    // =============================
    // MAPPER
    // =============================
    private TaskResponseDTO mapToResponse(TaskEntity task) {

        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setTaskId(uuidToHex(task.getTaskId()));
        dto.setTitulo(task.getTitulo());
        dto.setDescripcion(task.getDescripcion());
        dto.setFechaCreacion(task.getFechaCreacion().toString());
        dto.setFechaLimite(task.getFechaLimite().toString());

        if (task.getFechaFinalizacion() != null) {
            dto.setFechaFinalizacion(task.getFechaFinalizacion().toString());
        }

        dto.setEstadoId(task.getEstadoId());
        dto.setPrioridadId(task.getPrioridadId());
        dto.setProjectId(uuidToHex(task.getProjectId()));

        // 🔥 NUEVO: Sprint + nombre
        if (task.getSprintId() != null) {

            UUID sprintId = task.getSprintId();

            dto.setSprintId(uuidToHex(sprintId));

            sprintRepository.findById(sprintId)
                    .ifPresentOrElse(
                            sprint -> dto.setSprintNombre(sprint.getNombre()),
                            () -> dto.setSprintNombre("—")
                    );
        }

        dto.setTiempoEstimado(task.getTiempoEstimado());
        dto.setTiempoReal(task.getTiempoReal());

        return dto;
    }

    // =============================
    // UTILS
    // =============================
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