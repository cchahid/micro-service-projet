package com.buberdinner.dinnerservice.domain.event.EventListner;

import com.buberdinner.dinnerservice.domain.event.*;
import com.buberdinner.dinnerservice.infrastructure.messaging.KafkaProducerService;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DinnerEventListner {



    public DinnerEventListner(KafkaTemplate<String, DinnerCreatedEvent> kafkaTemplate, KafkaTemplate<String, DinnerUpdatedEvent> kafkaTemplate2, KafkaTemplate<String, DinnerStartedEvent> kafkaTemplate31, KafkaTemplate<String, DinnerCompletedEvent> kafkaTemplate4) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplate2 = kafkaTemplate2;
        this.kafkaTemplate3 = kafkaTemplate31;
        this.kafkaTemplate4 = kafkaTemplate4;
    }

    private final KafkaTemplate<String, DinnerCreatedEvent> kafkaTemplate;
    private final KafkaTemplate<String, DinnerUpdatedEvent> kafkaTemplate2;
    private final KafkaTemplate<String, DinnerStartedEvent> kafkaTemplate3;
    private final KafkaTemplate<String, DinnerCompletedEvent> kafkaTemplate4;


    @EventListener
    public void onDinnerCreated(DinnerCreatedEvent event) {

        kafkaTemplate.send("dinnerCreated", event);
        System.out.println("service notification asy" + event.dinner().toString());
    }

    @EventListener
    public void onDinnerStarted(DinnerStartedEvent event) {
        kafkaTemplate3.send("dinnerStarted", event);
        System.out.println("service notification asy" + event);
    }

    @EventListener
    public void onDinnerCompleted(DinnerCompletedEvent event) {
        kafkaTemplate4.send("dinnerCompleted", event);
        System.out.println("service de notification asy" + event.dinnerId());
    }



    @EventListener
    public void onDinnerUpdated(DinnerUpdatedEvent event) {
        kafkaTemplate2.send("dinnerUpdated", event);
        System.out.println(" Dinner updated: " + event.dinner().getName());
    }

//    @EventListener
//    public void onDinnerDeleted(DinnerDeletedEvent event) {
//        //System.out.println("Dinner deleted: " + event.dinner().getName());
//    }
}
