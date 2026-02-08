# Event-Driven Communication Implementation Guide

## Overview
This document describes the complete event-driven communication system between the Reservation and Notification services using Apache Kafka.

## Architecture

### System Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    Reservation Service                          │
│                                                                  │
│  1. Create/Cancel Reservation                                  │
│     ↓                                                            │
│  2. Publish Domain Event (ReservationCreated/Canceled)          │
│     ↓                                                            │
│  3. Event Listener captures event                               │
│     ↓                                                            │
│  4. Publishes to Kafka Topic                                    │
│     ↓                                                            │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  Kafka Topics                                          │    │
│  │  - reservation-created (3 partitions)                 │    │
│  │  - reservation-canceled (3 partitions)                │    │
│  │  - reservation-created.DLT (1 partition)              │    │
│  │  - reservation-canceled.DLT (1 partition)             │    │
│  └────────────────────────────────────────────────────────┘    │
│                      │                                           │
└──────────────────────┼───────────────────────────────────────────┘
                       │
                       │ Kafka Message Broker
                       │
┌──────────────────────┼───────────────────────────────────────────┐
│                      ↓                                            │
│              Notification Service                                │
│                                                                  │
│  1. Kafka Consumer listens to topics                             │
│     ↓                                                            │
│  2. Receives ReservationCreatedEventDTO                          │
│     ↓                                                            │
│  3. NotificationInputPort processes event                        │
│     ↓                                                            │
│  4. Sends Notification (Email/Push)                             │
│     ↓                                                            │
│  5. On Success → Confirm & Log                                  │
│  6. On Failure → Retry (3 times) → DLT                          │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

## Key Components

### 1. Domain Events (Reservation Service)

#### ReservationCreated Event
```java
public record ReservationCreated(
    long reservationId,
    long dinnerId,
    long guestId,
    LocalDateTime reservationTime,
    String restaurantName
) implements Serializable
```

#### ReservationCanceled Event
```java
public record ReservationCanceled(
    long reservationId,
    long dinnerId,
    long guestId
) implements Serializable
```

### 2. Event Listeners (Reservation Service)

#### ReservationCreatedListner
- Listens to `ReservationCreated` domain events
- Enriches event with headers (correlation-id, event-timestamp, event-type)
- Publishes to `reservation-created` Kafka topic
- Handles failures with detailed logging

#### ReservationCanceledListner
- Listens to `ReservationCanceled` domain events
- Enriches event with correlation tracking headers
- Publishes to `reservation-canceled` Kafka topic
- Handles failures with detailed logging

### 3. Kafka Configuration

#### Reservation Service (Producer)

**KafkaProducerConfig:**
- Configures separate producer factories for each event type
- Sets ACK mode to "all" for durability
- Enables retries (3 times) with linger settings for batching
- JSON serialization for event payloads

**KafkaConfig:**
- Creates topics with 3 partitions for parallel processing
- Configures 24-hour message retention
- Creates Dead Letter Topics (DLT) with 7-day retention

#### Notification Service (Consumer)

**KafkaConsumerConfig:**
- Generic consumer factory for Object deserialization
- Specialized consumer factory for GuestCreatedEventDTO
- Error handler with DeadLetterPublishingRecoverer
- Retry logic: 3 retries with 1-second intervals
- Creates DLT for all topics

**application.yml:**
```yaml
app:
  kafka:
    topics:
      reservation-created: reservationCreated
      reservation-canceled: reservationCanceled
```

### 4. Event DTOs (Notification Service)

**ReservationCreatedEventDTO:**
- Used by consumers to deserialize Kafka messages
- Contains: reservationId, dinnerId, guestId, reservationTime, restaurantName
- Matches ReservationCreated domain event structure

**ReservationCanceledEventDTO:**
- Used by consumers to deserialize cancel events
- Contains: reservationId, dinnerId, guestId

### 5. Event Listeners (Notification Service)

**ReservationEventListener:**
- Consumes `reservation-created` and `reservation-canceled` topics
- Extracts Kafka headers for correlation tracking
- Calls NotificationInputPort for processing
- Error handling with ErrorEventPublisher
- On failure: retries 3 times, then sends to DLT

## Configuration Properties

### Reservation Service (application.properties)

```properties
# Kafka Producer
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.consumer.group-id=reservation-service-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.producer.acks=all
spring.kafka.producer.retries=3
```

### Notification Service (application.yml)

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-group
      auto-offset-reset: earliest
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.use.type.headers: false
        spring.json.trusted.packages: "*"
```

## Event Flow Example

### 1. Reservation Creation

```
POST /api/reservations
{
  "dinnerId": 1,
  "guestId": 100,
  "restaurantName": "Italian Kitchen"
}

↓

ReservationServiceImpl.createReservation()
- Creates reservation in database
- Publishes ReservationCreated event

↓

ReservationCreatedListner.handleReservationCreatedEvent()
- Enriches event with headers:
  - correlation-id: "reservation-{reservationId}"
  - event-timestamp: current time
  - event-type: "ReservationCreated"
