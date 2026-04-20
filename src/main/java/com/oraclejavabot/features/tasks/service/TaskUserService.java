package com.oraclejavabot.features.tasks.service;

import com.oraclejavabot.features.projects.repository.ProjectRepository;
import com.oraclejavabot.features.tasks.dto.TaskUserDTO;
import com.oraclejavabot.features.tasks.model.TaskEntity;
import com.oraclejavabot.features.tasks.model.TaskUserEntity;
import com.oraclejavabot.features.tasks.model.TaskUserId;
import com.oraclejavabot.features.tasks.repository.TaskRepository;
import com.oraclejavabot.features.tasks.repository.TaskUserRepository;
import com.oraclejavabot.features.users.repository.UserRepository;
import com.oraclejavabot.messaging.producer.TaskEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskUserService {

    private static final Logger logger = LoggerFactory.getLogger(TaskUserService.class);

    private final TaskUserRepository taskUserRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskEventProducer taskEventProducer;

    public TaskUserService(TaskUserRepository taskUserRepository,
                           TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           UserRepository userRepository,
                           TaskEventProducer taskEventProducer) {
        this.taskUserRepository = taskUserRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskEventProducer = taskEventProducer;
    }

    public List<TaskUserDTO> getUsersByTask(String taskId) {
        return taskUserRepository.findByIdTaskId(hexToUuid(taskId))
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TaskUserDTO mapToDTO(TaskUserEntity entity) {
        TaskUserDTO dto = new TaskUserDTO();

        UUID userId = entity.getId().getUserId();

        dto.setTaskId(uuidToHex(entity.getId().getTaskId()));
        dto.setUserId(uuidToHex(userId));

        userRepository.findById(userId)
                .ifPresentOrElse(
                        user -> dto.setNombre(user.getPrimerNombre() + " " + user.getApellido()),
                        () -> dto.setNombre("—")
                );

        return dto;
    }

    public void assignUser(String taskId, String userId) {

        logger.info("Assign user to task requested. taskId={}, userId={}", taskId, userId);

        TaskEntity task = taskRepository.findById(hexToUuid(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        UUID userUUID = hexToUuid(userId);

        projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        int exists = projectRepository.existsUserInProjectTeam(
                uuidToHex(task.getProjectId()),
                userId
        );

        if (exists == 0) {
            throw new IllegalArgumentException("User does not belong to the project's team");
        }

        TaskUserId id = new TaskUserId(userUUID, task.getTaskId());

        if (taskUserRepository.existsById(id)) {
            throw new IllegalArgumentException("User already assigned to task");
        }

        TaskUserEntity entity = new TaskUserEntity(id);
        taskUserRepository.save(entity);

        logger.info("User assignment persisted successfully. taskId={}, userId={}", taskId, userId);

        taskEventProducer.sendUserAssignedEvent(taskId, userId);

        logger.info("Assignment flow completed with Kafka event. taskId={}, userId={}", taskId, userId);
    }

    public void removeUser(String taskId, String userId) {
        TaskUserId id = new TaskUserId(hexToUuid(userId), hexToUuid(taskId));

        if (!taskUserRepository.existsById(id)) {
            throw new IllegalArgumentException("User not assigned to task");
        }

        taskUserRepository.deleteById(id);
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