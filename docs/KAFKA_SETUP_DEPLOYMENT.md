# Kafka Event-Driven Communication - Setup & Deployment Guide

## Prerequisites

- Apache Kafka 3.0+
- Docker & Docker Compose (for containerized setup)
- Java 17+
- Maven 3.8+
- Spring Boot 3.2.5+

## Quick Start

### 1. Start Kafka Broker

#### Option A: Using Docker Compose (Recommended)

Update your existing `docker-compose.yml`:

```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka-broker
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
      KAFKA_LOG_RETENTION_HOURS: 24
      KAFKA_LOG_RETENTION_BYTES: -1
      KAFKA_NUM_PARTITIONS: 3

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    depends_on:
      - kafka
    ports:
      - "8080:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
      KAFKA_CLUSTERS_0_ZOOKEEPER: zookeeper:2181

volumes:
  zookeeper-data:
  kafka-data:
```

Run:
```bash
docker-compose up -d
```

#### Option B: Local Kafka Installation

1. Download Kafka from https://kafka.apache.org/downloads
2. Extract and navigate to the directory
3. Start Zookeeper:
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```
4. Start Kafka (in another terminal):
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```

### 2. Verify Kafka is Running

```bash
# Check broker status
docker logs kafka-broker

# List topics (should show created topics)
kafka-topics --bootstrap-server localhost:9092 --list

# Or use Kafka UI: http://localhost:8080
```

### 3. Build Services

#### Reservation Service
```bash
cd reservationService
mvn clean package -DskipTests

# Run
mvn spring-boot:run
# Service runs on: http://localhost:8083
```

#### Notification Service
```bash
cd notificationService
mvn clean package -DskipTests

# Run
mvn spring-boot:run
# Service runs on: http://localhost:8085
```

### 4. Verify Event Flow

#### Check Topics Created
```bash
docker exec -it kafka-broker kafka-topics --bootstrap-server localhost:9092 --list
```

Expected output:
```
__consumer_offsets
reservation-created
reservation-canceled
reservation-created.DLT
reservation-canceled.DLT
```

#### View Consumer Groups
```bash
docker exec -it kafka-broker kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

Expected output:
```
notification-group
reservation-service-group
```

#### Check Consumer Group Details
```bash
docker exec -it kafka-broker kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group notification-group --describe
```

## Testing the Event Flow

### 1. Create a Reservation

```bash
curl -X POST http://localhost:8083/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "dinnerId": 1,
    "guestId": 100,
    "restaurantName": "Italian Kitchen"
  }'
```

### 2. Monitor Kafka Messages

#### Using Kafka Console Consumer
```bash
docker exec -it kafka-broker kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic reservation-created --from-beginning --property print.headers=true
```

#### Using Kafka UI
Open http://localhost:8080 and navigate to:
- Topics → reservation-created
- Messages tab to view published events

### 3. Verify Notification Service Logs

```bash
# View reservation service logs
mvn spring-boot:run | grep -i "reservation"

# View notification service logs
mvn spring-boot:run | grep -i "received\|processed\|notification"
```

## Monitoring & Troubleshooting

### 1. Monitor Consumer Lag

```bash
docker exec -it kafka-broker kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group notification-group --describe
```

Expected output:
```
GROUP           TOPIC               PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG  CONSUMER-ID
notification-group reservation-created      0            10              10  0    consumer-1
notification-group reservation-created      1             5               5  0    consumer-2
```

LAG of 0 means all messages are processed.

### 2. View Dead Letter Topic Messages

If notifications fail, messages go to DLT:

```bash
docker exec -it kafka-broker kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic reservation-created.DLT --from-beginning --property print.headers=true
```

### 3. Check Partition Distribution

```bash
docker exec -it kafka-broker kafka-topics --bootstrap-server localhost:9092 \
  --describe --topic reservation-created
