package com.oraclejavabot.messaging.consumer;

import com.oraclejavabot.features.ai.service.AiDuplicateDetectionService;
import com.oraclejavabot.messaging.event.AiDuplicateDetectionResponseEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AiDuplicateDetectionResponseConsumer {

    private final AiDuplicateDetectionService aiDuplicateDetectionService;

    public AiDuplicateDetectionResponseConsumer(
            AiDuplicateDetectionService aiDuplicateDetectionService
    ) {
        this.aiDuplicateDetectionService = aiDuplicateDetectionService;
    }

    @KafkaListener(
            topics = "ai-duplicate-detection-response",
            containerFactory = "aiDuplicateDetectionKafkaListenerContainerFactory"
    )
    public void consume(AiDuplicateDetectionResponseEvent event) {
        System.out.println("📥 Received AI duplicate detection response for run: " + event.getRunId());

        try {
            int savedResults = aiDuplicateDetectionService.saveResultsFromAiResponse(event);

            System.out.println("✅ AI duplicate detection results saved: " + savedResults);

        } catch (Exception e) {
            System.err.println("❌ Error saving AI duplicate detection results: " + e.getMessage());
        }
    }
}