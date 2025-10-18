package com.buberDinner.userService.eventListner;

import com.buberDinner.userService.event.GuestCreated;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;


@Component
public class GuestCreatedListener {

    private static final String TOPIC = "guest-topic";
    private final KafkaTemplate<String, GuestCreated> kafkaTemplate;

    @Autowired
    public GuestCreatedListener(KafkaTemplate<String, GuestCreated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener
    public void handleGuestCreated(GuestCreated event) {
        // Vous pouvez choisir une clé logique (ex. l’ID du guest)
        String key = event.id().toString();
        kafkaTemplate.send(TOPIC, key, event);
    }
}