```

Expected output:
```
Topic: reservation-created
PartitionCount:3
ReplicationFactor:1
Topic: reservation-created	Partition: 0	Leader: 1	Replicas: 1	Isr: 1
Topic: reservation-created	Partition: 1	Leader: 1	Replicas: 1	Isr: 1
Topic: reservation-created	Partition: 2	Leader: 1	Replicas: 1	Isr: 1
```

### 4. Common Issues & Solutions

#### Issue: "Failed to create Kafka producer"
**Solution:** Ensure Kafka is running and accessible on localhost:9092

#### Issue: "No messages consumed"
**Solution:**
1. Check if topics were created: `kafka-topics --list`
2. Check consumer group: `kafka-consumer-groups --describe`
3. Check application logs for deserialization errors

#### Issue: "High DLT message count"
**Solution:**
1. Check notification service logs for errors
2. Verify email/push service connectivity
3. Check database connection
4. Review error message format

## Configuration Files Summary

### Reservation Service (application.properties)
```properties
spring.application.name=reservationService
server.port=8083
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.acks=all
spring.kafka.consumer.group-id=reservation-service-group
```

### Notification Service (application.yml)
```yaml
spring:
  application:
    name: notification-service
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-group
      
app:
  kafka:
    topics:
      reservation-created: reservationCreated
      reservation-canceled: reservationCanceled
```

## Advanced Configuration

### Enable Kafka Authentication (Production)

For SASL authentication, update docker-compose.yml:

```yaml
environment:
  KAFKA_SECURITY_INTER_BROKER_PROTOCOL: SASL_PLAINTEXT
  KAFKA_SASL_MECHANISM_INTER_BROKER_PROTOCOL: PLAIN
  KAFKA_SASL_ENABLED_MECHANISMS: PLAIN
```

### Configure Topic Retention

Modify in application code or via CLI:

```bash
docker exec -it kafka-broker kafka-configs --bootstrap-server localhost:9092 \
  --entity-type topics --entity-name reservation-created \
  --alter --add-config retention.ms=86400000
```

### Scale Consumers

Increase concurrency in NotificationService:

```yaml
spring:
  kafka:
    listener:
      concurrency: 5  # Process 5 messages in parallel
```

## Running Tests

### Unit Tests
```bash
cd reservationService
mvn test -Dtest=ReservationCreatedListnerTest

cd notificationService
mvn test -Dtest=ReservationEventListenerIntegrationTest
```

### Integration Tests with Embedded Kafka
```bash
mvn test -Dtest=*IntegrationTest
```

## Performance Tuning

### Producer Optimization (Reservation Service)

```properties
# Batch messages for better throughput
spring.kafka.producer.linger-ms=100
spring.kafka.producer.batch-size=32768

# Compression for reduced bandwidth
spring.kafka.producer.compression-type=snappy
```

### Consumer Optimization (Notification Service)

```yaml
spring:
  kafka:
    consumer:
      max-poll-records: 500  # Fetch more messages per poll
      fetch-min-bytes: 1024  # Wait for minimum data
    listener:
      concurrency: 5  # Process 5 partitions in parallel
```

## Monitoring with Prometheus & Grafana

### Add Micrometer Kafka Metrics

Add to pom.xml:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Metrics to Monitor

1. `kafka.producer.record.send.total` - Total records sent
2. `kafka.consumer.records.consumed.total` - Total records consumed
3. `kafka.consumer.lag` - Consumer lag per partition
4. `kafka_request_latency_ms` - Request latency

## Cleanup & Reset

### Delete All Topics
```bash
docker exec -it kafka-broker kafka-topics --bootstrap-server localhost:9092 \
  --delete --topic 'reservation-.*'
```

### Reset Consumer Group Offset
```bash
docker exec -it kafka-broker kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group notification-group --reset-offsets --to-earliest --execute
```

### Clear Kafka Data (Restart)
```bash
docker-compose down -v  # Remove volumes
docker-compose up -d    # Restart fresh
```

## Production Deployment Checklist

- [ ] Kafka cluster with 3+ brokers
- [ ] Replication factor set to 3 (or min.insync.replicas=2)
- [ ] Enable authentication (SASL/SSL)
- [ ] Configure topic retention policies
- [ ] Set up monitoring and alerting
- [ ] Implement DLT monitoring
- [ ] Enable message encryption
- [ ] Configure ACLs for topic access
- [ ] Set up automated backups
- [ ] Document runbooks for common issues
- [ ] Load test before go-live
- [ ] Implement distributed tracing
- [ ] Set up log aggregation (ELK, etc.)

## References

- Kafka Documentation: https://kafka.apache.org/documentation
- Spring Kafka: https://spring.io/projects/spring-kafka
- Confluent Platform: https://www.confluent.io
- Kafka Best Practices: https://kafka.apache.org/documentation/#bestpractices