- Publishes to "reservation-created" topic

↓

ReservationEventListener (NotificationService)
- Consumes message from topic
- Calls notificationInputPort.handleReservationCreatedEvent()
- Creates notification record
- Sends email/push notification
- Confirms message processing

↓

Email/Push sent to guest
```

### 2. On Failure

```
If notification processing fails:

↓

DefaultErrorHandler catches exception
- Retries immediately (1-second delay)
- Retries 2 more times (3 total)

↓

After 3 failed retries:

↓

DeadLetterPublishingRecoverer:
- Publishes failed message to "reservation-created.DLT"
- Logs error details
- Triggers monitoring/alerting

↓

Dead Letter Topic Analysis:
- Analyze failure reasons
- Implement fixes
- Replay messages from DLT
```

## Error Handling Strategy

### Retry Policy
- **Max Retries:** 3
- **Backoff:** Fixed 1-second delay between retries
- **Total Wait Time:** ~3 seconds before DLT

### Dead Letter Topics
- **Name Convention:** `{original-topic}.DLT`
- **Partitions:** 1 (no need for parallel processing on failures)
- **Retention:** 7 days
- **Purpose:** Store failed messages for manual analysis and replay

### Error Logging
- All failures logged with:
  - Correlation ID for tracing
  - Event details
  - Exception message and stack trace
  - Topic, partition, and offset information
  - Retry count

## Monitoring & Observability

### Key Metrics
1. **Message Publishing:**
   - Messages sent per topic
   - Publishing latency
   - Failed publishes

2. **Message Consumption:**
   - Messages consumed per second
   - Consumer lag
   - Processing latency
   - DLT message count

3. **Error Tracking:**
   - Retry count
   - DLT push rate
   - Error types distribution

### Log Statements

**Reservation Service:**
```
[ReservationCreatedListner] Publishing ReservationCreated event to Kafka - Reservation ID: {id}, Guest ID: {guestId}
[ReservationCreatedListner] Successfully published ReservationCreated event - Partition: {p}, Offset: {o}
[ReservationCreatedListner] Failed to publish ReservationCreated event for Reservation ID: {id}
```

**Notification Service:**
```
[ReservationEventListener] Received ReservationCreatedEvent from topic: {topic}, partition: {p}, offset: {o}
[ReservationEventListener] Successfully processed ReservationCreatedEvent - Reservation ID: {id}
[ReservationEventListener] Failed to process ReservationCreatedEvent - Reservation ID: {id}, Error: {error}
```

## Deployment Considerations

### Docker Compose
Ensure Kafka broker is running:
```yaml
kafka:
  image: confluentinc/cp-kafka:latest
  ports:
    - "9092:9092"
  environment:
    KAFKA_BROKER_ID: 1
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```

### Topic Creation
Topics are auto-created via Spring configuration on application startup.

### Scaling Considerations
- **Partitions:** 3 per main topic allows parallel processing
- **Consumer Groups:** Each service has its own group
- **Concurrency:** Notification service processes 3 messages in parallel

## Testing

### Unit Tests
- Mock KafkaTemplate and test listener behavior
- Verify event enrichment and header injection

### Integration Tests
- Use @EmbeddedKafka for in-memory Kafka
- Test end-to-end flow from creation to notification
- Verify DLT behavior with simulated failures

### Example Test
```java
@SpringBootTest
@EmbeddedKafka(partitions = 3, topics = {"reservation-created"})
class ReservationEventListenerTest {
    
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;
    
    @Test
    void testReservationCreatedEventProcessing() {
        // Publish test event
        // Assert notification was sent
    }
}
```

## Troubleshooting

### Messages Not Being Consumed
1. Check Kafka broker connectivity
2. Verify topic exists: `kafka-topics --list --bootstrap-server localhost:9092`
3. Check consumer group lag: `kafka-consumer-groups --describe --group notification-group`
4. Verify deserializer configuration

### High DLT Message Count
1. Check application logs for error patterns
2. Verify notification service is running
3. Check database connectivity
4. Review email/push service configuration

### Message Ordering Issues
1. Use partitionKey to ensure messages for same reservation go to same partition
2. Single partition topics for ordered processing
3. Consumer should be configured with concurrency = 1 if ordering is critical

## Future Enhancements

1. **Message Tracing:** Add OpenTelemetry for distributed tracing
2. **Idempotency:** Implement idempotent notification processing
3. **Exactly-Once Semantics:** Use transactional outbox pattern
4. **Filtering:** Add event filtering based on guest preferences
5. **Persistence:** Store event sourcing log for audit trail
6. **Batch Processing:** Implement batch processing for high-volume scenarios

## References

- Apache Kafka: https://kafka.apache.org/
- Spring Kafka: https://spring.io/projects/spring-kafka
- Event-Driven Architecture: https://martinfowler.com/articles/201701-event-driven.html
- Transactional Outbox Pattern: https://microservices.io/patterns/data/transactional-outbox.html

