package com.oraclejavabot.messaging.config;

import com.oraclejavabot.messaging.event.AiTaskGenerationResponseEvent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AiKafkaConfig {

    private static final String BOOTSTRAP = "kafka:29092";
    private static final String GROUP_ID = "ai-response-group";

    @Bean
    public ConsumerFactory<String, AiTaskGenerationResponseEvent> aiConsumerFactory() {

        JsonDeserializer<AiTaskGenerationResponseEvent> deserializer =
                new JsonDeserializer<>(AiTaskGenerationResponseEvent.class);

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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AiTaskGenerationResponseEvent>
    aiKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AiTaskGenerationResponseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(aiConsumerFactory());

        return factory;
    }
}