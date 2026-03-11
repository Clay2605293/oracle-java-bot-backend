package com.oraclejavabot.features.taskstatus.controller;

import com.oraclejavabot.features.taskstatus.dto.TaskStatusRequestDTO;
import com.oraclejavabot.features.taskstatus.dto.TaskStatusResponseDTO;
import com.oraclejavabot.features.taskstatus.service.TaskStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-status")
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public List<TaskStatusResponseDTO> getTaskStatuses() {
        return taskStatusService.getTaskStatuses();
    }

    @PostMapping
    public TaskStatusResponseDTO createTaskStatus(@RequestBody TaskStatusRequestDTO request) {
        return taskStatusService.createTaskStatus(request);
    }
}