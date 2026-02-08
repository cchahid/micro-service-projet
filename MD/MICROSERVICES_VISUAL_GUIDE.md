# Microservices Concepts - Visual Reference Guide

## Architecture Overview Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           CLIENT APPLICATIONS                           │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │
                    ┌────────────────▼──────────────┐
                    │      API GATEWAY              │
                    │     (Port 8080)               │
                    └────┬───┬──────┬──────┬────────┘
                         │   │      │      │
         ┌───────────────┘   │      │      └──────────────────┐
         │                   │      │                         │
         ▼                   ▼      ▼                         ▼
    ┌─────────┐        ┌──────────┐  ┌──────────┐      ┌──────────┐
    │Reservation     │ Notification  │ Dinner   │      │  User    │
    │ Service    │        Service   │ Service  │      │ Service  │
    │(8083)      │       (8085)     │(8084)    │      │(8087)    │
    └──────┬─────┘        ▲         └─────────┘      └──────────┘
           │              │
           │ publishes    │ consumes
           │              │
         ┌─┴──────────────┴──────────────────────┐
         │   Kafka Message Broker                │
         │                                        │
         │  Topics:                              │
         │  • reservation-created (3 partitions)│
         │  • reservation-canceled (3 partitions)
         │  • DLT topics (dead letter queues)    │
         └──────────────────────────────────────┘
           ▲              ▲              ▲
           │              │              │
           └──────────────┼──────────────┘
                    ┌─────▼──────┐
                    │   Eureka   │
                    │  Service   │
                    │ Registry   │
                    │(8761)      │
                    └────────────┘
                  (Service Discovery)
```

## Microservices Principles Visual

```
┌────────────────────────────────────────────────────────────────┐
│                  MICROSERVICES CHARACTERISTICS                 │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. SINGLE RESPONSIBILITY                                      │
│     ┌─────────────┐   ┌──────────────┐   ┌──────────┐        │
│     │Reservation  │   │ Notification │   │ Dinner   │        │
│     │Service      │   │ Service      │   │ Service  │        │
│     │             │   │              │   │          │        │
│     │Manages:     │   │Manages:      │   │Manages:  │        │
│     │• Create     │   │• Send Email  │   │• Dinner  │        │
│     │• Cancel     │   │• Send Push   │   │  Info    │        │
│     └─────────────┘   └──────────────┘   └──────────┘        │
│                                                                 │
│  2. LOOSE COUPLING                                             │
│     ┌─────────────────────────────────────┐                    │
│     │  Don't directly call each other      │                    │
│     │  Communicate via events/messages     │                    │
│     │  Each service has own database      │                    │
│     │  No shared libraries                 │                    │
│     └─────────────────────────────────────┘                    │
│                                                                 │
│  3. HIGH COHESION                                              │
│     ┌─────────────┐                                            │
│     │ Reservation │                                            │
│     │  Service    │                                            │
│     ├─────────────┤                                            │
│     │ Related:    │                                            │
│     │ • Domain    │                                            │
│     │ • Service   │                                            │
│     │ • Controller│                                            │
│     │ • Repo      │                                            │
│     │ • Config    │                                            │
│     └─────────────┘                                            │
│     (All related to Reservations)                             │
│                                                                 │
│  4. AUTONOMOUS SERVICES                                        │
│     ┌─────────────────────────────────────┐                    │
│     │  Each service:                       │                    │
│     │  • Has own database                  │                    │
│     │  • Can be deployed independently     │                    │
│     │  • Can be started independently      │                    │
│     │  • Can be scaled independently       │                    │
│     └─────────────────────────────────────┘                    │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

## Communication Patterns Comparison

