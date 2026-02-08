# Quick Reference Guide - Kafka Event-Driven Communication

## 🚀 Quick Start (5 Minutes)

### Step 1: Start Kafka
```bash
docker-compose up -d kafka zookeeper
sleep 10  # Wait for Kafka to start
```

### Step 2: Start Services
```bash
# Terminal 1: Reservation Service
cd reservationService
mvn spring-boot:run

# Terminal 2: Notification Service
cd notificationService
mvn spring-boot:run
```

### Step 3: Create a Reservation
```bash
curl -X POST http://localhost:8083/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "dinnerId": 1,
    "guestId": 100,
    "restaurantName": "Italian Kitchen"
  }'
```

### Step 4: Check Event Flow
```bash
# View messages in Kafka
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic reservation-created --from-beginning
```

## 📊 System Architecture

```
┌────────────────────────────┐
│  Reservation Service       │
│  Port: 8083                │
├────────────────────────────┤
│ ReservationCreatedListener │  → reservation-created (3 partitions)
│ ReservationCanceledListener│  → reservation-canceled (3 partitions)
└────────────────────────────┘
           ↓ Kafka
┌────────────────────────────┐
│ Notification Service       │
│ Port: 8085                 │
├────────────────────────────┤
│ ReservationEventListener   │  ← reservation-created
│ ReservationEventListener   │  ← reservation-canceled
└────────────────────────────┘
           ↓
    Send Email/Push
```

## 📁 Key Files

### Configuration
```
reservationService/
├── src/main/resources/application.properties
│   └── Kafka producer config, topics
└── src/main/java/.../infrastructure/config/
    ├── KafkaConfig.java (topics)
    ├── KafkaProducerConfig.java (producer setup)
    └── KafkaConsumerConfig.java (consumer setup)

notificationService/
├── src/main/resources/application.yml
│   └── Kafka consumer config, topics
└── src/main/java/.../infrastructure/config/
    ├── KafkaConsumerConfig.java (consumer setup)
    └── KafkaProducerConfig.java (producer setup)
```

### Event Listeners
```
reservationService/
└── src/main/java/.../domain/event/listner/
    ├── ReservationCreatedListener.java
    └── ReservationCanceledListener.java

notificationService/
└── src/main/java/.../infrastructure/entrypoints/events/
    └── ReservationEventListener.java
```

### Events & DTOs
```
Events (Domain):
├── ReservationCreated (reservationId, dinnerId, guestId, reservationTime, restaurantName)
└── ReservationCanceled (reservationId, dinnerId, guestId)

DTOs (Application):
├── ReservationCreatedEventDTO (for Kafka message)
└── ReservationCanceledEventDTO (for Kafka message)
```

## 🔧 Kafka Topics

| Topic | Partitions | Retention | Purpose |
|-------|-----------|-----------|---------|
| reservation-created | 3 | 24h | New reservations |
| reservation-canceled | 3 | 24h | Canceled reservations |
| reservation-created.DLT | 1 | 7d | Failed messages |
| reservation-canceled.DLT | 1 | 7d | Failed messages |

## 📤 Publishing an Event

### Automatic (When Reservation is Created)
```bash
POST /api/reservations
{
  "dinnerId": 1,
  "guestId": 100,
  "restaurantName": "Restaurant Name"
}
```

Event automatically:
1. Published by ReservationCreatedListener
2. Sent to Kafka topic `reservation-created`
3. Consumed by NotificationEventListener
4. Triggers notification creation

### Event Data Sent
```json
{
  "reservationId": 1,
  "dinnerId": 1,
  "guestId": 100,
  "reservationTime": "2026-02-08T10:30:00",
  "restaurantName": "Italian Kitchen",
  "eventType": "ReservationCreated",
  "eventTimestamp": "2026-02-08T10:30:00"
}
```

## 📥 Consuming Events

### Message Flow
1. **Reservation Service** publishes domain event
2. **Event Listener** enriches with headers:
   - `correlation-id`: "reservation-{id}"
   - `event-timestamp`: current time
   - `event-type`: "ReservationCreated"
3. **Kafka Topic** receives message
4. **Notification Service** consumer processes it
5. **On Success** → notification sent, offset committed
6. **On Failure** → retry 3 times, then send to DLT

## ⚙️ Configuration Properties

### Reservation Service (application.properties)
```properties
# Kafka
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

app:
  kafka:
    topics:
      reservation-created: reservationCreated
      reservation-canceled: reservationCanceled
```

