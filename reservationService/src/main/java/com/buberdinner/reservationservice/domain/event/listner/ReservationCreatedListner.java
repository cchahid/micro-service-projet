package com.buberdinner.reservationservice.domain.event.listner;

import com.buberdinner.reservationservice.domain.event.ReservationCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Listener for ReservationCreated domain events.
 * Handles publication to Kafka topic "reservation-created"
 */
@Component
@Slf4j
public class ReservationCreatedListner {

    private static final String TOPIC = "reservation-created";
    private final KafkaTemplate<String, ReservationCreated> kafkaTemplate;

    @Autowired
    public ReservationCreatedListner(KafkaTemplate<String, ReservationCreated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Handles the ReservationCreated event and publishes it to Kafka
     * @param event The ReservationCreated event
     */
    @EventListener
    public void handleReservationCreatedEvent(ReservationCreated event) {
        try {
            log.info("Publishing ReservationCreated event to Kafka - Reservation ID: {}, Guest ID: {}, Dinner ID: {}",
                    event.reservationId(), event.guestId(), event.dinnerId());

            // Create a message with headers for correlation and tracing
            Message<ReservationCreated> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    .setHeader("event-timestamp", Instant.now().toString())
                    .setHeader("event-type", "ReservationCreated")
                    .setHeader("correlation-id", "reservation-" + event.reservationId())
                    .build();

            kafkaTemplate.send(message).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published ReservationCreated event - Partition: {}, Offset: {}",
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish ReservationCreated event for Reservation ID: {}",
                            event.reservationId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error handling ReservationCreated event for Reservation ID: {}",
                    event.reservationId(), e);
            throw new RuntimeException("Failed to publish ReservationCreated event", e);
        }
    }
}

