package com.buberdinner.dinnerservice.infrastructure.config;

import com.buberdinner.dinnerservice.domain.event.DinnerStartedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

//    @Bean
//    public ProducerFactory<String, DinnerStartedEvent> producerFactory() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory<>(config);
//    }

    @Bean
    public NewTopic dinnerStarted() {
        return TopicBuilder.name("dinnerStarted").build();
    }

    @Bean
    public NewTopic dinnerCreated() {
        return TopicBuilder.name("dinnerCreated").build();
    }

    @Bean
    public NewTopic dinnerUpdated() {
        return TopicBuilder.name("dinnerUpdated").build();
    }

    @Bean
    public NewTopic dinnerEnded() {
        return TopicBuilder.name("dinnerEnded").build();
    }
}