```
┌──────────────────────────────────────────────────────────────┐
│                  SYNCHRONOUS vs ASYNCHRONOUS                 │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  SYNCHRONOUS (Direct API Call)                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Service A                  Service B                │   │
│  │      │                           │                   │   │
│  │      └────────── Request ───────→│                   │   │
│  │      ←───────── Response ────────┘                   │   │
│  │                 (waits here)                         │   │
│  │                                                       │   │
│  │  ✓ Immediate response                              │   │
│  │  ✓ Simple to implement                             │   │
│  │  ✗ Tight coupling                                   │   │
│  │  ✗ Cascading failures                              │   │
│  │  ✗ Slower (waits for response)                      │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
│  ASYNCHRONOUS (Event-Driven via Kafka)                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Service A              Kafka Topic          Service B   │
│  │      │                      │                   │       │
│  │      └─── Publish Event ───→│ (queues)          │       │
│  │      (doesn't wait)         │                   │       │
│  │                             └─ Consume Event ──→│       │
│  │                                (asynchronous)    │       │
│  │                                                       │   │
│  │  ✓ Loose coupling                                  │   │
│  │  ✓ Resilient (queued if consumer down)             │   │
│  │  ✓ Faster (non-blocking)                           │   │
│  │  ✗ Eventual consistency                            │   │
│  │  ✗ More complex                                     │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

## Event Flow Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                         COMPLETE EVENT FLOW                            │
├────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  STEP 1: Client Request                                              │
│  ┌────────────────────┐                                              │
│  │ POST /api/         │                                              │
│  │ reservations       │                                              │
│  └─────────┬──────────┘                                              │
│            │                                                          │
│  STEP 2: Presentation Layer                                          │
│  ┌─────────▼──────────┐                                              │
│  │ Controller         │                                              │
│  │ Validates input    │                                              │
│  └─────────┬──────────┘                                              │
│            │                                                          │
│  STEP 3: Application Layer                                           │
│  ┌─────────▼──────────┐                                              │
│  │ Service            │                                              │
│  │ • Save to DB       │                                              │
│  │ • Publish Event    │                                              │
│  └─────────┬──────────┘                                              │
│            │                                                          │
│  STEP 4: Domain Event Published                                      │
│  ┌─────────▼──────────────────────┐                                  │
│  │ ReservationCreated             │                                  │
│  │ {                              │                                  │
│  │   reservationId: 1,            │                                  │
│  │   dinnerId: 100,               │                                  │
│  │   guestId: 50,                 │                                  │
│  │   reservationTime: 2026-02-08  │                                  │
│  │ }                              │                                  │
│  └─────────┬──────────────────────┘                                  │
│            │                                                          │
│  STEP 5: Event Listener Processes Event                              │
│  ┌─────────▼────────────────────────────┐                            │
│  │ ReservationCreatedListener           │                            │
│  │ • Enriches with headers:             │                            │
│  │   - correlation-id: "reservation-1"  │                            │
│  │   - event-timestamp: now             │                            │
│  │   - event-type: "ReservationCreated" │                            │
│  │ • Sends to Kafka                     │                            │
│  └─────────┬────────────────────────────┘                            │
│            │                                                          │
│  STEP 6: Published to Kafka Topic                                    │
│  ┌─────────▼────────────────────────────┐                            │
│  │ Kafka Topic: "reservation-created"   │                            │
│  │                                      │                            │
│  │ ┌──────┬──────┬──────┐              │                            │
│  │ │ P0   │ P1   │ P2   │ (Partitions) │                            │
│  │ │[Msg] │[Msg] │      │              │                            │
│  │ └──────┴──────┴──────┘              │                            │
│  └─────────┬────────────────────────────┘                            │
│            │                                                          │
│  STEP 7: Consumer Subscribed                                         │
│  ┌─────────▼────────────────────────────┐                            │
│  │ NotificationService                  │                            │
│  │ (Subscribed to "reservation-created")│                            │
│  │                                      │                            │
│  │ Receives message when partition P0   │                            │
│  │ is assigned to consumer              │                            │
│  └─────────┬────────────────────────────┘                            │
│            │                                                          │
│  STEP 8: Event Listener Consumes                                     │
│  ┌─────────▼────────────────────────────┐                            │
│  │ ReservationEventListener             │                            │
│  │ • Deserializes event                 │                            │
│  │ • Extracts correlation-id            │                            │
│  │ • Calls NotificationInputPort        │                            │
│  └─────────┬────────────────────────────┘                            │
│            │                                                          │
│  STEP 9: Process Event                                               │
│  ┌─────────▼────────────────────────────┐                            │
│  │ NotificationInputPort                │                            │
│  │ • Create notification record         │                            │
│  │ • Send email/push                    │                            │
│  │ • Commit offset                      │                            │
│  └─────────┬────────────────────────────┘                            │
│            │                                                          │
│  STEP 10: Final State                                                │
│  ┌─────────▼────────────────────────────┐                            │
│  │ SUCCESS:                             │                            │
│  │ ✓ Reservation saved                  │                            │
│  │ ✓ Event published to Kafka           │                            │
│  │ ✓ Notification sent to guest         │                            │
│  │ ✓ Message offset committed           │                            │
│  │                                      │                            │
│  │ FAILURE:                             │                            │
│  │ ✗ Exception caught                   │                            │
│  │ ✗ Retry attempt #1 (wait 1s)         │                            │
│  │ ✗ Retry attempt #2 (wait 1s)         │                            │
│  │ ✗ Retry attempt #3 (wait 1s)         │                            │
│  │ ✗ Message sent to DLT (dead letter)  │                            │
│  │ ✗ Logged for manual review           │                            │
│  └────────────────────────────────────────┘                            │
│                                                                         │
└────────────────────────────────────────────────────────────────────────┘
```

