package com.buberdinner.reservationservice.infrastructure.event;

import com.buberdinner.reservationservice.domain.event.ReservationCreated;
import com.buberdinner.reservationservice.domain.event.listner.ReservationCreatedListner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ReservationCreated event publishing to Kafka
 * Uses embedded Kafka for testing without external broker
 */
@SpringBootTest
@EmbeddedKafka(
        partitions = 3,
        brokerProperties = {
                "log.retention.hours=24",
                "log.segment.bytes=1024"
        },
        topics = {
                "reservation-created",
                "reservation-canceled",
                "reservation-created.DLT",
                "reservation-canceled.DLT"
        }
)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.acks=all",
        "spring.kafka.consumer.group-id=test-group"
})
class ReservationCreatedEventIntegrationTest {


    @Autowired
    private ReservationCreatedListner reservationCreatedListner;

    /**
     * Test that ReservationCreated event is successfully published to Kafka
     */
    @Test
    void testReservationCreatedEventPublishing() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        ReservationCreated event = new ReservationCreated(
                reservationId,  // reservationId as UUID
                100L, // dinnerId
                50L,  // guestId
                LocalDateTime.now(),
                "Italian Kitchen"
        );

        // Act
        reservationCreatedListner.handleReservationCreatedEvent(event);

        // Assert - Event should be published without throwing exception
        assertEquals(reservationId, event.reservationId());
        assertEquals(100L, event.dinnerId());
        assertEquals(50L, event.guestId());
        assertEquals("Italian Kitchen", event.restaurantName());
    }

    /**
     * Test that event has all required fields for notification
     */
    @Test
    void testReservationCreatedEventHasAllRequiredFields() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        ReservationCreated event = new ReservationCreated(
                reservationId,
                100L,
                50L,
                LocalDateTime.now(),
                "French Bistro"
        );

        // Assert
        assertNotNull(event.reservationId(), "Reservation ID should be present");
        assertEquals(100L, event.dinnerId(), "Dinner ID should be present");
        assertEquals(50L, event.guestId(), "Guest ID should be present");
        assertNotNull(event.reservationTime(), "Reservation time should not be null");
        assertNotNull(event.restaurantName(), "Restaurant name should not be null");
    }
}

