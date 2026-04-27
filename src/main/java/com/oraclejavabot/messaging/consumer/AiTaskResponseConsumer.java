package com.oraclejavabot.messaging.consumer;

import com.oraclejavabot.features.ai.service.AiTaskSuggestionService;
import com.oraclejavabot.messaging.event.AiTaskGenerationResponseEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AiTaskResponseConsumer {

    private final AiTaskSuggestionService aiTaskSuggestionService;

    public AiTaskResponseConsumer(AiTaskSuggestionService aiTaskSuggestionService) {
        this.aiTaskSuggestionService = aiTaskSuggestionService;
    }

    @KafkaListener(
            topics = "ai-task-generation-response",
            containerFactory = "aiKafkaListenerContainerFactory"
    )
    public void consume(AiTaskGenerationResponseEvent event) {
        System.out.println("📥 Received AI response for project: " + event.getProjectId());

        try {
            int savedSuggestions = aiTaskSuggestionService.saveSuggestionsFromAiResponse(event);

            System.out.println("✅ AI suggestions saved: " + savedSuggestions);

        } catch (Exception e) {
            System.err.println("❌ Error saving AI suggestions: " + e.getMessage());
        }
    }
}