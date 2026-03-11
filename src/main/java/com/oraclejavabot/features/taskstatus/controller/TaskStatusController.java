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
    /**
     * Recupera la lista de estados de tarea disponibles.
     *
     * @return lista de {@link TaskStatusResponseDTO} con los estados almacenados
     */
    public List<TaskStatusResponseDTO> getTaskStatuses() {
        return taskStatusService.getTaskStatuses();
    }

    @PostMapping
    /**
     * Crea un nuevo estado de tarea a partir de los datos recibidos.
     *
     * @param request DTO con los datos para crear el estado
     * @return DTO con los datos del estado creado
     * @throws IllegalArgumentException si ya existe un estado con el mismo nombre
     */
    public TaskStatusResponseDTO createTaskStatus(@RequestBody TaskStatusRequestDTO request) {
        return taskStatusService.createTaskStatus(request);
    }
}