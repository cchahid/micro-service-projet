package com.buberdinner.reservationservice.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfig {


    @Bean
    public NewTopic reservationCreatedTopic() {
        return TopicBuilder.name("reservation-created").build();
    }

    @Bean
    public NewTopic reservationCanceledTopic() {
        return TopicBuilder.name("reservation-canceled").build();
    }
}
