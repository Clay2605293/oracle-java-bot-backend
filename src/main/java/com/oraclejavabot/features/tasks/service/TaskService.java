package com.oraclejavabot.features.tasks.service;

import com.oraclejavabot.features.ai.service.TaskVectorEmbeddingService;
import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.model.TaskEntity;
import com.oraclejavabot.features.tasks.repository.TaskRepository;
import com.oraclejavabot.features.sprints.repository.SprintRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final SprintRepository sprintRepository;
    private final TaskVectorEmbeddingService taskVectorEmbeddingService;
    private final TaskUserService taskUserService;

    public TaskService(
            TaskRepository repository,
            SprintRepository sprintRepository,
            TaskVectorEmbeddingService taskVectorEmbeddingService,
            TaskUserService taskUserService
    ) {
        this.repository = repository;
        this.sprintRepository = sprintRepository;
        this.taskVectorEmbeddingService = taskVectorEmbeddingService;
        this.taskUserService = taskUserService;
    }

    public TaskResponseDTO createTask(String projectId, TaskRequestDTO request) {

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

            int exists = repository.validateSprintInProject(
                    request.getSprintId(),
                    projectId
            );

            if (exists == 0) {
                throw new IllegalArgumentException("Sprint does not belong to project");
            }

            task.setSprintId(hexToUuid(request.getSprintId()));
        }

        task.setTiempoEstimado(request.getTiempoEstimado());
        task.setTiempoReal(0.0);

        TaskEntity saved = repository.save(task);

        return mapToResponse(saved);
    }

    public List<TaskResponseDTO> getTasksByProject(String projectId) {
        return repository.findByProjectId(hexToUuid(projectId))
                .stream()
                .map(this::mapToLightResponse)
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

    @Transactional(timeout = 10)
    public TaskResponseDTO updateTask(String taskId, TaskRequestDTO request) {

        TaskEntity task = repository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        boolean shouldRegenerateEmbedding = false;

        System.out.println("========== UPDATE TASK ==========");
        System.out.println("TaskId: " + taskId);

        // TITULO / DESCRIPCION
        // Solo regenerar embedding si realmente cambió el texto.
        if (request.getTitulo() != null && !request.getTitulo().equals(task.getTitulo())) {
            task.setTitulo(request.getTitulo());
            shouldRegenerateEmbedding = true;
        }

        if (request.getDescripcion() != null && !request.getDescripcion().equals(task.getDescripcion())) {
            task.setDescripcion(request.getDescripcion());
            shouldRegenerateEmbedding = true;
        }

        // FECHA LIMITE
        if (request.getFechaLimite() != null) {

            LocalDateTime fechaLimite;

            try {
                fechaLimite = LocalDateTime.parse(request.getFechaLimite());
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido");
            }

            if (!fechaLimite.isAfter(task.getFechaCreacion())) {
                throw new IllegalArgumentException("fechaLimite must be after fechaCreacion");
            }

            task.setFechaLimite(fechaLimite);
        }

        // PRIORIDAD
        if (request.getPrioridadId() != null) {
            task.setPrioridadId(request.getPrioridadId());
        }

        // SPRINT
        if (request.getSprintId() != null) {

            if (!request.getSprintId().isBlank()) {

                int exists = repository.validateSprintInProject(
                        request.getSprintId(),
                        uuidToHex(task.getProjectId())
                );

                if (exists == 0) {
                    throw new IllegalArgumentException("Sprint does not belong to project");
                }

                task.setSprintId(hexToUuid(request.getSprintId()));

            } else {
                task.setSprintId(null);
            }
        }

        // TIEMPOS
        if (request.getTiempoEstimado() != null) {
            task.setTiempoEstimado(request.getTiempoEstimado());
        }

        if (request.getTiempoReal() != null) {
            task.setTiempoReal(request.getTiempoReal());
        }

        // ESTADO + FECHA FINALIZACION
        if (request.getEstadoId() != null) {

            Integer estado = request.getEstadoId();
            task.setEstadoId(estado);

            if (estado == 3) {

                if (request.getFechaFinalizacion() != null) {
                    try {
                        task.setFechaFinalizacion(
                                LocalDateTime.parse(request.getFechaFinalizacion())
                        );
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Formato de fechaFinalizacion inválido");
                    }
                } else {
                    task.setFechaFinalizacion(LocalDateTime.now());
                }

            } else {
                task.setFechaFinalizacion(null);
            }
        }

        TaskEntity updated = repository.save(task);

        if (shouldRegenerateEmbedding) {
            System.out.println(
                    "ℹ️ Embedding regeneration skipped during synchronous task update: "
                            + uuidToHex(updated.getTaskId())
            );
        }

return mapToLightResponse(updated);
    }

    public void deleteTask(String taskId) {
        TaskEntity task = repository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (task.getEstadoId() == 3) {
            throw new IllegalArgumentException("Cannot delete completed task");
        }

        repository.delete(task);
    }

    @Transactional(timeout = 10)
    public TaskResponseDTO changeStatus(String taskId, Integer estadoId) {

        TaskEntity task = repository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setEstadoId(estadoId);

        if (estadoId == 3) {
            task.setFechaFinalizacion(LocalDateTime.now());
        } else {
            task.setFechaFinalizacion(null);
        }

        TaskEntity updated = repository.save(task);

        return mapToLightResponse(updated);
    }

    private void requestEmbeddingSafely(TaskEntity task) {
        try {
            if (task == null || task.getTaskId() == null || task.getProjectId() == null) {
                return;
            }

            if (task.getTitulo() == null || task.getTitulo().isBlank()) {
                return;
            }

            taskVectorEmbeddingService.upsertTaskVectorEmbedding(task);

            System.out.println(
                    "✅ Oracle vector embedding generated for task: "
                            + uuidToHex(task.getTaskId())
            );

        } catch (Exception e) {
            System.err.println(
                    "⚠️ Could not generate Oracle vector embedding. Task operation was not rolled back: "
                            + e.getMessage()
            );
        }
    }

    private TaskResponseDTO mapToResponse(TaskEntity task) {

        TaskResponseDTO dto = mapBaseFields(task, true);

        dto.setResponsables(
                taskUserService.getUsersByTask(uuidToHex(task.getTaskId()))
        );

        return dto;
    }

    private TaskResponseDTO mapToLightResponse(TaskEntity task) {

        TaskResponseDTO dto = mapBaseFields(task, false);

        // Respuesta ligera: no consultar USUARIO_A_TAREA ni USUARIO.
        dto.setResponsables(List.of());

        return dto;
    }

    private TaskResponseDTO mapBaseFields(TaskEntity task, boolean includeSprintName) {

        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setTaskId(uuidToHex(task.getTaskId()));
        dto.setTitulo(task.getTitulo());
        dto.setDescripcion(task.getDescripcion());

        if (task.getFechaCreacion() != null) {
            dto.setFechaCreacion(task.getFechaCreacion().toString());
        }

        if (task.getFechaLimite() != null) {
            dto.setFechaLimite(task.getFechaLimite().toString());
        }

        if (task.getFechaFinalizacion() != null) {
            dto.setFechaFinalizacion(task.getFechaFinalizacion().toString());
        }

        dto.setEstadoId(task.getEstadoId());
        dto.setPrioridadId(task.getPrioridadId());

        if (task.getProjectId() != null) {
            dto.setProjectId(uuidToHex(task.getProjectId()));
        }

        if (task.getSprintId() != null) {

            UUID sprintId = task.getSprintId();

            dto.setSprintId(uuidToHex(sprintId));

            if (includeSprintName) {
                sprintRepository.findById(sprintId)
                        .ifPresentOrElse(
                                sprint -> dto.setSprintNombre(sprint.getNombre()),
                                () -> dto.setSprintNombre("—")
                        );
            }
        }

        dto.setTiempoEstimado(task.getTiempoEstimado());
        dto.setTiempoReal(task.getTiempoReal());

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