## Data Consistency Model

```
┌──────────────────────────────────────────────────────────────┐
│              EVENTUAL CONSISTENCY (Not Immediate)            │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  Timeline:                                                   │
│                                                               │
│  T=0s    Reservation created in Database                    │
│  │       ✓ ReservationDB has reservation                    │
│  │       ✗ NotificationDB doesn't have notification yet     │
│  │                                                            │
│  T=0.1s  Event published to Kafka                           │
│  │       ✓ ReservationDB has reservation                    │
│  │       ✗ NotificationDB doesn't have notification yet     │
│  │                                                            │
│  T=0.5s  Notification Service consumes and processes        │
│  │       ✓ ReservationDB has reservation                    │
│  │       ✓ NotificationDB has notification                  │
│  │       ✓ Email sent                                        │
│  │       ✓ EVENTUAL CONSISTENCY ACHIEVED                    │
│  │                                                            │
│  Total latency: ~500ms (acceptable)                         │
│                                                               │
│  Contrast with Synchronous:                                 │
│  T=0s    Client calls ReservationService                    │
│  │       ReservationService calls NotificationService       │
│  │       NotificationService calls EmailService             │
│  │       EmailService returns                               │
│  │       NotificationService returns                        │
│  │       ReservationService returns                         │
│  │                                                            │
│  T=2s    Response returned to client                        │
│  │       Total latency: ~2s (slower, tightly coupled)       │
│  │                                                            │
└──────────────────────────────────────────────────────────────┘
```

## Service Discovery Flow

