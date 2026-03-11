package com.oraclejavabot.features.taskpriorities.service;

import com.oraclejavabot.features.taskpriorities.dto.PriorityRequestDTO;
import com.oraclejavabot.features.taskpriorities.dto.PriorityResponseDTO;
import com.oraclejavabot.features.taskpriorities.model.PriorityEntity;
import com.oraclejavabot.features.taskpriorities.repository.PriorityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriorityService {

    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    public List<PriorityResponseDTO> getPriorities() {

        return priorityRepository.findAll()
                .stream()
                .map(p -> new PriorityResponseDTO(
                        p.getPrioridadId(),
                        p.getNombre(),
                        p.getDescripcion(),
                        p.getOrden()
                ))
                .collect(Collectors.toList());
    }

    public PriorityResponseDTO createPriority(PriorityRequestDTO request) {

        if (priorityRepository.existsByNombre(request.getNombre())) {
            throw new IllegalArgumentException("Ya existe una prioridad con ese nombre");
        }

        PriorityEntity priority = new PriorityEntity();
        priority.setPrioridadId(request.getPrioridadId());
        priority.setNombre(request.getNombre());
        priority.setDescripcion(request.getDescripcion());
        priority.setOrden(request.getOrden());

        priorityRepository.save(priority);

        return new PriorityResponseDTO(
                priority.getPrioridadId(),
                priority.getNombre(),
                priority.getDescripcion(),
                priority.getOrden()
        );
    }
}