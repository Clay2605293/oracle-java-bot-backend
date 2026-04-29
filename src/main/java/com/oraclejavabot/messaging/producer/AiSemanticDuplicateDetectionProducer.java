package com.oraclejavabot.messaging.producer;

import com.oraclejavabot.messaging.event.AiSemanticDuplicateDetectionRequestEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiSemanticDuplicateDetectionProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AiSemanticDuplicateDetectionProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSemanticDuplicateDetectionRequest(
            AiSemanticDuplicateDetectionRequestEvent event
    ) {
        kafkaTemplate.send("ai-semantic-duplicate-detection-request", event);
    }
}