```
┌──────────────────────────────────────────────────────────────┐
│              SERVICE DISCOVERY WITH EUREKA                   │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  REGISTRATION PHASE                                          │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ Reservation Service starts                              ││
│  │ ┌────────────────────────────────────────────────────┐ ││
│  │ │ Sends: Register "reservationService"               │ ││
│  │ │        at http://localhost:8083                    │ ││
│  │ │        with health check endpoint                  │ ││
│  │ └────────┬───────────────────────────────────────────┘ ││
│  │          │                                             ││
│  │          ▼                                             ││
│  │ ┌────────────────────────────────────────────────────┐ ││
│  │ │ Eureka Server                                      │ ││
│  │ │ ┌──────────────────────────────────────────────┐  │ ││
│  │ │ │ Service Registry:                            │  │ ││
│  │ │ │ reservationService → localhost:8083         │  │ ││
│  │ │ │ notificationService → localhost:8085        │  │ ││
│  │ │ │ dinnerService → localhost:8084              │  │ ││
│  │ │ │ userService → localhost:8087                │  │ ││
│  │ │ └──────────────────────────────────────────────┘  │ ││
│  │ └────────────────────────────────────────────────────┘ ││
│  └─────────────────────────────────────────────────────────┘│
│                                                               │
│  DISCOVERY PHASE                                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ Client makes request:                                   ││
│  │ GET /api/reservations/1                                ││
│  │                                                          ││
│  │ ┌────────────────────────────────────────────────────┐ ││
│  │ │ API Gateway receives request                      │ ││
│  │ │ Queries Eureka: "Where is reservationService?"   │ ││
│  │ └────────┬───────────────────────────────────────────┘ ││
│  │          │                                             ││
│  │          ▼                                             ││
│  │ ┌────────────────────────────────────────────────────┐ ││
│  │ │ Eureka Server responds:                           │ ││
│  │ │ "reservationService is at http://localhost:8083" │ ││
│  │ └────────┬───────────────────────────────────────────┘ ││
│  │          │                                             ││
│  │          ▼                                             ││
│  │ ┌────────────────────────────────────────────────────┐ ││
│  │ │ API Gateway forwards request to:                 │ ││
│  │ │ http://localhost:8083/api/reservations/1        │ ││
│  │ │                                                   │ ││
│  │ │ Reservation Service returns response             │ ││
│  │ │ API Gateway returns response to client           │ ││
│  │ └────────────────────────────────────────────────────┘ ││
│  └─────────────────────────────────────────────────────────┘│
│                                                               │
│  SCALE UP SCENARIO                                           │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ Another Reservation Service instance starts on 8083-2   ││
│  │                                                          ││
│  │ Eureka Registry updates:                                ││
│  │ reservationService:                                      ││
│  │ ├── Instance 1: localhost:8083                          ││
│  │ └── Instance 2: localhost:8083-2                        ││
│  │                                                          ││
│  │ API Gateway load balances:                              ││
│  │ Request 1 → Instance 1                                  ││
│  │ Request 2 → Instance 2                                  ││
│  │ Request 3 → Instance 1                                  ││
│  │ (Round robin)                                           ││
│  │                                                          ││
│  │ NO CODE CHANGES NEEDED!                                 ││
│  └─────────────────────────────────────────────────────────┘│
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

## Resilience Pattern: Error Handling

```
┌──────────────────────────────────────────────────────────────┐
│              RESILIENCE THROUGH DEAD LETTER QUEUE             │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  SCENARIO: Message Processing Fails                         │
│                                                               │
│  ┌─────────────────────────────────────┐                    │
│  │ Message arrives at consumer         │                    │
│  │ Notification Service processes      │                    │
│  └────────────┬────────────────────────┘                    │
│               │                                              │
│               ▼ Exception!                                  │
│  ┌─────────────────────────────────────┐                    │
│  │ DefaultErrorHandler catches error   │                    │
│  │ Retry attempt #1                    │                    │
│  │ [Wait 1 second]                     │                    │
│  └────────────┬────────────────────────┘                    │
│               │                                              │
│               ▼ Still fails!                                │
│  ┌─────────────────────────────────────┐                    │
│  │ Retry attempt #2                    │                    │
│  │ [Wait 1 second]                     │                    │
│  └────────────┬────────────────────────┘                    │
│               │                                              │
│               ▼ Still fails!                                │
│  ┌─────────────────────────────────────┐                    │
│  │ Retry attempt #3                    │                    │
│  │ [Wait 1 second]                     │                    │
│  └────────────┬────────────────────────┘                    │
│               │                                              │
│               ▼ Still fails!                                │
│  ┌─────────────────────────────────────┐                    │
│  │ All retries exhausted               │                    │
│  │ DeadLetterPublishingRecoverer       │                    │
│  │ publishes to DLT                    │                    │
│  └────────────┬────────────────────────┘                    │
│               │                                              │
│               ▼                                              │
│  ┌─────────────────────────────────────┐                    │
│  │ Dead Letter Topic (DLT)             │                    │
│  │ Topic: reservation-created.DLT      │                    │
│  │                                     │                    │
│  │ Message stored:                     │                    │
│  │ • Original payload                  │                    │
│  │ • Exception details                 │                    │
│  │ • Retry count                       │                    │
│  │ • Timestamp                         │                    │
│  │ • Source topic                      │                    │
│  └────────────┬────────────────────────┘                    │
│               │                                              │
│               ▼                                              │
│  ┌─────────────────────────────────────┐                    │
│  │ Operator Analysis (Manual)          │                    │
│  │                                     │                    │
│  │ 1. Check DLT for failed messages    │                    │
│  │ 2. Analyze exception details        │                    │
│  │ 3. Fix root cause                   │                    │
│  │ 4. Replay message                   │                    │
│  │ 5. Verify processing success        │                    │
│  └────────────┬────────────────────────┘                    │
│               │                                              │
│               ▼                                              │
│  ┌─────────────────────────────────────┐                    │
│  │ Message Replay                      │                    │
│  │ Message read from DLT and           │                    │
│  │ reprocessed by consumer             │                    │
│  │ This time, processing succeeds!     │                    │
│  └─────────────────────────────────────┘                    │
│                                                               │
│  RESULT: NO MESSAGE LOSS!                                    │
│  The system is resilient to transient failures.             │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

## Layered Architecture per Service

