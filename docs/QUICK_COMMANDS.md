# 🎯 Quick Commands Reference - Event-Driven Architecture

## 🚀 Quick Start (3 Steps)

### **Step 1: Start Infrastructure (30 seconds)**
```powershell
# Option A: Using the script (Recommended)
.\start-infrastructure.ps1

# Option B: Manual
docker-compose up -d
```

### **Step 2: Start Services (3 separate terminals)**

**Terminal 1 - Discovery Service:**
```powershell
cd discoveryService
mvn spring-boot:run
```

**Terminal 2 - Reservation Service:**
```powershell
cd reservationService
mvn spring-boot:run
```

**Terminal 3 - Notification Service:**
```powershell
cd notificationService
mvn spring-boot:run
```

### **Step 3: Test & See Emails**
```powershell
# Option A: Using the test script (Recommended)
.\test-create-reservation.ps1

# Option B: Manual command
Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/reservations" `
  -ContentType "application/json" `
  -Body '{"dinnerId": 100, "guestId": 50, "reservationDate": "2026-02-10T19:00:00", "restaurantName": "Italian Kitchen"}'
```

**Then open:** http://localhost:1080 to see the email! 📧

---

## 📋 All Commands

### **Docker Commands**

```powershell
# Start all infrastructure
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f kafka
docker-compose logs -f mail-dev

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v

# Restart a specific service
docker-compose restart kafka
```

### **Kafka Commands**

```powershell
# List all topics
docker exec -it ms_kafka kafka-topics --list --bootstrap-server localhost:9092

# View messages in reservation-created topic
docker exec -it ms_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic reservation-created --from-beginning

# View messages in DLT (Dead Letter Topic)
docker exec -it ms_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic reservation-created.DLT --from-beginning

# Check consumer groups
docker exec -it ms_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Check consumer lag
docker exec -it ms_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group notification-service-group --describe
```

### **Service Commands**

```powershell
# Start Discovery Service
cd discoveryService
mvn spring-boot:run

# Start Reservation Service
cd reservationService
mvn spring-boot:run

# Start Notification Service
cd notificationService
mvn spring-boot:run

# Start API Gateway
cd api-gateway
mvn spring-boot:run

# Start Dinner Service
cd dinnerService
mvn spring-boot:run

# Start User Service
cd userService
mvn spring-boot:run
```

### **Build Commands**

```powershell
# Build all services (from root)
mvn clean install -DskipTests

# Build specific service
cd reservationService
mvn clean package -DskipTests

# Run tests
mvn test

# Run specific test
mvn test -Dtest=ReservationCreatedEventIntegrationTest
```

### **Test Commands**

```powershell
# Create a reservation (triggers email)
Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/reservations" `
  -ContentType "application/json" `
  -Body '{"dinnerId": 100, "guestId": 50, "reservationDate": "2026-02-10T19:00:00", "restaurantName": "Italian Kitchen"}'

# Get reservation by ID
Invoke-RestMethod -Method Get -Uri "http://localhost:8083/api/reservations/1"

# Cancel reservation (triggers cancellation email)
Invoke-RestMethod -Method Delete -Uri "http://localhost:8083/api/reservations/1"

# Health check
Invoke-RestMethod -Method Get -Uri "http://localhost:8083/actuator/health"
```

### **Multiple Reservations Test**

```powershell
# Create 5 reservations quickly
1..5 | ForEach-Object {
    Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/reservations" `
      -ContentType "application/json" `
      -Body "{\"dinnerId\": $_, \"guestId\": $(Get-Random -Minimum 1 -Maximum 100), \"reservationDate\": \"2026-02-10T19:00:00\", \"restaurantName\": \"Restaurant $_\"}"
    Start-Sleep -Milliseconds 500
}
```

---

## 🌐 URL Reference

| Service | URL | Purpose |
|---------|-----|---------|
| **MailDev** 📧 | http://localhost:1080 | **View Emails** |
| Eureka Dashboard | http://localhost:8761 | Service Registry |
| Reservation API | http://localhost:8083 | Create/Cancel Reservations |
| Notification API | http://localhost:8085 | Notification Service |
| API Gateway | http://localhost:8080 | Gateway |
| Dinner API | http://localhost:8084 | Dinner Service |
| User API | http://localhost:8087 | User Service |
| PgAdmin | http://localhost:5050 | PostgreSQL Admin |
| Mongo Express | http://localhost:8081 | MongoDB Admin |
| Zipkin | http://localhost:9411 | Distributed Tracing |

---

## 🔍 Verification Commands

### **Check if Everything is Running**

```powershell
# Check Docker containers
docker-compose ps

# Check Eureka (should show registered services)
Start-Process "http://localhost:8761"

# Check Kafka topics
docker exec -it ms_kafka kafka-topics --list --bootstrap-server localhost:9092

# Check service health
Invoke-RestMethod -Uri "http://localhost:8083/actuator/health"
Invoke-RestMethod -Uri "http://localhost:8085/actuator/health"
```

