package com.buberdinner.reservationservice.infrastructure.config;

import com.buberdinner.reservationservice.domain.event.ReservationCanceled;
import com.buberdinner.reservationservice.domain.event.ReservationCreated;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer Configuration for ReservationService
 * Configures producers for ReservationCreated and ReservationCanceled events
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Producer Factory for ReservationCreated events
     */
    @Bean
    public ProducerFactory<String, ReservationCreated> reservationCreatedProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 100); // Batch messages
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Kafka Template for ReservationCreated events
     */
    @Bean
    public KafkaTemplate<String, ReservationCreated> reservationCreatedKafkaTemplate() {
        return new KafkaTemplate<>(reservationCreatedProducerFactory());
    }

    /**
     * Producer Factory for ReservationCanceled events
     */
    @Bean
    public ProducerFactory<String, ReservationCanceled> reservationCanceledProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Kafka Template for ReservationCanceled events
     */
    @Bean
    public KafkaTemplate<String, ReservationCanceled> reservationCanceledKafkaTemplate() {
        return new KafkaTemplate<>(reservationCanceledProducerFactory());
    }

    /**
     * Generic producer factory for general purpose
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Generic Kafka Template for general purpose
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

