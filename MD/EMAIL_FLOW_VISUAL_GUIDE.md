# 📧 Email Notification Flow - Visual Guide

## Complete Event-Driven Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    STEP-BY-STEP: FROM REQUEST TO EMAIL                      │
└─────────────────────────────────────────────────────────────────────────────┘

STEP 1: Client Makes Request
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        
    📱 Client (Postman/Browser/cURL)
       │
       │ POST /api/reservations
       │ {
       │   "dinnerId": 100,
       │   "guestId": 50,
       │   "restaurantName": "Italian Kitchen"
       │ }
       ▼


STEP 2: Reservation Service Processes Request
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    🏢 Reservation Service (Port 8083)
       │
       ├─ ReservationController receives request
       ├─ ReservationService creates reservation
       ├─ Save to Database ✅
       │
       ├─ Publish Domain Event:
       │  ┌───────────────────────────────────────┐
       │  │ ReservationCreated                    │
       │  │ ├─ reservationId: UUID                │
       │  │ ├─ dinnerId: 100                      │
       │  │ ├─ guestId: 50                        │
       │  │ ├─ reservationTime: 2026-02-10T19:00  │
       │  │ └─ restaurantName: "Italian Kitchen"  │
       │  └───────────────────────────────────────┘
       ▼


STEP 3: Event Listener Enriches and Publishes to Kafka
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    📢 ReservationCreatedListner
       │
       ├─ Listen to Domain Event
       ├─ Enrich with Headers:
       │  ├─ correlation-id: "reservation-<UUID>"
       │  ├─ event-timestamp: current time
       │  ├─ event-type: "ReservationCreated"
       │  └─ source-service: "reservationService"
       │
       ├─ Convert to DTO
       │
       ├─ Publish to Kafka Topic
       ▼

    
STEP 4: Message Queued in Kafka
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    📮 Apache Kafka (Port 9092)
       │
       │ Topic: "reservation-created"
       │ ┌──────────────────────────────────────┐
       │ │ Partition 0 │ Partition 1 │ Partition 2 │
       │ │   [MSG]     │             │             │
       │ └──────────────────────────────────────┘
       │
       │ Message stored with:
       │ ├─ Key: reservationId
       │ ├─ Value: ReservationCreatedEventDTO
       │ ├─ Headers: correlation-id, timestamp, etc.
       │ └─ Offset: 0, 1, 2... (incremental)
       │
       │ ⏳ Message persisted and ready for consumption
       ▼


STEP 5: Notification Service Consumes Event
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    🔔 Notification Service (Port 8085)
       │
       ├─ KafkaConsumer subscribed to "reservation-created"
       │
       ├─ ReservationEventListener receives message
       │  ┌───────────────────────────────────────┐
       │  │ @KafkaListener                        │
       │  │ consumeReservationCreated()           │
       │  └───────────────────────────────────────┘
       │
       ├─ Extract event data:
       │  ├─ reservationId
       │  ├─ guestId
       │  ├─ restaurantName
       │  └─ reservationTime
       │
       ├─ Extract headers (correlation-id for tracing)
       ▼


STEP 6: Process Event & Prepare Email
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    🎯 NotificationInputPort
       │
       ├─ handleReservationCreatedEvent()
       │
       ├─ Create Notification record in database
       │
       ├─ Prepare Email:
       │  ┌───────────────────────────────────────┐
       │  │ To: guest-50@example.com              │
       │  │ From: noreply@buber-dinner.com        │
       │  │ Subject: Reservation Confirmation     │
       │  │                                       │
       │  │ Body:                                 │
       │  │ Dear Guest,                           │
       │  │                                       │
       │  │ Your reservation at Italian Kitchen   │
       │  │ has been confirmed!                   │
       │  │                                       │
       │  │ Date: 2026-02-10 at 19:00            │
       │  │ Reservation ID: <UUID>                │
       │  │                                       │
       │  │ Thank you for choosing us!            │
       │  └───────────────────────────────────────┘
       ▼


STEP 7: Send Email via SMTP
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    📧 Spring Mail Sender
       │
       ├─ Configuration:
       │  ├─ Host: localhost
       │  ├─ Port: 1025 (MailDev SMTP)
       │  └─ Protocol: SMTP
       │
       ├─ Send Email to MailDev
       │
       ├─ Log: "Email sent successfully!"
       │
       ├─ Commit Kafka offset ✅
       ▼


STEP 8: Email Captured by MailDev
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    📬 MailDev (Port 1080 - Web UI)
           (Port 1025 - SMTP Server)
       │
       ├─ Receives email via SMTP
       ├─ Stores in memory inbox
       ├─ Email instantly available in Web UI
       │
       │ 🌐 Open in browser:
       │    http://localhost:1080
       │
       └─ ✅ EMAIL VISIBLE! 🎉


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                              COMPLETE FLOW! 
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## Detailed Timeline

