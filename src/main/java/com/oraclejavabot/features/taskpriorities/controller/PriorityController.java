package com.oraclejavabot.features.taskpriorities.controller;

import com.oraclejavabot.features.taskpriorities.dto.PriorityRequestDTO;
import com.oraclejavabot.features.taskpriorities.dto.PriorityResponseDTO;
import com.oraclejavabot.features.taskpriorities.service.PriorityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/priorities")
public class PriorityController {

    private final PriorityService priorityService;

    public PriorityController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @GetMapping
    /**
     * Recupera la lista de prioridades disponibles.
     *
     * @return lista de {@link PriorityResponseDTO} con las prioridades almacenadas
     */
    public List<PriorityResponseDTO> getPriorities() {
        return priorityService.getPriorities();
    }

    @PostMapping
    /**
     * Crea una nueva prioridad a partir de los datos recibidos.
     *
     * @param request DTO con los datos para crear la prioridad
     * @return DTO con los datos de la prioridad creada
     * @throws IllegalArgumentException si ya existe una prioridad con el mismo
     *                                  nombre
     */
    public PriorityResponseDTO createPriority(@RequestBody PriorityRequestDTO request) {
        return priorityService.createPriority(request);
    }
}