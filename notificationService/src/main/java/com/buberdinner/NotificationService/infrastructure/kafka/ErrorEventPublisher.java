package com.buberdinner.NotificationService.infrastructure.kafka;

import lombok.Value;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor; // Explicitly add NoArgsConstructor if needed for deserialization outside @Value
import lombok.AllArgsConstructor; // Explicitly add AllArgsConstructor if needed for constructor matching

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(ConsumerRecord<?, ?> originalRecord, String errorType) {
        // Ensure the constructor matches the arguments provided
        ErrorEvent errorEvent = new ErrorEvent(
                originalRecord.value(),
                errorType,
                System.currentTimeMillis()
        );
        kafkaTemplate.send("error.events", errorEvent);
    }

    // Fix for ErrorEvent: @Value already implies an all-args constructor.
    // If you explicitly needed a no-args constructor for deserialization,
    // you would add @NoArgsConstructor, but it would then require @AllArgsConstructor
    // for the other constructor. For this use case, @Value is sufficient.
    @Value // Implies @Getter, @EqualsAndHashCode, @ToString, and an all-args constructor
    public static class ErrorEvent {
        Object originalEvent; // Can be generic Object as per ConsumerRecord.value()
        String errorType;
        long timestamp;
    }
}