```
T=0ms      Client sends POST request
           ↓
T=10ms     Reservation saved to database
           ↓
T=15ms     Domain event published
           ↓
T=20ms     Event listener publishes to Kafka
           ↓
T=25ms     Message persisted in Kafka topic
           ↓
T=50ms     Notification Service polls and receives message
           ↓
T=55ms     Event deserialized and processed
           ↓
T=60ms     Notification record created in database
           ↓
T=70ms     Email composed and sent to MailDev
           ↓
T=75ms     Kafka offset committed
           ↓
T=80ms     Email visible in MailDev UI 📧✅

Total Time: ~80ms (0.08 seconds)
```

---

## Error Handling Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     WHAT IF SOMETHING FAILS?                                │
└─────────────────────────────────────────────────────────────────────────────┘

SCENARIO 1: Notification Service is Down
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    Reservation Service
       ↓
    Publishes to Kafka ✅
       ↓
    Message queued in Kafka (persisted)
       ↓
    Notification Service is DOWN ❌
       ↓
    ⏳ Message waits in queue...
       ↓
    Notification Service comes back UP ✅
       ↓
    Consumes pending messages
       ↓
    Email sent successfully! 📧✅

RESULT: ✅ No message loss! Async communication protects from failures.


SCENARIO 2: Email Sending Fails
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    Notification Service receives event
       ↓
    Tries to send email ❌ (Error!)
       ↓
    DefaultErrorHandler catches exception
       ↓
    Retry #1 (wait 1 second) ⏳
       ↓
    Still fails ❌
       ↓
    Retry #2 (wait 1 second) ⏳
       ↓
    Still fails ❌
       ↓
    Retry #3 (wait 1 second) ⏳
       ↓
    All retries exhausted ❌
       ↓
    Message sent to Dead Letter Topic (DLT)
       ↓
    Topic: "reservation-created.DLT"
       ↓
    Operator analyzes failure
       ↓
    Fix issue and replay from DLT
       ↓
    Email sent successfully! 📧✅

RESULT: ✅ No message loss! DLT ensures every message is eventually processed.


SCENARIO 3: Kafka is Down
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    Reservation Service tries to publish
       ↓
    Kafka is unreachable ❌
       ↓
    Producer retries (configured 3 times)
       ↓
    Request fails after retries
       ↓
    ⚠️ User receives error (HTTP 500)
       ↓
    User can retry the request
       ↓
    Kafka comes back UP ✅
       ↓
    Next request succeeds
       ↓
    Event published and processed normally ✅

RESULT: ⚠️ Request fails, but system recovers when Kafka is back.
```

---

## Component Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      MICROSERVICES ARCHITECTURE                              │
└─────────────────────────────────────────────────────────────────────────────┘

                              ┌─────────────┐
                              │   Client    │
                              │ (Postman)   │
                              └──────┬──────┘
                                     │
                                     │ HTTP POST
                                     ▼
                        ┌────────────────────────┐
                        │  API Gateway           │
                        │  (Port 8080)           │
                        │  [Optional]            │
                        └───────────┬────────────┘
                                    │
                                    │ Route to Service
                                    ▼
                        ┌────────────────────────┐
                        │ Reservation Service    │
                        │ (Port 8083)            │
                        │ ┌──────────────────┐   │
                        │ │ Controller       │   │
                        │ │ Service          │   │
                        │ │ Repository       │   │
                        │ │ Event Publisher  │   │
                        │ └──────────────────┘   │
                        └───────────┬────────────┘
                                    │
                                    │ Publish Event
                                    ▼
                        ┌────────────────────────┐
                        │  Apache Kafka          │
                        │  (Port 9092)           │
                        │                        │
                        │  Topics:               │
                        │  • reservation-created │
                        │  • reservation-canceled│
                        │  • *.DLT (Dead Letter) │
                        └───────────┬────────────┘
                                    │
                                    │ Subscribe & Consume
                                    ▼
                        ┌────────────────────────┐
                        │ Notification Service   │
                        │ (Port 8085)            │
                        │ ┌──────────────────┐   │
                        │ │ Event Listener   │   │
                        │ │ Input Port       │   │
                        │ │ Mail Sender      │   │
                        │ └──────────────────┘   │
                        └───────────┬────────────┘
                                    │
                                    │ Send Email (SMTP)
                                    ▼
                        ┌────────────────────────┐
                        │  MailDev               │
                        │  SMTP: Port 1025       │
                        │  Web UI: Port 1080     │
                        │                        │
                        │  📧 Email Inbox        │
                        └────────────────────────┘
                                    │
                                    │ View in Browser
                                    ▼
                              ┌─────────────┐
                              │  Developer  │
                              │   (You!)    │
                              └─────────────┘


Supporting Services:
──────────────────────────────────────────────────────────────────────────────
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Eureka       │  │ PostgreSQL   │  │ MongoDB      │  │ Zipkin       │
│ (8761)       │  │ (5432)       │  │ (27017)      │  │ (9411)       │
│              │  │              │  │              │  │              │
│ Service      │  │ Database     │  │ NoSQL DB     │  │ Tracing      │
│ Registry     │  │              │  │              │  │              │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
```

