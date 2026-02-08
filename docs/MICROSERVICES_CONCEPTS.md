# Microservices Concepts & Patterns Used in This Project

## Table of Contents
1. [Introduction](#introduction)
2. [Core Microservices Principles](#core-microservices-principles)
3. [Architecture Patterns](#architecture-patterns)
4. [Communication Patterns](#communication-patterns)
5. [Data Management Patterns](#data-management-patterns)
6. [Resilience Patterns](#resilience-patterns)
7. [Service Discovery & Registration](#service-discovery--registration)
8. [API Gateway Pattern](#api-gateway-pattern)
9. [Event-Driven Architecture](#event-driven-architecture)
10. [Design Patterns](#design-patterns)
11. [Implementation Examples](#implementation-examples)
12. [Best Practices](#best-practices)
13. [Glossary](#glossary)

---

## Introduction

This microservices project implements modern distributed system architecture patterns. It consists of five independent services (Reservation, Notification, Dinner, User, Discovery) that communicate asynchronously via Apache Kafka and synchronously via REST APIs discovered through Eureka service registry.

---

## Core Microservices Principles

### 1. **Single Responsibility Principle (SRP)**

**Definition:** Each microservice should have a single reason to change and should be responsible for only one aspect of the business domain.

**In This Project:**
- **Reservation Service** → Manages reservations only
- **Notification Service** → Manages notifications only
- **Dinner Service** → Manages dinner information only
- **User Service** → Manages user information only
- **Discovery Service** → Manages service registry only

**Benefits:**
- Easier to understand and maintain
- Smaller codebases per service
- Faster development cycles
- Independent deployment

### 2. **Loose Coupling**

**Definition:** Services should be independent of each other and should not be tightly bound by shared code, databases, or direct dependencies.

**In This Project:**
- Services communicate via Kafka (asynchronous)
- Services use REST APIs via service discovery (synchronous when needed)
- No shared databases between services
- No shared libraries except common interfaces

**Example:**
```
Before (Monolithic):
Reservation Service directly calls Notification Service
→ If Notification is down, Reservation fails

After (Decoupled):
Reservation Service → Kafka Topic → Notification Service
→ If Notification is down, messages queue in Kafka
→ Notification processes them when it recovers
```

### 3. **High Cohesion**

**Definition:** Related functionality should be grouped together within a service, but separated between services.

**In This Project:**
- All reservation logic is in Reservation Service
- All notification logic is in Notification Service
- Business logic related to the same entity is co-located

### 4. **Autonomous Services**

**Definition:** Each service should be independently deployable and runnable without depending on other services being deployed first.

**In This Project:**
- Each service has its own database (H2 or PostgreSQL)
- Each service has its own configuration files
- Each service can be started independently
- Each service manages its own data

**Example:**
```bash
# Services can run independently
java -jar reservationService.jar
java -jar notificationService.jar
# Both can run without the other being present
```

---

## Architecture Patterns

### 1. **Microservices Architecture (MSA)**

**Definition:** Architectural style that structures an application as a collection of loosely coupled, independently deployable services.

**Characteristics in This Project:**
- Multiple independent services
- Each service handles a specific business capability
- Services communicate via well-defined APIs
- Services can be developed and deployed independently
- Each service can use different technologies

**Contrast with Monolithic:**
```
Monolithic:
┌─────────────────────────────┐
│  Single Application          │
├─────────────────────────────┤
│ Reservation | Notification  │
│ Dinner | User | Discovery   │
└─────────────────────────────┘

Microservices:
┌──────────┐  ┌──────────┐  ┌──────────┐
│Reservation│  │Notification│ │ Dinner  │
└──────────┘  └──────────┘  └──────────┘
```

### 2. **Service-Oriented Architecture (SOA)**

**Definition:** An architectural approach where services provide business capabilities through well-defined service interfaces.

**In This Project:**
- Each service provides REST endpoints for external access
- Services are discovered via Eureka
- Services communicate through defined interfaces
- Services are independent but coordinate through messages

### 3. **Layered Architecture (per service)**

**Definition:** Each microservice is internally structured in layers (presentation, application, domain, infrastructure).

**In Reservation Service:**
```
┌─────────────────────────────┐
│  Presentation Layer         │
│ (ReservationController)     │
├─────────────────────────────┤
│  Application Layer          │
│ (ReservationService)        │
├─────────────────────────────┤
│  Domain Layer               │
│ (Reservation, DinnerId)     │
├─────────────────────────────┤
│  Infrastructure Layer       │
│ (Repository, Config, Mapper)│
└─────────────────────────────┘
```

---

## Communication Patterns

### 1. **Synchronous Communication (REST/HTTP)**

**Definition:** Services communicate directly with each other using HTTP requests and expect immediate responses.

**In This Project:**
- Reservation Service can call Dinner Service to get dinner details
- Services communicate via REST APIs
- Service discovery via Eureka allows finding other services

**Implementation:**
```java
// In Reservation Service
@FeignClient("dinnerService")  // Service discovery
public interface DinnerClient {
    @GetMapping("/api/dinners/{id}")
    Dinner getDinner(@PathVariable Long id);
}

// Usage
Dinner dinner = dinnerClient.getDinner(dinnerId);
```

**Advantages:**
- Simple to implement
- Request-response pattern is intuitive
- Direct feedback on success/failure

**Disadvantages:**
- Tight coupling (caller must know about callee)
- Cascading failures (if dinner service is down, reservation service fails)
- Higher latency (waits for response)

### 2. **Asynchronous Communication (Event-Driven)**

**Definition:** Services communicate through events published to message brokers. The sender doesn't wait for a response.

**In This Project:**
- When a reservation is created, an event is published to Kafka
- Notification Service subscribes to these events
- Services don't directly call each other

**Implementation:**
```
Reservation Service (Producer):
    ReservationCreated Event
          ↓
    Kafka Topic: "reservation-created"
          ↓
    Notification Service (Consumer)
        Processes event asynchronously
```

**Advantages:**
- Loose coupling (services don't know about each other)
- Resilience (if consumer is down, messages queue)
- Scalability (can add multiple consumers)
- Better performance (non-blocking)

**Disadvantages:**
- Eventual consistency (data not immediately synchronized)
- More complex to debug
- Message ordering challenges
- Requires monitoring of message processing

### 3. **Hybrid Communication**

**Definition:** Using both synchronous and asynchronous communication where appropriate.

**In This Project:**
- **Synchronous:** When immediate response is needed (e.g., validate dinner exists)
- **Asynchronous:** When order doesn't matter (e.g., send notification)

---

## Data Management Patterns

### 1. **Database per Service Pattern**

**Definition:** Each microservice owns its own database and other services cannot directly access it.

**In This Project:**
```
Reservation Service
    ├── Database: H2 (reservationdb)
    └── Tables: reservations, dinners

Notification Service
    ├── Database: H2 or PostgreSQL
    └── Tables: notifications, guests, hosts

User Service
    ├── Database: PostgreSQL
    └── Tables: users, credentials

Each service manages its own data exclusively
```

**Benefits:**
- Services can evolve their schema independently
- Different databases can be chosen per service
- Services can't accidentally corrupt each other's data
- Easier to scale individual services

**Challenges:**
- Distributed transactions become complex
- Data consistency becomes eventual
- Requires event synchronization

### 2. **Event Sourcing (Foundation)**

**Definition:** All changes to state are captured as immutable events, which are stored in a log. Current state is derived from replaying events.

**In This Project:**
- Domain events (ReservationCreated, ReservationCanceled) are published to Kafka
- These events form an audit trail
- Services can replay events to reconstruct state
- Kafka topics act as an event log

**Example:**
```
Event Log:
1. ReservationCreated(id=1, dinnerId=100, guestId=50)
2. ReservationCanceled(id=1, dinnerId=100, guestId=50)

Current State can be reconstructed by replaying events
```

### 3. **CQRS (Command Query Responsibility Segregation)**

**Definition:** Separate the models for updating data (Command) from reading data (Query).

**In This Project:**
- **Command Side:** ReservationServiceImpl creates/cancels reservations
- **Query Side:** ReservationController retrieves reservation details
- Events are published from commands
- Queries can be served from read-optimized models

**Benefits:**
- Can optimize read and write operations differently
- Can scale read and write independently
- Clearer separation of concerns

---

## Resilience Patterns

### 1. **Retry Pattern**

**Definition:** Automatically retry failed operations with configurable attempts and backoff strategies.

**In This Project:**
```java
// KafkaConsumerConfig.java
new FixedBackOff(1000L, 3)  // Retry 3 times, 1 second delay
```

**How it works:**
```
Message Processing Fails
    ↓
Retry #1 (wait 1s)
    ↓
Retry #2 (wait 1s)
    ↓
Retry #3 (wait 1s)
    ↓
All retries exhausted → Send to DLT
```

**Use Case:**
- Transient failures (network hiccup, temporary service unavailability)
- Not suitable for permanent failures

### 2. **Dead Letter Queue (DLT) Pattern**

**Definition:** Messages that fail to process are sent to a separate queue for later analysis and replay.

**In This Project:**
```
reservation-created
├── Success → offset committed
└── Failure (after 3 retries) → reservation-created.DLT

DLT (Dead Letter Topic):
├── 7-day retention
├── For manual analysis
└── For later replay after fixing the issue
```

**Benefits:**
- Prevents message loss
- Allows debugging of failures
- Enables replay when fixed

### 3. **Circuit Breaker Pattern**

**Definition:** Prevent cascading failures by stopping requests to failing services.

**In This Project:**
- Kafka acts as a circuit breaker
- If Notification Service is down, messages accumulate in Kafka
- Reservation Service continues to publish events
- No cascading failure

**States:**
```
Closed (Normal):
    Request → Service → Response

Open (Service Down):
    Request → Circuit Breaker → Fail Fast (Don't try calling service)

Half-Open (Recovery):
    Request → Circuit Breaker → Try calling service
    If success → Closed
    If failure → Open
```

### 4. **Timeout Pattern**

**Definition:** Set maximum time to wait for a response; if exceeded, consider the operation failed.

**In This Project:**
```yaml
spring:
  kafka:
    consumer:
      session-timeout-ms: 30000  # 30 second timeout
```

### 5. **Bulkhead Pattern**

**Definition:** Isolate elements of an application into pools so that if one fails, others continue operating.

**In This Project:**
```yaml
# Each service in its own container/process
- Reservation Service (port 8083)
- Notification Service (port 8085)
- Dinner Service (port 8084)

If one crashes, others continue
```

---

## Service Discovery & Registration

### 1. **Service Registry Pattern (Eureka)**

**Definition:** A central registry where services register themselves and clients can discover services dynamically.

**In This Project:**
```
Eureka Server (Discovery Service)
    ├── Reservation Service registered at: http://localhost:8083
    ├── Notification Service registered at: http://localhost:8085
    ├── Dinner Service registered at: http://localhost:8084
    └── User Service registered at: http://localhost:8087
```

**Benefits:**
- Dynamic service discovery (services can move without code changes)
- Load balancing (can route to any instance)
- Health checking (sick instances removed automatically)
- No need to hardcode service locations

**Example:**
```java
// Instead of hardcoding URL
// String url = "http://localhost:8084/api/dinners/{id}";

// Use service discovery
@FeignClient("dinnerService")  // Looks up dinnerService in Eureka
public interface DinnerClient {
    @GetMapping("/api/dinners/{id}")
    Dinner getDinner(@PathVariable Long id);
}
```

### 2. **Client-Side Service Discovery**

**Definition:** The client is responsible for finding the location of a service instance.

**In This Project:**
- Reservation Service (client) asks Eureka where Dinner Service is
- Eureka returns the URL
- Reservation Service connects to that URL

**Contrast with Server-Side:**
```
Client-Side (This Project):
Client → Eureka "Where is Dinner Service?" → Get URL
Client → Dinner Service directly

Server-Side:
Client → Load Balancer
Load Balancer → Eureka "Where is Dinner Service?"
Load Balancer → Dinner Service
```

---

## API Gateway Pattern

### 1. **API Gateway**

**Definition:** A single entry point for client requests. The gateway routes requests to appropriate microservices.

**In This Project:**
```
API Gateway (Port 8080)
    ├── GET /api/reservations → Routes to Reservation Service
    ├── GET /api/dinners → Routes to Dinner Service
    ├── GET /api/users → Routes to User Service
    └── GET /api/notifications → Routes to Notification Service
```

**Benefits:**
- Single entry point for clients
- Can add cross-cutting concerns (authentication, logging, rate limiting)
- Shields internal complexity from clients
- Service instances can change without client knowing

**In This Project:**
```
Client
    ↓
API Gateway (api-gateway service)
    ├─ Route to Reservation Service
    ├─ Route to Notification Service
    ├─ Route to Dinner Service
    ├─ Route to User Service
    └─ Service discovery via Eureka
```

---

## Event-Driven Architecture

### 1. **Event-Driven Architecture (Complete Implementation)**

**Definition:** Services primarily communicate through events. When something important happens, an event is published for other services to react.

**In This Project:**

#### **Event Types:**
```
Domain Events (Internal to a service):
    └── ReservationCreated, ReservationCanceled

Application Events (Shared across services):
    ├── ReservationCreatedEventDTO
    ├── ReservationCanceledEventDTO
    ├── DinnerStartedEventDTO
    ├── DinnerEndedEventDTO
    ├── GuestCreatedEventDTO
    └── HostCreatedEventDTO
```

#### **Event Flow:**
```
1. Reservation Created
   ↓
   ReservationServiceImpl.createReservation()
   ↓
   Domain Event: ReservationCreated
   ↓
   Event Listener: ReservationCreatedListner
   ↓
   Kafka Topic: reservation-created
   ↓
   Subscription: NotificationService
   ↓
   Send Email/Push to Guest
```

#### **Event Characteristics:**
- **Immutable:** Events represent something that happened and cannot be changed
- **Complete:** Events contain all information needed for downstream processing
- **Traced:** Each event has correlation ID for tracing
- **Timestamped:** When the event occurred
- **Typed:** Clear event type for filtering

### 2. **Pub-Sub (Publish-Subscribe) Pattern**

**Definition:** Publishers emit events to a topic; multiple subscribers can listen to the same topic without knowing about each other.

**In This Project:**
```
Publisher (Reservation Service):
    ReservationCreated event
    ↓
    Kafka Topic: "reservation-created"
    ↓
Subscribers:
    ├── Notification Service (sends email)
    ├── Analytics Service (collects metrics)
    └── Audit Service (logs for compliance)

All subscribers receive the same event independently
```

**Benefits:**
- Publishers don't need to know subscribers
- Subscribers can be added without changing publishers
- Decouples services
- Event can have multiple consequences

### 3. **Event Choreography vs Orchestration**

**In This Project (Event Choreography):**
```
Choreography (Decentralized):
Reservation Service publishes ReservationCreated
    ↓
Each service reacts independently:
    ├── Notification Service: Send notification
    ├── Analytics Service: Update metrics
    └── Audit Service: Log event

No central coordinator
Services know what events to listen to
```

**Alternative (Orchestration - Not used here):**
```
Orchestration (Centralized):
Reservation Service publishes ReservationCreated
    ↓
Orchestrator receives and decides next steps:
    ├── Tell Notification Service to send email
    ├── Tell Analytics Service to update metrics
    └── Tell Audit Service to log

Central coordinator controls flow
```

---

## Design Patterns

### 1. **Repository Pattern**

**Definition:** Provides an abstraction for data access, decoupling business logic from data persistence.

**In This Project:**
```java
// Abstract interface
public interface ReservationRepository {
    Reservation createReservation(Reservation reservation);
    Optional<Reservation> findById(ReservationId id);
    void deleteReservation(ReservationId id);
}

// Implementation details hidden
@Service
public class ReservationRepositoryImpl implements ReservationRepository {
    @Autowired
    private JpaReservationRepository jpaRepository;
    // Implementation uses JPA
}

// Usage
@Service
public class ReservationServiceImpl {
    @Autowired
    private ReservationRepository repository;  // Uses abstraction
}
```

**Benefits:**
- Can switch database implementations without changing service
- Easy to test (mock the repository)
- Single source of data access logic

### 2. **Mapper/Converter Pattern**

**Definition:** Convert between different object representations (Entity ↔ DTO ↔ Domain).

**In This Project:**
```java
// ReservationMapper.java
@Component
public class ReservationMapper {
    public ReservationResponse toResponseDto(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId().value(),
            reservation.getDinnerId().value(),
            reservation.getGuestId().value()
        );
    }
}

// Different layers use different representations
Presentation Layer: ReservationResponse (DTO)
    ↓ (mapped)
Domain Layer: Reservation (domain object)
    ↓ (mapped)
Persistence Layer: ReservationEntity (JPA entity)
```

**Benefits:**
- Isolates layers from each other
- Can evolve DTOs independently from domain objects
- Prevents data leakage between layers

### 3. **Value Object Pattern**

**Definition:** Objects that have no identity of their own but represent a value that is meaningful only in combination.

**In This Project:**
```java
public record ReservationId(long value) {
    public static ReservationId generate() {
        return new ReservationId(generateId());
    }
}

public record DinnerId(long value) {}

public record GuestId(long value) {}

// Benefits:
// - Type-safe (can't accidentally mix ReservationId with DinnerId)
// - Self-documenting
// - Encapsulates logic related to the value
```

**Benefits:**
- Type safety (prevents mixing wrong IDs)
- Self-documenting code
- Can encapsulate business logic

### 4. **Domain-Driven Design (DDD)**

**Definition:** Design software around the business domain, not technical layers.

**In This Project:**
```
Domain Layer (Reservation Bounded Context):
├── Aggregates: Reservation
├── Value Objects: ReservationId, DinnerId, GuestId
├── Domain Events: ReservationCreated, ReservationCanceled
└── Repositories: ReservationRepository

The domain language matches business language:
Reservation, Dinner, Guest, Notification
(Not Table, Row, Query)
```

### 5. **Adapter Pattern**

**Definition:** Convert the interface of a class into another interface clients expect. Allows incompatible interfaces to work together.

**In This Project:**
```java
// Domain model
public class Reservation {
    private ReservationId id;
    private DinnerId dinnerId;
}

// Persistence adapter (JPA)
@Entity
public class ReservationEntity {
    @Id
    private Long id;
    @Column(name = "dinner_id")
    private Long dinnerId;
}

// NotificationSenderAdapter converts domain to notification format
@Component
public class NotificationSenderAdapter implements NotificationSenderPort {
    @Autowired
    private EmailNotificationSender emailSender;
    
    @Override
    public void sendEmail(String to, String message) {
        // Adapts domain language to email sender API
        emailSender.send(to, message);
    }
}
```

### 6. **Dependency Injection Pattern**

**Definition:** Provide dependencies to a class rather than having it create them.

**In This Project:**
```java
// Without DI (wrong)
public class ReservationService {
    private ReservationRepository repository = new ReservationRepositoryImpl();
}

// With DI (correct)
@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository repository;
    
    @Autowired
    public ReservationServiceImpl(ReservationRepository repository) {
        this.repository = repository;
    }
}

// Benefits:
// - Loosely coupled
// - Easy to test (inject mock)
// - Easy to swap implementations
```

---

## Implementation Examples

### Example 1: Creating a Reservation (Synchronous + Asynchronous)

```
1. CLIENT REQUEST
   POST /api/reservations
   {
     "dinnerId": 1,
     "guestId": 100,
     "restaurantName": "Italian Kitchen"
   }

2. PRESENTATION LAYER (Controller)
   ReservationController.createReservation(CreateReservationCommand)

3. APPLICATION LAYER (Service)
   ReservationServiceImpl.createReservation()
   ├── Validate input
   ├── Check if dinner exists (SYNCHRONOUS call if needed)
   ├── Create Reservation in database
   └── Publish ReservationCreated event

4. DOMAIN LAYER (Event)
   ReservationCreated event contains:
   ├── reservationId
   ├── dinnerId
   ├── guestId
   ├── reservationTime
   └── restaurantName

5. EVENT LISTENER
   ReservationCreatedListner.handleReservationCreatedEvent()
   ├── Enrich event with headers (correlation-id, timestamps)
   └── Publish to Kafka Topic

6. KAFKA (Message Broker)
   Topic: reservation-created
   Message: ReservationCreatedEventDTO
   Partition: 0, 1, or 2 (round-robin)

7. NOTIFICATION SERVICE (Consumer)
   ReservationEventListener.consumeReservationCreated()
   ├── Deserialize event
   ├── Extract correlation ID
   └── Call NotificationInputPort.handleReservationCreatedEvent()

8. NOTIFICATION PROCESSING
   NotificationInputPort
   ├── Create Notification entity in database
   ├── Send email notification
   └── Log operation

9. RESPONSE
   Success: Notification sent to guest
   Failure: Message sent to DLT for analysis

10. MONITORING
    ├── Event correlated via correlation-id
    ├── Latency tracked (from creation to notification)
    └── Metrics collected (messages/second, errors)
```

### Example 2: Error Handling

```
SCENARIO: Notification Service is down

1. Reservation Service publishes ReservationCreated to Kafka
   (Service doesn't care if consumer is up or not)

2. Messages accumulate in reservation-created Kafka topic

3. When Notification Service comes back online:
   ├── Consumer reconnects
   ├── Reads accumulated messages
   └── Processes them

4. No message loss, no cascading failure
   (This is the power of asynchronous communication)

SCENARIO: Notification Service crashes during processing

1. Message is being processed
2. Unexpected error occurs
3. Exception is caught by DefaultErrorHandler
4. Retry mechanism kicks in:
   ├── Retry #1 (wait 1s)
   ├── Retry #2 (wait 1s)
   ├── Retry #3 (wait 1s)
5. After 3 retries, message sent to:
   reservation-created.DLT
6. Operator analyzes failure reason
7. Root cause fixed
8. Message replayed from DLT
9. Notification sent successfully
```

### Example 3: Service Discovery in Action

```
STARTUP SEQUENCE:

1. Discovery Service starts (Eureka Server)
   Listening on port 8761

2. Reservation Service starts
   ├── Registers with Eureka: "reservationService" at localhost:8083
   ├── Eureka returns list of available services
   └── Reservation Service ready

3. Notification Service starts
   ├── Registers with Eureka: "notificationService" at localhost:8085
   └── Ready to consume events

4. Client makes request
   GET /api/reservations/1
   ↓
   API Gateway queries Eureka: "Where is reservationService?"
   ↓
   Eureka returns: "http://localhost:8083"
   ↓
   API Gateway forwards request to http://localhost:8083
   ↓
   Response returned to client

SCALE UP SCENARIO:

5. Another Reservation Service instance starts on port 8083-2
   ├── Registers with Eureka
   └── Eureka has 2 instances of reservationService

6. Next client request:
   ├── API Gateway queries Eureka
   ├── Eureka returns both instances: [8083, 8083-2]
   ├── Load balancer picks one (round-robin)
   └── Request served by available instance

NO CODE CHANGES NEEDED!
Service locations changed but client code works unchanged.
```

---

## Best Practices

### 1. **Implement Idempotency**

**Definition:** Same operation can be executed multiple times with same result.

**Why it matters:**
- Messages may be processed multiple times (retries)
- If operation is idempotent, duplicates don't cause harm

**Implementation:**
```java
// Bad (not idempotent)
public void processReservation(ReservationCreatedEventDTO event) {
    // If called twice, creates notification twice
    Notification notification = new Notification(...);
    notificationRepository.save(notification);
}

// Good (idempotent)
public void processReservation(ReservationCreatedEventDTO event) {
    // Use correlation-id to prevent duplicates
    if (notificationRepository.existsByCorrelationId(event.getReservationId())) {
        return;  // Already processed
    }
    Notification notification = new Notification(...);
    notificationRepository.save(notification);
}
```

### 2. **Use Correlation IDs for Tracing**

**Definition:** Include a unique ID that follows a request through all services for debugging.

**In This Project:**
```java
// Every event gets a correlation ID
Message<ReservationCreated> message = MessageBuilder
    .withPayload(event)
    .setHeader("correlation-id", "reservation-" + event.reservationId())
    .build();

// Consumer extracts and logs it
@KafkaListener(...)
public void consumeReservationCreated(
    @Header(name = "correlation-id") String correlationId
) {
    log.info("Processing event with correlation-id: {}", correlationId);
    // Can now trace this correlation-id across all services
}
```

### 3. **Ensure Data Consistency Eventually**

**Definition:** In distributed systems, accept that data won't be immediately consistent everywhere, but will be eventually.

**In This Project:**
```
Reservation Service writes: reservation created
    ↓ (event published)
Kafka Topic stores event
    ↓ (event consumed)
Notification Service writes: notification created

Gap between "Reservation created" and "Notification created":
- This is acceptable in microservices
- Data is eventually consistent
```

### 4. **Monitor Everything**

**Definition:** Log, track metrics, and alert on important events.

**In This Project:**
```
Things to monitor:
├── Message publishing rate
├── Message consumption rate
├── Consumer lag
├── DLT message count (indicates failures)
├── Processing latency
├── Error rates
├── Service health (up/down)
└── Database connection pool usage
```

### 5. **Use Configuration Management**

**Definition:** Don't hardcode configuration; use external configuration.

**In This Project:**
```yaml
# application.properties (Reservation Service)
spring.kafka.bootstrap-servers=localhost:9092  # Configurable
spring.kafka.producer.acks=all

# application.yml (Notification Service)
app:
  kafka:
    topics:
      reservation-created: reservationCreated  # Can be changed without recompile
```

### 6. **Implement Proper Error Handling**

**Definition:** Handle errors gracefully, with proper logging and recovery mechanisms.

**In This Project:**
```java
@KafkaListener(...)
public void consumeReservationCreated(ReservationCreatedEventDTO event) {
    try {
        // Process event
        notificationInputPort.handleReservationCreatedEvent(event);
        log.info("Successfully processed event");
    } catch (DatabaseException e) {
        log.error("Database error processing event: {}", event, e);
        // Re-throw to trigger retry + DLT
        throw new RuntimeException("Failed to process event", e);
    } catch (ExternalServiceException e) {
        log.error("External service error: {}", event, e);
        // Re-throw to trigger retry
        throw new RuntimeException("External service failed", e);
    }
}
```

### 7. **Use Version Compatibility in Events**

**Definition:** Design events to handle schema evolution without breaking consumers.

**Example:**
```java
// V1 (Original)
public record ReservationCreated(
    long dinnerId,
    long guestId
) implements Serializable {}

// V2 (Enhanced) - backward compatible
public record ReservationCreated(
    long dinnerId,
    long guestId,
    long reservationId,           // NEW
    LocalDateTime reservationTime // NEW
) implements Serializable {}

// Old consumers still work (ignore new fields)
// New consumers can use new fields
```

### 8. **Document Your APIs**

**Definition:** Clearly document all REST endpoints and events.

**In This Project:**
```
REST Endpoints:
POST /api/reservations
├── Input: CreateReservationCommand
├── Output: ReservationCreatedResponse
└── Publishes: ReservationCreated event

Events:
ReservationCreated
├── Topic: reservation-created
├── Payload: reservationId, dinnerId, guestId, reservationTime
└── Consumers: Notification Service, Analytics Service
```

---

## Glossary

| Term | Definition |
|------|-----------|
| **Microservice** | Small, independent service that handles one business capability |
| **Event** | Something important that happened (immutable, timestamped) |
| **Kafka** | Distributed message broker for event streaming |
| **Topic** | Channel in Kafka where events are published |
| **Partition** | Subset of a topic's data, stored separately for parallelism |
| **Consumer Group** | Group of consumers that share consuming from a topic |
| **Dead Letter Queue (DLT)** | Queue for messages that failed processing |
| **Event Sourcing** | Storing all state changes as events |
| **CQRS** | Separating read model from write model |
| **Service Discovery** | Mechanism to find service location dynamically |
| **Eureka** | Spring Cloud service registry |
| **API Gateway** | Single entry point for all client requests |
| **Idempotent** | Operation with same effect when run once or multiple times |
| **Circuit Breaker** | Pattern that prevents cascading failures |
| **Correlation ID** | Unique ID that follows request through services |
| **Repository Pattern** | Abstract data access from business logic |
| **Domain-Driven Design** | Design around business domain, not technical layers |
| **Value Object** | Object with no identity, represents a value |
| **Bounded Context** | Explicit boundary within which a model applies |
| **Resilience** | System's ability to handle failures gracefully |
| **Loose Coupling** | Services independent of each other |
| **High Cohesion** | Related functionality grouped together |
| **Synchronous** | Caller waits for response |
| **Asynchronous** | Caller doesn't wait for response |
| **Pub-Sub** | Publish-Subscribe communication pattern |
| **Choreography** | Decentralized event-driven coordination |
| **Orchestration** | Centralized coordination of services |

---

## Conclusion

This project demonstrates modern microservices architecture combining:

1. **Loose Coupling** - Services independent via async events
2. **High Scalability** - Parallel processing via Kafka partitions
3. **Resilience** - Retry logic, DLT, circuit breakers
4. **Observability** - Correlation IDs, detailed logging
5. **Maintainability** - Clear separation of concerns, DDD
6. **Flexibility** - Easy to add new services, change implementations

The combination of Spring Boot, Kafka, Eureka, and event-driven architecture provides a robust foundation for building scalable, resilient microservices systems.

---

**Last Updated:** February 8, 2026
**Status:** Comprehensive Guide
**For Questions:** Refer to project documentation files

