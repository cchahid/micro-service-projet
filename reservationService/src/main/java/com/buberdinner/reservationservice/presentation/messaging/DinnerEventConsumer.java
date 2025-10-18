package com.buberdinner.reservationservice.presentation.messaging;

import com.buberdinner.reservationservice.application.service.DinnerService;
import com.buberdinner.reservationservice.domain.module.Dinner;
import com.buberdinner.reservationservice.presentation.dto.DinnerCreatedEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class DinnerEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DinnerService dinnerService;

    public DinnerEventConsumer(DinnerService dinnerService) {
        this.dinnerService = dinnerService;
    }

    @KafkaListener(topics = "dinnerCreated", groupId = "reservation-group")
    public void handleDinnerCreated(String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            DinnerCreatedEvent event = mapper.readValue(payload, DinnerCreatedEvent.class);
            Dinner dinner = new Dinner();
            dinner.setId(event.dinner().id());
            dinner.setName(event.dinner().name());
            dinner.setDescription(event.dinner().description());
            dinner.setPrice(event.dinner().price());
            dinner.setStartTime(event.dinner().startTime());
            dinner.setEndTime(event.dinner().endTime());
            dinner.setAddress(event.dinner().address());
            dinner.setCuisineType(event.dinner().cuisineType());
            dinner.setMaxGuestCount(event.dinner().maxGuestCount());
            dinner.setStatus(event.dinner().status());
            dinner.setRating(event.dinner().rating());
            dinner.setHostId(event.dinner().hostId());
            dinner.setMenuId(event.dinner().menuId());

            dinnerService.createOrUpdateDinner(dinner);
            System.out.println("✅ Received dinner event: " + event);
        } catch (Exception e) {
            System.err.println("❌ Failed to parse event: " + e.getMessage());
        }
    }

}
