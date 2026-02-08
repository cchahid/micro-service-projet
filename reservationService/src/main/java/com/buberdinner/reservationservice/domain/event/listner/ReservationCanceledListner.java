package com.buberdinner.reservationservice.domain.event.listner;

import com.buberdinner.reservationservice.domain.event.ReservationCanceled;
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
 * Listener for ReservationCanceled domain events.
 * Handles publication to Kafka topic "reservation-canceled"
 */
@Component
@Slf4j
public class ReservationCanceledListner {

    private static final String TOPIC = "reservation-canceled";
    private final KafkaTemplate<String, ReservationCanceled> kafkaTemplate;

    @Autowired
    public ReservationCanceledListner(KafkaTemplate<String, ReservationCanceled> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Handles the ReservationCanceled event and publishes it to Kafka
     * @param event The ReservationCanceled event
     */
    @EventListener
    public void handleReservationCanceledEvent(ReservationCanceled event) {
        try {
            log.info("Publishing ReservationCanceled event to Kafka - Reservation ID: {}, Guest ID: {}, Dinner ID: {}",
                    event.reservationId(), event.guestId(), event.dinnerId());

            // Create a message with headers for correlation and tracing
            Message<ReservationCanceled> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    .setHeader("event-timestamp", Instant.now().toString())
                    .setHeader("event-type", "ReservationCanceled")
                    .setHeader("correlation-id", "reservation-" + event.reservationId())
                    .build();

            kafkaTemplate.send(message).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published ReservationCanceled event - Partition: {}, Offset: {}",
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish ReservationCanceled event for Reservation ID: {}",
                            event.reservationId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error handling ReservationCanceled event for Reservation ID: {}",
                    event.reservationId(), e);
            throw new RuntimeException("Failed to publish ReservationCanceled event", e);
        }
    }
}
