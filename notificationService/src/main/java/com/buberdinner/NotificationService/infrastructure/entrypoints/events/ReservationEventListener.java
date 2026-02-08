package com.buberdinner.NotificationService.infrastructure.entrypoints.events;

import com.buberdinner.NotificationService.application.dto.ReservationCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.ReservationCanceledEventDTO;
import com.buberdinner.NotificationService.application.ports.input.NotificationInputPort;
import com.buberdinner.NotificationService.infrastructure.kafka.ErrorEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka event listener for reservation-related events.
 * Consumes ReservationCreated and ReservationCanceled events and triggers notification processing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventListener {

    private final NotificationInputPort notificationInputPort;
    private final ErrorEventPublisher errorEventPublisher;

    /**
     * Consumes ReservationCreated events and triggers notification creation
     * Includes error handling and logging with correlation IDs
     */
    @KafkaListener(
            topics = "${app.kafka.topics.reservation-created}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeReservationCreated(
            @Payload ReservationCreatedEventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(name = "correlation-id", required = false) String correlationId) {

        try {
            log.info("Received ReservationCreatedEvent from topic: {}, partition: {}, offset: {} - Event: {}",
                    topic, partition, offset, event);

            if (event.getReservationId() == null) {
                log.warn("ReservationCreatedEvent has null reservationId, using dinnerId as fallback");
            }

            // Process the event
            notificationInputPort.handleReservationCreatedEvent(event);

            log.info("Successfully processed ReservationCreatedEvent - Reservation ID: {}, Guest ID: {}",
                    event.getReservationId(), event.getGuestId());

        } catch (Exception e) {
            log.error("Failed to process ReservationCreatedEvent - Reservation ID: {}, Error: {}",
                    event.getReservationId(), e.getMessage(), e);

            // Re-throw to trigger DLT handling
            throw new RuntimeException("Failed to process ReservationCreatedEvent", e);
        }
    }

    /**
     * Consumes ReservationCanceled events and triggers notification creation
     * Includes error handling and logging with correlation IDs
     */
    @KafkaListener(
            topics = "${app.kafka.topics.reservation-canceled}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeReservationCanceled(
            @Payload ReservationCanceledEventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(name = "correlation-id", required = false) String correlationId) {

        try {
            log.info("Received ReservationCanceledEvent from topic: {}, partition: {}, offset: {} - Event: {}",
                    topic, partition, offset, event);

            // Process the event
            notificationInputPort.handleReservationCanceledEvent(event);

            log.info("Successfully processed ReservationCanceledEvent - Reservation ID: {}, Guest ID: {}",
                    event.getReservationId(), event.getGuestId());

        } catch (Exception e) {
            log.error("Failed to process ReservationCanceledEvent - Reservation ID: {}, Error: {}",
                    event.getReservationId(), e.getMessage(), e);

            // Re-throw to trigger DLT handling
            throw new RuntimeException("Failed to process ReservationCanceledEvent", e);
        }
    }
}

