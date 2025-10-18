package com.buberdinner.NotificationService.infrastructure.entrypoints.events;

import com.buberdinner.NotificationService.application.dto.GuestCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.HostCreatedEventDTO;
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
public class UserEventListener {

    private final NotificationInputPort notificationInputPort;
    private final ErrorEventPublisher errorEventPublisher;



    @KafkaListener(topics = "${app.kafka.topics.guest-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeGuestCreated(GuestCreatedEventDTO event) {
        log.info("Received GuestCreatedEvent: {}", event);
        System.out.println("--- Sending Push Notification ---");
        notificationInputPort.handleGuestCreatedEvent(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.host-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeHostRaw(ConsumerRecord<String, Object> record) {
        Object obj = record.value();
        if (obj instanceof HostCreatedEventDTO host) {
            notificationInputPort.handleHostCreatedEvent(host);
        } else {
            log.error("❌ Mauvais type reçu dans host-topic : {}", obj.getClass().getSimpleName());
            errorEventPublisher.publish(record, "INVALID_EVENT_FOR_HOST_TOPIC");
        }
    }


}