### **Verify Event Flow**

```powershell
# 1. Create reservation
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/reservations" `
  -ContentType "application/json" `
  -Body '{"dinnerId": 100, "guestId": 50, "reservationDate": "2026-02-10T19:00:00", "restaurantName": "Test Restaurant"}'

Write-Host "Reservation created with ID: $($response.id)"

# 2. Check Kafka messages
docker exec -it ms_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic reservation-created --max-messages 1

# 3. Open MailDev
Start-Process "http://localhost:1080"
```

---

## 🛠️ Troubleshooting Commands

### **Kafka Not Working**

```powershell
# Restart Kafka
docker-compose restart zookeeper
Start-Sleep -Seconds 5
docker-compose restart kafka
Start-Sleep -Seconds 30

# Check Kafka logs
docker logs ms_kafka --tail 50
```

### **Service Won't Start**

```powershell
# Check if port is already in use
netstat -ano | findstr :8083

# Kill process on port (replace PID)
taskkill /PID <PID> /F

# Check service logs
# (Look in the terminal where you started the service)
```

### **Emails Not Appearing**

```powershell
# Check MailDev is running
docker ps | findstr mail-dev

# Restart MailDev
docker-compose restart mail-dev

# Check Notification Service logs
# (Look for "Email sent successfully" message)

# Verify email configuration
# Check: notificationService/src/main/resources/application.properties
# Should have:
# spring.mail.host=localhost
# spring.mail.port=1025
```

### **Clean Start (Reset Everything)**

```powershell
# Stop all services (Ctrl+C in each terminal)

# Stop and remove Docker containers and volumes
docker-compose down -v

# Remove all Docker containers
docker container prune -f

# Remove all Docker volumes
docker volume prune -f

# Rebuild services
mvn clean install -DskipTests

# Start fresh
.\start-infrastructure.ps1
```

---

## 📊 Monitoring Commands

### **Real-time Logs**

```powershell
# Follow all Docker logs
docker-compose logs -f

# Follow specific service logs
docker-compose logs -f kafka
docker-compose logs -f mail-dev

# Follow Kafka consumer
docker exec -it ms_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic reservation-created --from-beginning
```

### **Check Consumer Lag**

```powershell
# List consumer groups
docker exec -it ms_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Check lag for notification service
docker exec -it ms_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group notification-service-group --describe
```

---

## 🎯 Common Workflows

### **Workflow 1: Clean Start**
```powershell
docker-compose down -v
docker-compose up -d
Start-Sleep -Seconds 30
# Start services in 3 terminals
# Test with .\test-create-reservation.ps1
```

### **Workflow 2: Development Cycle**
```powershell
# Make code changes
# Restart specific service (Ctrl+C then mvn spring-boot:run)
# Test changes
# Repeat
```

### **Workflow 3: Demo Flow**
```powershell
# 1. Open MailDev
Start-Process "http://localhost:1080"

# 2. Open Eureka
Start-Process "http://localhost:8761"

# 3. Create reservation
.\test-create-reservation.ps1

# 4. Show email in MailDev
# 5. Show trace in Zipkin (optional)
Start-Process "http://localhost:9411"
```

---

## 💡 Pro Tips

```powershell
# Create alias for common commands
Set-Alias -Name dcu -Value docker-compose up -d
Set-Alias -Name dcd -Value docker-compose down
Set-Alias -Name dps -Value docker-compose ps

# View Kafka in real-time while testing
Start-Process powershell -ArgumentList "-NoExit", "-Command", "docker exec -it ms_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic reservation-created --from-beginning"

# Open all URLs at once
@(
    "http://localhost:1080",  # MailDev
    "http://localhost:8761"   # Eureka
) | ForEach-Object { Start-Process $_ }
```

---

## 📝 Example: Complete Test Session

```powershell
# 1. Start infrastructure
.\start-infrastructure.ps1

# 2. Start services (in 3 separate terminals)
# Terminal 1:
cd discoveryService; mvn spring-boot:run

# Terminal 2:
cd reservationService; mvn spring-boot:run

# Terminal 3:
cd notificationService; mvn spring-boot:run

# 3. Wait for all services to start (check Eureka)
Start-Process "http://localhost:8761"

# 4. Create multiple reservations
1..3 | ForEach-Object {
    Write-Host "Creating reservation $_..."
    .\test-create-reservation.ps1
    Start-Sleep -Seconds 2
}

# 5. Check MailDev for 3 emails
Start-Process "http://localhost:1080"

# 6. View Kafka messages
docker exec -it ms_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic reservation-created --from-beginning

# 7. Done! Clean up when finished
docker-compose down
```

---

**Created:** February 8, 2026
**Status:** ✅ Complete Command Reference
**Use Case:** Running and testing event-driven microservices architecture

