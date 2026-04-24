package com.oraclejavabot.features.ai.controller;

import com.oraclejavabot.messaging.event.AiTaskGenerationRequestEvent;
import com.oraclejavabot.messaging.producer.AiTaskProducer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiTaskProducer producer;

    public AiController(AiTaskProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/generate-backlog")
    public String generateBacklog() {

        // 🔥 Mock inicial (luego lo conectamos a Project real)
        AiTaskGenerationRequestEvent event = new AiTaskGenerationRequestEvent();

        event.setProjectId("123");
        event.setProjectName("Oracle Java Bot");
        event.setProjectDescription("Sistema de productividad");

        AiTaskGenerationRequestEvent.Document doc = new AiTaskGenerationRequestEvent.Document();
        doc.setType("SRS");
        doc.setContent("El sistema debe generar backlog automáticamente.");

        event.setDocuments(List.of(doc));

        producer.sendTaskGenerationRequest(event);

        return "AI task generation event sent to Kafka";
    }
}