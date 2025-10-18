package com.buberDinner.userService.eventListner;



import com.buberDinner.userService.event.HostCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Component
public class HostCreatedListener {

    private static final String TOPIC = "host-topic";
    private final KafkaTemplate<String, HostCreated> kafkaTemplate;

    @Autowired
    public HostCreatedListener(KafkaTemplate<String, HostCreated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener
    public void handleHostCreated(HostCreated event) {
        String key = event.id().toString();
        kafkaTemplate.send(TOPIC, key, event);
    }
}

