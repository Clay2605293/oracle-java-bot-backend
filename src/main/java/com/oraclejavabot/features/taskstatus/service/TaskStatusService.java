package com.oraclejavabot.features.taskstatus.service;

import com.oraclejavabot.features.taskstatus.dto.TaskStatusRequestDTO;
import com.oraclejavabot.features.taskstatus.dto.TaskStatusResponseDTO;
import com.oraclejavabot.features.taskstatus.model.TaskStatusEntity;
import com.oraclejavabot.features.taskstatus.repository.TaskStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    public List<TaskStatusResponseDTO> getTaskStatuses() {

        return taskStatusRepository.findAll()
                .stream()
                .map(s -> new TaskStatusResponseDTO(
                        s.getEstadoId(),
                        s.getNombre(),
                        s.getDescripcion(),
                        s.getEsActivo()
                ))
                .collect(Collectors.toList());
    }

    public TaskStatusResponseDTO createTaskStatus(TaskStatusRequestDTO request) {

        if (taskStatusRepository.existsByNombre(request.getNombre())) {
            throw new IllegalArgumentException("Ya existe un estado con ese nombre");
        }

        TaskStatusEntity status = new TaskStatusEntity();
        status.setEstadoId(request.getEstadoId());
        status.setNombre(request.getNombre());
        status.setDescripcion(request.getDescripcion());
        status.setEsActivo(request.getEsActivo());

        taskStatusRepository.save(status);

        return new TaskStatusResponseDTO(
                status.getEstadoId(),
                status.getNombre(),
                status.getDescripcion(),
                status.getEsActivo()
        );
    }
}