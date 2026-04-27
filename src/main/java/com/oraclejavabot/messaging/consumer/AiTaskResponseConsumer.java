package com.oraclejavabot.messaging.consumer;

import com.oraclejavabot.messaging.event.AiTaskGenerationResponseEvent;
import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.service.TaskService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        System.out.println("📥 Received AI response for project: " + event.getProjectId());

        for (AiTaskGenerationResponseEvent.Task task : event.getTasks()) {
            try {
                TaskRequestDTO dto = new TaskRequestDTO();
                dto.setTitulo(task.getTitulo());
                dto.setDescripcion(task.getDescripcion());
                dto.setTiempoEstimado(task.getTiempoEstimado());

                // Defaults que el manager completará después
                dto.setPrioridadId(2);
                dto.setEstadoId(1);
                dto.setFechaLimite(LocalDateTime.now().plusYears(1).toString());

                taskService.createTask(event.getProjectId(), dto);

                System.out.println("✅ AI task created: " + task.getTitulo());

            } catch (Exception e) {
                System.err.println("❌ Error creating AI task: " + e.getMessage());
            }
        }
    }
}
