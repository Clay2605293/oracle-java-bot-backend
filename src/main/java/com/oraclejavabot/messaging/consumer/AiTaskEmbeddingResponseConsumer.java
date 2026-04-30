package com.oraclejavabot.messaging.consumer;

import com.oraclejavabot.features.ai.service.TaskEmbeddingService;
import com.oraclejavabot.messaging.event.AiTaskEmbeddingResponseEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AiTaskEmbeddingResponseConsumer {

    private final TaskEmbeddingService taskEmbeddingService;

    public AiTaskEmbeddingResponseConsumer(TaskEmbeddingService taskEmbeddingService) {
        this.taskEmbeddingService = taskEmbeddingService;
    }

    @KafkaListener(
            topics = "ai-task-embedding-response",
            containerFactory = "taskEmbeddingKafkaListenerContainerFactory"
    )
    public void consume(AiTaskEmbeddingResponseEvent event) {
        System.out.println("📥 Received AI task embedding response for task: " + event.getTaskId());

        try {
            taskEmbeddingService.saveEmbeddingFromAiResponse(event);

            System.out.println("✅ Task embedding saved for task: " + event.getTaskId());

        } catch (Exception e) {
            System.err.println("❌ Error saving task embedding: " + e.getMessage());
        }
    }
}
