package com.oraclejavabot.messaging.producer;

import com.oraclejavabot.messaging.event.UserAssignedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class TaskEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(TaskEventProducer.class);
    private static final String TASK_EVENTS_TOPIC = "task-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 🔥 ENVÍA EVENTO ENRIQUECIDO DE ASIGNACIÓN DE USUARIO
     */
    public void sendUserAssignedEvent(String taskId,
                                      String userId,
                                      String taskTitle,
                                      String projectName,
                                      String priority,
                                      String dueDate) {

        UserAssignedEvent event = new UserAssignedEvent(
                taskId,
                userId,
                taskTitle,
                projectName,
                priority,
                dueDate
        );

        logger.info(
                "Sending Kafka event → topic={}, taskId={}, userId={}, taskTitle={}, project={}",
                TASK_EVENTS_TOPIC,
                taskId,
                userId,
                taskTitle,
                projectName
        );

        try {
            SendResult<String, Object> result = kafkaTemplate
                    .send(TASK_EVENTS_TOPIC, taskId, event) // 🔑 taskId como key
                    .get();

            logger.info(
                    "Kafka event sent successfully → topic={}, partition={}, offset={}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );

        } catch (Exception e) {
            logger.error(
                    "Error sending Kafka event → taskId={}, userId={}",
                    taskId,
                    userId,
                    e
            );
            throw new IllegalStateException("Kafka event could not be sent", e);
        }
    }
}