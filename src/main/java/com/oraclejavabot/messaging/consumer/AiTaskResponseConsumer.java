package com.oraclejavabot.messaging.consumer;

import com.oraclejavabot.messaging.event.AiTaskGenerationResponseEvent;
import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.service.TaskService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AiTaskResponseConsumer {

    private final TaskService taskService;

    public AiTaskResponseConsumer(TaskService taskService) {
        this.taskService = taskService;
    }

    @KafkaListener(
        topics = "ai-task-generation-response",
        containerFactory = "aiKafkaListenerContainerFactory"
    )
    public void consume(AiTaskGenerationResponseEvent event) {

        System.out.println("📥 Received AI response: " + event);

        // 🔥 MVP: usuario fijo (luego se mejora)
        String systemUserId = "SYSTEM_AI";

        for (AiTaskGenerationResponseEvent.Task task : event.getTasks()) {

            try {

                TaskRequestDTO dto = new TaskRequestDTO();

                dto.setTitulo(task.getTitulo());
                dto.setDescripcion(task.getDescripcion());

                // prioridad
                dto.setPrioridadId(mapPriority(task.getPriority()));

                // 🔥 FIX 1: usar Double
                dto.setTiempoEstimado(
                        task.getEstimatedHours() != null
                                ? task.getEstimatedHours()
                                : 1.0
                );

                // estado default
                dto.setEstadoId(1);

                // ⚠️ NO seteamos projectId aquí (tu DTO no lo tiene)

                // 🔥 FIX 2: método correcto
                taskService.createTask(systemUserId, dto);

            } catch (Exception e) {
                System.err.println("❌ Error creating task: " + e.getMessage());
            }
        }
    }

    private int mapPriority(String priority) {
        return switch (priority) {
            case "HIGH" -> 1;
            case "MEDIUM" -> 2;
            case "LOW" -> 3;
            default -> 2;
        };
    }
}