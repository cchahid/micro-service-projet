package com.buberdinner.NotificationService.infrastructure.entrypoints.events;

import com.buberdinner.NotificationService.application.dto.DinnerEndedEventDTO;
import com.buberdinner.NotificationService.application.dto.DinnerStartedEventDTO;
import com.buberdinner.NotificationService.application.ports.input.NotificationInputPort;
import com.buberdinner.NotificationService.infrastructure.kafka.ErrorEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ADD THIS IMPORT
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j // ADD THIS ANNOTATION
public class DinnerEventListener {

    private final NotificationInputPort notificationInputPort;
    private final ErrorEventPublisher errorEventPublisher;



    @KafkaListener(topics = "${app.kafka.topics.dinner-started}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDinnerStarted(DinnerStartedEventDTO event) {
        log.info("Received DinnerStartedEvent: {}", event);
        notificationInputPort.handleDinnerStartedEvent(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.dinner-ended}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDinnerEnded(DinnerEndedEventDTO event) {
        log.info("Received DinnerEndedEvent: {}", event);
        notificationInputPort.handleDinnerEndedEvent(event);
    }

}