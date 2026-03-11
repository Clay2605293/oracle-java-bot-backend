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

    /**
     * Recupera todos los estados de tarea desde la base de datos y los mapea
     * a DTOs de respuesta.
     *
     * @return lista de {@link TaskStatusResponseDTO}
     */
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

    /**
     * Crea un nuevo estado de tarea. Valida que no exista un estado con el
     * mismo nombre antes de persistir.
     *
     * @param request DTO con los datos del estado a crear
     * @return DTO con los datos del estado creado
     * @throws IllegalArgumentException si ya existe un estado con el mismo nombre
     */
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