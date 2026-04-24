package com.oraclejavabot.messaging.producer;

import com.oraclejavabot.messaging.event.AiTaskGenerationRequestEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiTaskProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AiTaskProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskGenerationRequest(AiTaskGenerationRequestEvent event) {
        kafkaTemplate.send("ai-task-generation-request", event);
    }
}