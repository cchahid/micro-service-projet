package com.buberdinner.reservationservice.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Topic Configuration for ReservationService
 * Defines topics for reservation events with proper partitioning and replication
 */
@Configuration
public class KafkaConfig {

    /**
     * Topic for ReservationCreated events
     * - 3 partitions for parallel processing
     * - 1 replica for single-node environment (increase for production)
     */
    @Bean
    public NewTopic reservationCreatedTopic() {
        return TopicBuilder
                .name("reservation-created")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", String.valueOf(86400000)) // 24 hours retention
                .config("cleanup.policy", "delete")
                .build();
    }

    /**
     * Topic for ReservationCanceled events
     * - 3 partitions for parallel processing
     * - 1 replica for single-node environment
     */
    @Bean
    public NewTopic reservationCanceledTopic() {
        return TopicBuilder
                .name("reservation-canceled")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", String.valueOf(86400000)) // 24 hours retention
                .config("cleanup.policy", "delete")
                .build();
    }

    /**
     * Dead Letter Topic (DLT) for failed reservation-created events
     */
    @Bean
    public NewTopic reservationCreatedDltTopic() {
        return TopicBuilder
                .name("reservation-created.DLT")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(604800000)) // 7 days retention for DLT
                .build();
    }

    /**
     * Dead Letter Topic (DLT) for failed reservation-canceled events
     */
    @Bean
    public NewTopic reservationCanceledDltTopic() {
        return TopicBuilder
                .name("reservation-canceled.DLT")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(604800000)) // 7 days retention for DLT
                .build();
    }
}