---

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         DATA TRANSFORMATION                                  │
└─────────────────────────────────────────────────────────────────────────────┘

HTTP Request Body (JSON)
┌───────────────────────────────┐
│ {                             │
│   "dinnerId": 100,            │
│   "guestId": 50,              │
│   "reservationDate": "...",   │
│   "restaurantName": "..."     │
│ }                             │
└───────────┬───────────────────┘
            │
            │ Deserialized to
            ▼
CreateReservationCommand (DTO)
┌───────────────────────────────┐
│ dinnerId: Long                │
│ guestId: Long                 │
│ reservationDate: LocalDateTime│
│ restaurantName: String        │
└───────────┬───────────────────┘
            │
            │ Mapped to
            ▼
Reservation (Domain Model)
┌───────────────────────────────┐
│ reservationId: UUID           │
│ dinnerId: Long                │
│ guestId: Long                 │
│ reservationDate: LocalDateTime│
└───────────┬───────────────────┘
            │
            │ Event Published
            ▼
ReservationCreated (Domain Event)
┌───────────────────────────────┐
│ reservationId: UUID           │
│ dinnerId: Long                │
│ guestId: Long                 │
│ reservationTime: LocalDateTime│
│ restaurantName: String        │
└───────────┬───────────────────┘
            │
            │ Converted to
            ▼
ReservationCreatedEventDTO (Transfer Object)
┌───────────────────────────────┐
│ reservationId: UUID           │
│ guestId: Long                 │
│ dinnerId: Long                │
│ reservationTime: LocalDateTime│
│ restaurantName: String        │
└───────────┬───────────────────┘
            │
            │ Serialized to JSON & sent via Kafka
            ▼
Kafka Message (Binary)
┌───────────────────────────────┐
│ Key: UUID                     │
│ Value: JSON bytes             │
│ Headers: correlation-id, etc. │
└───────────┬───────────────────┘
            │
            │ Deserialized by Consumer
            ▼
ReservationCreatedEventDTO (Notification Service)
┌───────────────────────────────┐
│ reservationId: UUID           │
│ guestId: Long                 │
│ dinnerId: Long                │
│ reservationTime: LocalDateTime│
│ restaurantName: String        │
└───────────┬───────────────────┘
            │
            │ Processed to create
            ▼
Email Message (MIME)
┌───────────────────────────────┐
│ To: guest-50@example.com      │
│ From: noreply@...             │
│ Subject: Confirmation         │
│ Body: HTML/Text               │
└───────────┬───────────────────┘
            │
            │ Sent via SMTP
            ▼
MailDev Inbox 📧✅
```

---

## Testing Checklist

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    VERIFICATION CHECKLIST                                    │
└─────────────────────────────────────────────────────────────────────────────┘

Infrastructure:
  ☐ Docker Desktop is running
  ☐ docker-compose up -d executed successfully
  ☐ All containers show "Up" status (docker-compose ps)
  ☐ Kafka is ready (wait 30 seconds after start)

Services:
  ☐ Discovery Service started (http://localhost:8761 accessible)
  ☐ Reservation Service started (logs show "Started successfully")
  ☐ Notification Service started (logs show "Subscribed to topic")
  ☐ Eureka shows all services registered

Kafka:
  ☐ Topics created (docker exec ... kafka-topics --list)
  ☐ reservation-created topic exists
  ☐ reservation-created.DLT topic exists

MailDev:
  ☐ MailDev UI accessible at http://localhost:1080
  ☐ Inbox is empty (ready for new emails)

Test:
  ☐ POST request to create reservation succeeds (201 Created)
  ☐ Reservation Service logs show "Event published"
  ☐ Notification Service logs show "Event consumed"
  ☐ Notification Service logs show "Email sent successfully"
  ☐ Email appears in MailDev UI 📧✅

Success Criteria:
  ✅ Email visible in MailDev within 5 seconds
  ✅ Email contains correct reservation details
  ✅ Email has proper formatting (HTML)
  ✅ Kafka offset committed (no reprocessing)
```

---

**Created:** February 8, 2026
**Purpose:** Visual guide for event-driven email flow
**Status:** ✅ Production Ready
**Use:** Understanding and troubleshooting the complete flow

