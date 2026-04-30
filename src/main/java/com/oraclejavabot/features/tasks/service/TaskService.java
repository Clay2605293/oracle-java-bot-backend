package com.oraclejavabot.features.tasks.service;

import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.model.TaskEntity;
import com.oraclejavabot.features.tasks.repository.TaskRepository;
import com.oraclejavabot.features.sprints.repository.SprintRepository;

import com.oraclejavabot.messaging.event.AiTaskEmbeddingRequestEvent;
import com.oraclejavabot.messaging.producer.AiTaskEmbeddingProducer;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-3-small";

    private final TaskRepository repository;
    private final SprintRepository sprintRepository;
    private final AiTaskEmbeddingProducer taskEmbeddingProducer;

    public TaskService(TaskRepository repository,
                       SprintRepository sprintRepository,
                       AiTaskEmbeddingProducer taskEmbeddingProducer) {
        this.repository = repository;
        this.sprintRepository = sprintRepository;
        this.taskEmbeddingProducer = taskEmbeddingProducer;
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

        requestEmbeddingSafely(saved);

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

        boolean shouldRegenerateEmbedding = false;

        System.out.println("========== UPDATE TASK ==========");
        System.out.println("TaskId: " + taskId);

        // TITULO / DESCRIPCION
        if (request.getTitulo() != null) {
            task.setTitulo(request.getTitulo());
            shouldRegenerateEmbedding = true;
        }

        if (request.getDescripcion() != null) {
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
            requestEmbeddingSafely(updated);
        }

        return mapToResponse(updated);
    }

    public void deleteTask(String taskId) {
        TaskEntity task = repository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (task.getEstadoId() == 3) {
            throw new IllegalArgumentException("Cannot delete completed task");
        }

        repository.delete(task);
    }

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

        return mapToResponse(updated);
    }

    private void requestEmbeddingSafely(TaskEntity task) {
        try {
            if (task == null || task.getTaskId() == null || task.getProjectId() == null) {
                return;
            }

            if (task.getTitulo() == null || task.getTitulo().isBlank()) {
                return;
            }

            AiTaskEmbeddingRequestEvent event = new AiTaskEmbeddingRequestEvent();

            event.setTaskId(uuidToHex(task.getTaskId()));
            event.setProjectId(uuidToHex(task.getProjectId()));
            event.setTitulo(task.getTitulo());
            event.setDescripcion(task.getDescripcion());
            event.setEmbeddingModel(DEFAULT_EMBEDDING_MODEL);

            taskEmbeddingProducer.sendTaskEmbeddingRequest(event);

            System.out.println("📤 Task embedding request sent for task: " + uuidToHex(task.getTaskId()));

        } catch (Exception e) {
            System.err.println(
                    "⚠️ Could not send task embedding request. Task operation was not rolled back: "
                            + e.getMessage()
            );
        }
    }

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