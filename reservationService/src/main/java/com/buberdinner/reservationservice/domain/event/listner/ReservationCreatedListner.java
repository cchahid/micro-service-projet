package com.buberdinner.reservationservice.domain.event.listner;

import com.buberdinner.reservationservice.domain.event.ReservationCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservationCreatedListner {

    private static final String TOPIC = "reservation-created";
    private final KafkaTemplate<String, ReservationCreated> kafkaTemplate;

    @Autowired
    public ReservationCreatedListner(KafkaTemplate<String, ReservationCreated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }



    @EventListener
    public void handleReservationCreatedEvent(ReservationCreated event) {
        System.out.println("reservation-created event received:"+event.toString());
        kafkaTemplate.send(TOPIC, event);

    }
}
