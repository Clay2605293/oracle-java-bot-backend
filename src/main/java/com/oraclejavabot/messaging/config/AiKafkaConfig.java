package com.oraclejavabot.messaging.config;

import com.oraclejavabot.messaging.event.AiDuplicateDetectionResponseEvent;
import com.oraclejavabot.messaging.event.AiTaskGenerationResponseEvent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.oraclejavabot.messaging.event.AiSemanticDuplicateDetectionResponseEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AiKafkaConfig {

    private static final String BOOTSTRAP = "kafka:29092";

    private static final String AI_TASK_RESPONSE_GROUP_ID = "ai-response-group";
    private static final String AI_DUPLICATE_RESPONSE_GROUP_ID = "ai-duplicate-response-group";
    private static final String AI_SEMANTIC_DUPLICATE_RESPONSE_GROUP_ID = "ai-semantic-duplicate-response-group";

    @Bean
    public ConsumerFactory<String, AiTaskGenerationResponseEvent> aiConsumerFactory() {

        JsonDeserializer<AiTaskGenerationResponseEvent> deserializer =
                new JsonDeserializer<>(AiTaskGenerationResponseEvent.class);

        deserializer.addTrustedPackages("*");

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, AI_TASK_RESPONSE_GROUP_ID);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AiTaskGenerationResponseEvent>
    aiKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AiTaskGenerationResponseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(aiConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, AiDuplicateDetectionResponseEvent> aiDuplicateDetectionConsumerFactory() {

        JsonDeserializer<AiDuplicateDetectionResponseEvent> deserializer =
                new JsonDeserializer<>(AiDuplicateDetectionResponseEvent.class);

        deserializer.addTrustedPackages("*");

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, AI_DUPLICATE_RESPONSE_GROUP_ID);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AiDuplicateDetectionResponseEvent>
    aiDuplicateDetectionKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AiDuplicateDetectionResponseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(aiDuplicateDetectionConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, AiSemanticDuplicateDetectionResponseEvent>
    semanticDuplicateDetectionConsumerFactory() {

        JsonDeserializer<AiSemanticDuplicateDetectionResponseEvent> deserializer =
                new JsonDeserializer<>(AiSemanticDuplicateDetectionResponseEvent.class);

        deserializer.addTrustedPackages("*");

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, AI_SEMANTIC_DUPLICATE_RESPONSE_GROUP_ID);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AiSemanticDuplicateDetectionResponseEvent>
    semanticDuplicateDetectionKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AiSemanticDuplicateDetectionResponseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(semanticDuplicateDetectionConsumerFactory());

        return factory;
    }
}