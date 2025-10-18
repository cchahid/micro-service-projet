package com.buberdinner.reservationservice.domain.event.listner;

import com.buberdinner.reservationservice.domain.event.ReservationCanceled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservationCanceledListner {

    private static final String TOPIC = "reservation-canceled";
    private final KafkaTemplate<String, ReservationCanceled> kafkaTemplate;

    @Autowired
    public ReservationCanceledListner(KafkaTemplate<String, ReservationCanceled> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener
    public void handleReservationCanceledEvent(ReservationCanceled event) {
        System.out.println("reservation-canceled event received:"+event.toString());
        kafkaTemplate.send(TOPIC, event);
    }
}
