package com.oraclejavabot.messaging.producer;

import com.oraclejavabot.messaging.event.AiDuplicateDetectionRequestEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiDuplicateDetectionProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AiDuplicateDetectionProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDuplicateDetectionRequest(AiDuplicateDetectionRequestEvent event) {
        kafkaTemplate.send("ai-duplicate-detection-request", event);
    }
}