package com.oraclejavabot.features.tasks.controller;

import com.oraclejavabot.features.tasks.dto.TaskUserDTO;
import com.oraclejavabot.features.tasks.service.TaskUserService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.oraclejavabot.features.tasks.dto.TaskAssignmentDTO;

@RestController
@RequestMapping("/api")
public class TaskUserController {

    private final TaskUserService service;

    public TaskUserController(TaskUserService service) {
        this.service = service;
    }

    // =============================
    // GET USERS BY TASK
    // =============================
    @GetMapping("/tasks/{taskId}/users")
    public List<TaskUserDTO> getUsers(@PathVariable String taskId) {
        return service.getUsersByTask(taskId);
    }

    // =============================
    // ASSIGN USER
    // =============================
    @PostMapping("/tasks/{taskId}/users/{userId}")
    public void assignUser(@PathVariable String taskId,
                           @PathVariable String userId) {
        service.assignUser(taskId, userId);
    }

    // =============================
    // REMOVE USER
    // =============================
    @DeleteMapping("/tasks/{taskId}/users/{userId}")
    public void removeUser(@PathVariable String taskId,
                           @PathVariable String userId) {
        service.removeUser(taskId, userId);
    }

    // =============================
    // GET TASKS BY DEVELOPERS AND SPRINTS
    // =============================
    @GetMapping("/task-assignments")
    public List<TaskAssignmentDTO> getTasksByDevelopersAndSprints(
            @RequestParam List<String> developerIds,
            @RequestParam List<String> sprintIds
    ) {
        return service.getTasksByDevelopersAndSprints(developerIds, sprintIds);
    }
}