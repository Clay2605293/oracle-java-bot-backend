package com.oraclejavabot.messaging.consumer;

import com.oraclejavabot.features.ai.service.AiSemanticDuplicateDetectionService;
import com.oraclejavabot.messaging.event.AiSemanticDuplicateDetectionResponseEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AiSemanticDuplicateDetectionResponseConsumer {

    private final AiSemanticDuplicateDetectionService aiSemanticDuplicateDetectionService;

    public AiSemanticDuplicateDetectionResponseConsumer(
            AiSemanticDuplicateDetectionService aiSemanticDuplicateDetectionService
    ) {
        this.aiSemanticDuplicateDetectionService = aiSemanticDuplicateDetectionService;
    }

    @KafkaListener(
            topics = "ai-semantic-duplicate-detection-response",
            containerFactory = "semanticDuplicateDetectionKafkaListenerContainerFactory"
    )
    public void consume(AiSemanticDuplicateDetectionResponseEvent event) {
        System.out.println("📥 Received AI semantic duplicate detection response for run: " + event.getRunId());

        try {
            int savedResults = aiSemanticDuplicateDetectionService.saveResultsFromAiResponse(event);

            System.out.println("✅ AI semantic duplicate detection results saved: " + savedResults);

        } catch (Exception e) {
            System.err.println("❌ Error saving AI semantic duplicate detection results: " + e.getMessage());
        }
    }
}
