package com.oraclejavabot.features.tasks.service;

import com.oraclejavabot.features.tasks.dto.TaskUserDTO;
import com.oraclejavabot.features.tasks.model.TaskEntity;
import com.oraclejavabot.features.tasks.model.TaskUserEntity;
import com.oraclejavabot.features.tasks.model.TaskUserId;
import com.oraclejavabot.features.tasks.repository.TaskRepository;
import com.oraclejavabot.features.tasks.repository.TaskUserRepository;
import com.oraclejavabot.features.projects.repository.ProjectRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskUserService {

    private final TaskUserRepository taskUserRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskUserService(TaskUserRepository taskUserRepository,
                           TaskRepository taskRepository,
                           ProjectRepository projectRepository) {
        this.taskUserRepository = taskUserRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    // =============================
    // GET USERS BY TASK
    // =============================
    public List<TaskUserDTO> getUsersByTask(String taskId) {

        return taskUserRepository.findByIdTaskId(hexToUuid(taskId))
                .stream()
                .map(entity -> {
                    TaskUserDTO dto = new TaskUserDTO();
                    dto.setTaskId(uuidToHex(entity.getId().getTaskId()));
                    dto.setUserId(uuidToHex(entity.getId().getUserId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // =============================
    // ASSIGN USER TO TASK
    // =============================
    public void assignUser(String taskId, String userId) {

        System.out.println("========== ASSIGN USER TO TASK ==========");

        // 1. Validar task
        TaskEntity task = taskRepository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        UUID userUUID = hexToUuid(userId);

        // 2. Obtener project
        var project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        UUID teamId = project.getTeamId();

        // 3. Validar user pertenece al team (USANDO TU QUERY EXISTENTE)
        int exists = projectRepository.existsUserInProjectTeam(
                uuidToHex(task.getProjectId()),
                userId
        );

        if (exists == 0) {
            throw new IllegalArgumentException("User does not belong to the project's team");
        }

        // 4. Evitar duplicados
        TaskUserId id = new TaskUserId(userUUID, task.getTaskId());

        if (taskUserRepository.existsById(id)) {
            throw new IllegalArgumentException("User already assigned to task");
        }

        // 5. Guardar
        TaskUserEntity entity = new TaskUserEntity(id);
        taskUserRepository.save(entity);

        System.out.println("User assigned successfully");
    }

    // =============================
    // REMOVE USER
    // =============================
    public void removeUser(String taskId, String userId) {

        TaskUserId id = new TaskUserId(hexToUuid(userId), hexToUuid(taskId));

        if (!taskUserRepository.existsById(id)) {
            throw new IllegalArgumentException("User not assigned to task");
        }

        taskUserRepository.deleteById(id);
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