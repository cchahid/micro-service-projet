package com.buberdinner.reservationservice.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Kafka consumer configuration for ReservationService
 * Enables Kafka and configures consumer properties
 */
@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {

    // Consumer configuration is handled through application.properties
    // This class serves as a marker for Kafka consumer enabling

    public KafkaConsumerConfig() {
        log.info("Kafka Consumer Configuration initialized for ReservationService");
    }
}

