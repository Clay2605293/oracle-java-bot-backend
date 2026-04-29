package com.oraclejavabot.messaging.config;

import com.oraclejavabot.messaging.event.UserAssignedEvent;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    private static final String BOOTSTRAP = "kafka:29092";
    private static final String GROUP_ID = "oracle-java-bot-group";

    // =============================
    // PRODUCER
    // =============================
    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // =============================
    // CONSUMER
    // =============================
    @Bean
    public ConsumerFactory<String, UserAssignedEvent> consumerFactory() {

        JsonDeserializer<UserAssignedEvent> deserializer =
                new JsonDeserializer<>(UserAssignedEvent.class);

        deserializer.addTrustedPackages("*");

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    // =============================
    // LISTENER FACTORY
    // =============================
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserAssignedEvent>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserAssignedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    // =============================
    // 🔥 AUTO-CREATE TOPIC
    // =============================
    @Bean
    public NewTopic taskEventsTopic() {
        return TopicBuilder.name("task-events")
                .partitions(3)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic aiTaskGenerationRequestTopic() {
        return TopicBuilder.name("ai-task-generation-request")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aiTaskGenerationResponseTopic() {
        return TopicBuilder.name("ai-task-generation-response")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aiDuplicateDetectionRequestTopic() {
        return TopicBuilder.name("ai-duplicate-detection-request")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aiDuplicateDetectionResponseTopic() {
        return TopicBuilder.name("ai-duplicate-detection-response")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aiSemanticDuplicateDetectionRequestTopic() {
        return TopicBuilder.name("ai-semantic-duplicate-detection-request")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aiSemanticDuplicateDetectionResponseTopic() {
        return TopicBuilder.name("ai-semantic-duplicate-detection-response")
                .partitions(1)
                .replicas(1)
                .build();
    }
    
}