package com.oraclejavabot.messaging.producer;

import com.oraclejavabot.messaging.event.AiTaskEmbeddingRequestEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiTaskEmbeddingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AiTaskEmbeddingProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskEmbeddingRequest(AiTaskEmbeddingRequestEvent event) {
        kafkaTemplate.send("ai-task-embedding-request", event);
    }
}