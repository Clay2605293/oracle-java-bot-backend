package com.oraclejavabot.features.tasks.controller;

import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.service.TaskService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // =============================
    // CREATE TASK
    // =============================
    @PostMapping("/projects/{projectId}/tasks")
    public TaskResponseDTO createTask(@PathVariable String projectId,
                                      @RequestBody TaskRequestDTO request) {
        return service.createTask(projectId, request);
    }

    // =============================
    // GET TASKS BY PROJECT
    // =============================
    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponseDTO> getTasksByProject(@PathVariable String projectId) {
        return service.getTasksByProject(projectId);
    }

    // =============================
    // GET TASK BY ID
    // =============================
    @GetMapping("/tasks/{taskId}")
    public TaskResponseDTO getTaskById(@PathVariable String taskId) {
        return service.getTaskById(taskId);
    }

    // =============================
    // UPDATE TASK
    // =============================
    @PutMapping("/tasks/{taskId}")
    public TaskResponseDTO updateTask(@PathVariable String taskId,
                                      @RequestBody TaskRequestDTO request) {
        return service.updateTask(taskId, request);
    }

    // =============================
    // DELETE TASK
    // =============================
    @DeleteMapping("/tasks/{taskId}")
    public void deleteTask(@PathVariable String taskId) {
        service.deleteTask(taskId);
    }

    // =============================
    // CHANGE STATUS
    // =============================
    @PatchMapping("/tasks/{taskId}/status")
    public TaskResponseDTO changeStatus(@PathVariable String taskId,
                                        @RequestParam Integer estadoId) {
        return service.changeStatus(taskId, estadoId);
    }
}