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
    public List<PriorityResponseDTO> getPriorities() {
        return priorityService.getPriorities();
    }

    @PostMapping
    public PriorityResponseDTO createPriority(@RequestBody PriorityRequestDTO request) {
        return priorityService.createPriority(request);
    }
}