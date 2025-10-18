package com.buberdinner.dinnerservice.infrastructure.messaging;


import com.buberdinner.dinnerservice.domain.event.DinnerCreatedEvent;
import com.buberdinner.dinnerservice.domain.event.DinnerStartedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, DinnerStartedEvent> kafkaTemplate;
    private static final String TOPIC = "dinnerStarted";

    private final KafkaTemplate<String, DinnerCreatedEvent> kafkaTemplate2;
    public KafkaProducerService(KafkaTemplate<String, DinnerStartedEvent> kafkaTemplate, KafkaTemplate<String, DinnerCreatedEvent> kafkaTemplate2) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplate2 = kafkaTemplate2;
    }

    public void sendDinnerStartedEvent(DinnerStartedEvent event) {
        kafkaTemplate.send("dinnerStarted", event);
    }
    public void sendDinnerCreatedEvent(DinnerCreatedEvent event) { kafkaTemplate2.send("dinnerCreated", event);}
}

