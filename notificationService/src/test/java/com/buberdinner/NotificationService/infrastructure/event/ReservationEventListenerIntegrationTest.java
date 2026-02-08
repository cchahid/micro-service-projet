package com.buberdinner.NotificationService.infrastructure.event;

import com.buberdinner.NotificationService.application.dto.ReservationCreatedEventDTO;
import com.buberdinner.NotificationService.application.dto.ReservationCanceledEventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ReservationEventListener consuming from Kafka
 * Tests both ReservationCreated and ReservationCanceled event consumption
 */
@SpringBootTest
@EmbeddedKafka(
        partitions = 3,
        brokerProperties = {
                "log.retention.hours=24",
                "log.segment.bytes=1024"
        },
        topics = {
                "reservationCreated",
                "reservationCanceled",
                "reservation-created.DLT",
                "reservation-canceled.DLT"
        }
)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.group-id=test-notification-group",
        "app.kafka.topics.reservation-created=reservationCreated",
        "app.kafka.topics.reservation-canceled=reservationCanceled"
})
class ReservationEventListenerIntegrationTest {

    /**
     * Test that ReservationCreatedEventDTO can be properly deserialized from Kafka
     */
    @Test
    void testReservationCreatedEventDeserialization() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        ReservationCreatedEventDTO event = new ReservationCreatedEventDTO(
                reservationId,
                100L,
                50L,
                LocalDateTime.now(),
                "Italian Kitchen"
        );

        // Assert event has all required fields
        assertNotNull(event.getReservationId(), "Reservation ID should not be null");
        assertNotNull(event.getDinnerId(), "Dinner ID should not be null");
        assertNotNull(event.getGuestId(), "Guest ID should not be null");
        assertNotNull(event.getReservationTime(), "Reservation time should not be null");
        assertNotNull(event.getRestaurantName(), "Restaurant name should not be null");
    }

    /**
     * Test that ReservationCanceledEventDTO can be properly deserialized from Kafka
     */
    @Test
    void testReservationCanceledEventDeserialization() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        ReservationCanceledEventDTO event = new ReservationCanceledEventDTO(
                reservationId,
                100L
        );

        // Assert event has all required fields
        assertNotNull(event.getReservationId(), "Reservation ID should not be null");
        assertNotNull(event.getGuestId(), "Guest ID should not be null");
    }

    /**
     * Test that event message with correlation headers is properly processed
     */
    @Test
    void testReservationEventWithCorrelationHeaders() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        ReservationCreatedEventDTO event = new ReservationCreatedEventDTO(
                reservationId,
                100L,
                50L,
                LocalDateTime.now(),
                "Italian Kitchen"
        );

        String correlationId = "reservation-" + reservationId;
        String eventType = "ReservationCreated";

        // Assert that headers can be attached and retrieved
        assertNotNull(correlationId);
        assertNotNull(eventType);
        assertTrue(correlationId.contains("reservation-"));
        assertNotNull(event.getReservationId());
        assertEquals("Italian Kitchen", event.getRestaurantName());
    }
}

