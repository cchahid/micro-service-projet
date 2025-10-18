package com.buberdinner.NotificationService.infrastructure.entrypoints.events;

import com.buberdinner.NotificationService.application.dto.ReservationCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.ReservationCanceledEventDTO; // New import
import com.buberdinner.NotificationService.application.ports.input.NotificationInputPort;
import com.buberdinner.NotificationService.infrastructure.kafka.ErrorEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventListener {

    private final NotificationInputPort notificationInputPort;
    private final ErrorEventPublisher errorEventPublisher;



    @KafkaListener(topics = "${app.kafka.topics.reservation-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeReservationCreated(ReservationCreatedEventDTO event) {
        log.info("Received ReservationCreatedEvent: {}", event);
        notificationInputPort.handleReservationCreatedEvent(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.reservation-canceled}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeReservationCanceled(ReservationCanceledEventDTO event) {
        log.info("Received ReservationCanceledEvent: {}", event);
        notificationInputPort.handleReservationCanceledEvent(event);
    }
}