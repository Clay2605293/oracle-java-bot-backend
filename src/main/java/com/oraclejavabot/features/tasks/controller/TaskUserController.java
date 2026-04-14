package com.oraclejavabot.features.tasks.controller;

import com.oraclejavabot.features.tasks.dto.TaskUserDTO;
import com.oraclejavabot.features.tasks.service.TaskUserService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}