```
┌────────────────────────────────────────────────────────────────┐
│              LAYERED ARCHITECTURE (Reservation Service)        │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │ PRESENTATION LAYER                                       │ │
│  │ ┌────────────────────────────────────────────────────┐   │ │
│  │ │ ReservationController                             │   │ │
│  │ │ • POST /api/reservations (create)                 │   │ │
│  │ │ • GET /api/reservations/{id} (retrieve)           │   │ │
│  │ │ • DELETE /api/reservations/{id} (cancel)          │   │ │
│  │ │                                                   │   │ │
│  │ │ Responsibilities:                                 │   │ │
│  │ │ • Receive HTTP requests                           │   │ │
│  │ │ • Validate input format                           │   │ │
│  │ │ • Convert to/from DTOs                            │   │ │
│  │ │ • Return HTTP responses                           │   │ │
│  │ └────────────────────────────────────────────────────┘   │ │
│  └──────────────────┬───────────────────────────────────────┘ │
│                     │                                          │
│  ┌──────────────────▼───────────────────────────────────────┐ │
│  │ APPLICATION LAYER                                        │ │
│  │ ┌────────────────────────────────────────────────────┐   │ │
│  │ │ ReservationServiceImpl                             │   │ │
│  │ │ • createReservation(command)                       │   │ │
│  │ │ • getReservation(id)                              │   │ │
│  │ │ • cancelReservation(id)                           │   │ │
│  │ │                                                   │   │ │
│  │ │ Responsibilities:                                 │   │ │
│  │ │ • Orchestrate business logic                      │   │ │
│  │ │ • Call domain layer                              │   │ │
│  │ │ • Publish domain events                           │   │ │
│  │ │ • Coordinate with repositories                    │   │ │
│  │ └────────────────────────────────────────────────────┘   │ │
│  └──────────────────┬───────────────────────────────────────┘ │
│                     │                                          │
│  ┌──────────────────▼───────────────────────────────────────┐ │
│  │ DOMAIN LAYER (Business Logic)                           │ │
│  │ ┌────────────────────────────────────────────────────┐   │ │
│  │ │ Reservation (Aggregate)                           │   │ │
│  │ │ • reservationId: ReservationId                    │   │ │
│  │ │ • dinnerId: DinnerId                              │   │ │
│  │ │ • guestId: GuestId                                │   │ │
│  │ │ • reservationDate: LocalDateTime                  │   │ │
│  │ │                                                   │   │ │
│  │ │ Domain Events:                                    │   │ │
│  │ │ • ReservationCreated                              │   │ │
│  │ │ • ReservationCanceled                             │   │ │
│  │ │                                                   │   │ │
│  │ │ Value Objects:                                    │   │ │
│  │ │ • ReservationId                                   │   │ │
│  │ │ • DinnerId                                        │   │ │
│  │ │ • GuestId                                         │   │ │
│  │ │                                                   │   │ │
│  │ │ Responsibilities:                                 │   │ │
│  │ │ • Encapsulate business rules                      │   │ │
│  │ │ • Validate domain constraints                     │   │ │
│  │ │ • Publish domain events                           │   │ │
│  │ │ • No knowledge of persistence                     │   │ │
│  │ └────────────────────────────────────────────────────┘   │ │
│  └──────────────────┬───────────────────────────────────────┘ │
│                     │                                          │
│  ┌──────────────────▼───────────────────────────────────────┐ │
│  │ INFRASTRUCTURE LAYER                                     │ │
│  │ ┌────────────────────────────────────────────────────┐   │ │
│  │ │ Configuration:                                     │   │ │
│  │ │ • KafkaProducerConfig                             │   │ │
│  │ │ • KafkaConfig (topics)                            │   │ │
│  │ │                                                   │   │ │
│  │ │ Persistence:                                      │   │ │
│  │ │ • ReservationRepository (interface)               │   │ │
│  │ │ • ReservationRepositoryImpl (JPA)                 │   │ │
│  │ │ • ReservationEntity (JPA mapped)                 │   │ │
│  │ │                                                   │   │ │
│  │ │ Event Publishing:                                 │   │ │
│  │ │ • ReservationCreatedListner                       │   │ │
│  │ │ • ReservationCanceledListner                      │   │ │
│  │ │                                                   │   │ │
│  │ │ Mapping:                                          │   │ │
│  │ │ • ReservationMapper (Entity ↔ DTO)              │   │ │
│  │ │ • ReservationPresentationMapper                   │   │ │
│  │ │                                                   │   │ │
│  │ │ Responsibilities:                                 │   │ │
│  │ │ • Handle persistence                             │   │ │
│  │ │ • Provide adapters to external systems           │   │ │
│  │ │ • Configure frameworks                            │   │ │
│  │ │ • Execute queries                                 │   │ │
│  │ └────────────────────────────────────────────────────┘   │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │ DATABASE                                                 │ │
│  │ ├── reservations table                                   │ │
│  │ └── dinners table (cached)                               │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

## Domain-Driven Design Concept

```
┌──────────────────────────────────────────────────────────────┐
│            DOMAIN-DRIVEN DESIGN HIERARCHY                    │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  BUSINESS DOMAIN: Restaurant Dinner Reservation System       │
│                                                               │
│  ┌──────────────────────────────────────────────────────────┐│
│  │ BOUNDED CONTEXT 1: Reservation Management                ││
│  │ ┌────────────────────────────────────────────────────┐   ││
│  │ │ Aggregates:                                        │   ││
│  │ │ ┌─────────────────────────────────────────────┐    │   ││
│  │ │ │ Reservation (Aggregate Root)               │    │   ││
│  │ │ │ • reservationId (Identity)                 │    │   ││
│  │ │ │ • dinnerId (reference to Dinner context)   │    │   ││
│  │ │ │ • guestId (reference to User context)      │    │   ││
│  │ │ │ • reservationDate                          │    │   ││
│  │ │ │                                            │    │   ││
│  │ │ │ Value Objects (no identity):               │    │   ││
│  │ │ │ • ReservationId                            │    │   ││
│  │ │ │ • DinnerId                                 │    │   ││
│  │ │ │ • GuestId                                  │    │   ││
│  │ │ │                                            │    │   ││
│  │ │ │ Domain Events:                             │    │   ││
│  │ │ │ • ReservationCreated                       │    │   ││
│  │ │ │ • ReservationCanceled                      │    │   ││
│  │ │ │                                            │    │   ││
│  │ │ │ Repository (access to Reservations)        │    │   ││
│  │ │ │ Service (Reservation business rules)       │    │   ││
│  │ │ └─────────────────────────────────────────────┘    │   ││
│  │ └────────────────────────────────────────────────────┘   ││
│  └──────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌──────────────────────────────────────────────────────────┐│
│  │ BOUNDED CONTEXT 2: Notification Management               ││
│  │ ┌────────────────────────────────────────────────────┐   ││
│  │ │ Aggregates:                                        │   ││
│  │ │ ┌─────────────────────────────────────────────┐    │   ││
│  │ │ │ Notification                               │    │   ││
│  │ │ │ • notificationId                           │    │   ││
│  │ │ │ • guestId (who receives)                   │    │   ││
│  │ │ │ • message                                  │    │   ││
│  │ │ │ • status                                   │    │   ││
│  │ │ │ • sentAt                                   │    │   ││
│  │ │ │                                            │    │   ││
│  │ │ │ Domain Events:                             │    │   ││
│  │ │ │ • NotificationSent                         │    │   ││
│  │ │ │ • NotificationFailed                       │    │   ││
│  │ │ │                                            │    │   ││
│  │ │ │ Repository (access to Notifications)       │    │   ││
│  │ │ │ Service (Notification business rules)      │    │   ││
│  │ │ └─────────────────────────────────────────────┘    │   ││
│  │ └────────────────────────────────────────────────────┘   ││
│  └──────────────────────────────────────────────────────────┘│
│                                                               │
│  INTER-CONTEXT COMMUNICATION:                               │
│  Reservation Context publishes:                             │
│  • ReservationCreated                                       │
│  • ReservationCanceled                                      │
│                                                               │
│  Notification Context subscribes to:                        │
│  • ReservationCreated (→ send confirmation email)           │
│  • ReservationCanceled (→ send cancellation email)          │
│                                                               │
│  Each context has:                                          │
│  ✓ Its own database                                         │
│  ✓ Its own models (different ReservationId meanings)        │
│  ✓ Its own business logic                                   │
│  ✓ Clear responsibilities                                   │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

---

**Last Updated:** February 8, 2026
**Purpose:** Visual reference for microservices concepts
**For Implementation Details:** See MICROSERVICES_CONCEPTS.md