## 🔍 Monitoring Commands

### View All Topics
```bash
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### Describe a Topic
```bash
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic reservation-created
```

### Check Consumer Groups
```bash
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

### View Consumer Group Details
```bash
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group notification-group --describe
```

### Monitor Messages in Real-Time
```bash
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic reservation-created --from-beginning --property print.headers=true
```

### View Dead Letter Topic
```bash
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic reservation-created.DLT --from-beginning
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Topics not created | Wait 10s for Kafka to start, check `docker logs kafka` |
| Messages not consumed | Check consumer group: `kafka-consumer-groups --describe` |
| Connection timeout | Verify `localhost:9092` is accessible |
| Deserialization errors | Check trusted packages in application.yml |
| High DLT messages | Check notification service logs for errors |
| Consumer lag high | Increase consumer concurrency in KafkaConsumerConfig |

## 📊 Kafka UI (Web Dashboard)

Start with:
```bash
docker-compose up -d kafka-ui
```

Open: **http://localhost:8080**

Features:
- View all topics and messages
- Monitor consumer groups
- Check offsets and lag
- Explore message details
- Inspect partition distribution

## 📈 Performance Tuning

### Increase Throughput
```properties
# In application.properties (Reservation Service)
spring.kafka.producer.linger-ms=100
spring.kafka.producer.batch-size=32768
spring.kafka.producer.compression-type=snappy

# In application.yml (Notification Service)
spring.kafka.listener.concurrency=5
spring.kafka.consumer.max-poll-records=500
```

### Increase Reliability
```properties
spring.kafka.producer.acks=all
spring.kafka.producer.retries=5
spring.kafka.producer.min-insync-replicas=2
```

## 🧪 Testing

### Run Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Run All Tests
```bash
mvn test
```

### Test Event Publishing
```bash
mvn test -Dtest=ReservationCreatedEventIntegrationTest
```

## 📚 Documentation

- **IMPLEMENTATION_SUMMARY.md** - Complete implementation details
- **KAFKA_EVENT_DRIVEN_IMPLEMENTATION.md** - Architecture & design patterns
- **KAFKA_SETUP_DEPLOYMENT.md** - Full setup and deployment guide

## 🆘 Getting Help

### Check Reservation Service Logs
```bash
# Look for: "Publishing ReservationCreated event to Kafka"
mvn spring-boot:run | grep -i "reservation\|kafka"
```

### Check Notification Service Logs
```bash
# Look for: "Received ReservationCreatedEvent"
mvn spring-boot:run | grep -i "received\|processed"
```

### View Docker Logs
```bash
docker logs kafka-broker       # Kafka
docker logs zookeeper          # Zookeeper
```

## 🔗 API Endpoints

| Method | Endpoint | Service | Purpose |
|--------|----------|---------|---------|
| POST | /api/reservations | 8083 | Create reservation (publishes event) |
| GET | /api/reservations/{id} | 8083 | Get reservation details |
| DELETE | /api/reservations/{id} | 8083 | Cancel reservation (publishes event) |
| GET | /api/notifications | 8085 | List notifications |

## ✅ Checklist for New Developers

- [ ] Kafka running (docker-compose up -d)
- [ ] Both services compiled and running
- [ ] Can create reservation via API
- [ ] Can see messages in Kafka topic
- [ ] Notifications created in notification service
- [ ] Can view messages in Kafka UI (localhost:8080)
- [ ] Understand event flow (read IMPLEMENTATION_SUMMARY.md)
- [ ] Know how to check consumer lag
- [ ] Know how to view DLT for failures
- [ ] Tested failure scenarios (e.g., stop notification service, see DLT populate)

## 🎯 Common Tasks

### Stop All Services
```bash
docker-compose down
```

### Restart Kafka (Clean)
```bash
docker-compose down -v
docker-compose up -d kafka zookeeper kafka-ui
```

### Check if Services are Running
```bash
curl http://localhost:8083/health  # Reservation Service
curl http://localhost:8085/health  # Notification Service
curl http://localhost:9092         # Kafka
```

### View Application Logs
```bash
# Reservation Service
tail -f reservationService/logs/application.log

# Notification Service
tail -f notificationService/logs/application.log

# Kafka
docker logs -f kafka-broker
```

---

**Last Updated:** February 8, 2026
**Status:** Ready for Production
**Kafka Version:** 7.5.0+
**Spring Boot Version:** 3.2.5+

