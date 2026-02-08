# 🚀 Running Event-Driven Architecture - Complete Guide

## Overview
This guide will help you start the entire microservices architecture with Kafka event-driven communication and **see actual emails** being sent when reservations are created.

---

## 📋 Prerequisites

Ensure you have:
- ✅ Docker Desktop installed and running
- ✅ Java 17 installed
- ✅ Maven installed (or use `mvnw`)
- ✅ All services compiled successfully

---

## 🎯 Step-by-Step Execution Guide

### **Step 1: Start Infrastructure with Docker Compose**

Open PowerShell in your project root directory and start all infrastructure services:

```powershell
# Navigate to project root
cd C:\Users\chahid\IdeaProjects\micro-service-projet

# Start all Docker services (Kafka, Zookeeper, PostgreSQL, MongoDB, MailDev, etc.)
docker-compose up -d
```

**What this starts:**
- ✅ **Kafka** (localhost:9092) - Message broker
- ✅ **Zookeeper** (localhost:2181) - Kafka coordinator
- ✅ **PostgreSQL** (localhost:5432) - Database for services
- ✅ **MongoDB** (localhost:27017) - NoSQL database
- ✅ **MailDev** (localhost:1080) - **Email capture UI** ⬅️ **This is where you'll see emails!**
- ✅ **PgAdmin** (localhost:5050) - PostgreSQL admin
- ✅ **Mongo Express** (localhost:8081) - MongoDB admin
- ✅ **Zipkin** (localhost:9411) - Distributed tracing

**Verify Docker services are running:**
```powershell
docker-compose ps
```

You should see all services with status "Up".

---

### **Step 2: Verify Kafka is Ready**

Wait 30-60 seconds for Kafka to fully start, then verify:

```powershell
# Check Kafka container logs
docker logs ms_kafka

# You should see "Kafka Server started" in the logs
```

---

### **Step 3: Start Discovery Service (Eureka)**

Open a **new PowerShell window** and start the Eureka service:

```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet\discoveryService

# Option 1: Using Maven Wrapper
mvn spring-boot:run

# Option 2: Using installed Maven
mvn spring-boot:run
```

**Wait for:** "Eureka Server started" message

**Verify:** Open browser to http://localhost:8761
- You should see Eureka Dashboard

---

### **Step 4: Start Reservation Service**

Open a **new PowerShell window** and start the Reservation service:

```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet\reservationService

mvn spring-boot:run
```

**Wait for:** 
- "Started ReservationServiceApplication"
- "Registered instance RESERVATIONSERVICE"
- "KafkaProducerConfig initialized"

**Service will be available at:** http://localhost:8083

---

### **Step 5: Start Notification Service**

Open a **new PowerShell window** and start the Notification service:

```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet\notificationService

mvn spring-boot:run
```

**Wait for:**
- "Started NotificationServiceApplication"
- "Registered instance NOTIFICATIONSERVICE"
- "KafkaConsumerConfig initialized"
- "Subscribed to topic: reservation-created"

**Service will be available at:** http://localhost:8085

---

### **Step 6: (Optional) Start API Gateway**

Open a **new PowerShell window**:

```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet\api-gateway

mvn spring-boot:run
```

**Gateway will be available at:** http://localhost:8080

---

### **Step 7: (Optional) Start Other Services**

If you want the complete system:

**Dinner Service:**
```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet\dinnerService
mvn spring-boot:run
```
Available at: http://localhost:8084

**User Service:**
```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet\userService
mvn spring-boot:run
```
Available at: http://localhost:8087

---

## 📧 **Step 8: Open MailDev to See Emails**

**Open your browser and go to:**
```
http://localhost:1080
```

This is the **MailDev Web UI** where you'll see all emails captured!

**Features:**
- 📬 **Inbox** - All captured emails
- 📝 **Preview** - HTML and plain text views
- 🔍 **Search** - Find specific emails
- 🗑️ **Delete** - Clear inbox

---

## 🧪 **Step 9: Test the Event-Driven Flow**

Now let's create a reservation and see the email notification!

### **Option A: Using cURL (PowerShell)**

```powershell
# Create a reservation
Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/reservations" `
  -ContentType "application/json" `
  -Body '{
    "dinnerId": 100,
    "guestId": 50,
    "reservationDate": "2026-02-10T19:00:00",
    "restaurantName": "Italian Kitchen"
  }'
```

### **Option B: Using cURL (if available)**

```bash
curl -X POST http://localhost:8083/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "dinnerId": 100,
    "guestId": 50,
    "reservationDate": "2026-02-10T19:00:00",
    "restaurantName": "Italian Kitchen"
  }'
```

### **Option C: Using Postman**

1. Method: **POST**
2. URL: `http://localhost:8083/api/reservations`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "dinnerId": 100,
  "guestId": 50,
  "reservationDate": "2026-02-10T19:00:00",
  "restaurantName": "Italian Kitchen"
}
```

---

## 🎯 **Expected Results**

### **1. Reservation Service Console:**
```
Publishing ReservationCreated event to Kafka
Event published successfully to topic: reservation-created
```

### **2. Kafka Topic (You can verify):**
```powershell
# View Kafka topics
docker exec -it ms_kafka kafka-topics --list --bootstrap-server localhost:9092

# Should show:
# reservation-created
# reservation-canceled
# reservation-created.DLT
# reservation-canceled.DLT
```

### **3. Notification Service Console:**
```
Consumed ReservationCreated event from Kafka
Correlation ID: reservation-<UUID>
Processing reservation for Guest ID: 50
Sending notification email...
Email sent successfully!
```

### **4. MailDev UI (http://localhost:1080):**
You should see a **NEW EMAIL** appear in the inbox! 📧

**Email Details:**
- **From:** noreply@buber-dinner.com
- **To:** guest-50@example.com (or configured email)
- **Subject:** Reservation Confirmation - Italian Kitchen
- **Body:** Details about the reservation

---

## 🔍 **Monitoring & Verification**

### **Check Eureka Dashboard**
```
http://localhost:8761
```
Should show all registered services:
- RESERVATIONSERVICE
- NOTIFICATIONSERVICE
- DINNERSERVICE (if started)
- USERSERVICE (if started)

### **Check Kafka Topics**
```powershell
# List all topics
docker exec -it ms_kafka kafka-topics --list --bootstrap-server localhost:9092

# Read messages from reservation-created topic
docker exec -it ms_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic reservation-created --from-beginning
```

### **Check PostgreSQL Database**
```
http://localhost:5050
```
- Email: pgadmin4@pgadmin.org
- Password: admin
- Add server: postgresql:5432
- Username: chahid, Password: chahid

### **Check Zipkin Tracing**
```
http://localhost:9411
```
View distributed traces across services.

---

## 📊 **System Architecture Verification**

After all services are running, you should see this flow:

```
1. Client (Postman/cURL)
   ↓ HTTP POST
2. Reservation Service (localhost:8083)
   ↓ Publishes Event
3. Kafka Topic (reservation-created)
   ↓ Event Consumed
4. Notification Service (localhost:8085)
   ↓ Sends Email via SMTP
5. MailDev (localhost:1080)
   ✅ Email Captured and Displayed!
```

---

## 🛠️ **Troubleshooting**

### **Issue: Services can't connect to Kafka**
```powershell
# Restart Kafka
docker-compose restart kafka

# Wait 30 seconds, then restart your services
```

### **Issue: Eureka not showing services**
- Wait 30-60 seconds (services register on startup)
- Check service logs for "Registered instance" messages

### **Issue: No emails appearing in MailDev**
1. Check Notification Service logs for errors
2. Verify MailDev is running: `docker ps | findstr mail-dev`
3. Check email configuration in `notificationService/application.properties`:
```properties
spring.mail.host=localhost
spring.mail.port=1025
```

### **Issue: Kafka consumer not receiving messages**
```powershell
# Check consumer group
docker exec -it ms_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Check consumer lag
docker exec -it ms_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group notification-service-group --describe
```

---

## 🧹 **Cleanup / Shutdown**

### **Stop All Services (Gracefully)**

1. **Stop Spring Boot services:** Press `Ctrl+C` in each PowerShell window

2. **Stop Docker services:**
```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet
docker-compose down
```

### **Clean Up Everything (Including Data)**
```powershell
# Stop and remove containers, networks, volumes
docker-compose down -v

# Remove all stopped containers
docker container prune -f

# Remove all unused volumes
docker volume prune -f
```

---

## 🎮 **Quick Start Script**

Create a file `start-all.ps1` in your project root:

```powershell
# Start infrastructure
Write-Host "Starting Docker services..." -ForegroundColor Green
docker-compose up -d

Write-Host "Waiting for Kafka to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "Open MailDev in browser: http://localhost:1080" -ForegroundColor Cyan
Start-Process "http://localhost:1080"

Write-Host "`nNow start your services in separate terminals:" -ForegroundColor Green
Write-Host "1. Discovery Service (port 8761)" -ForegroundColor Cyan
Write-Host "2. Reservation Service (port 8083)" -ForegroundColor Cyan
Write-Host "3. Notification Service (port 8085)" -ForegroundColor Cyan

Write-Host "`nTest with:" -ForegroundColor Green
Write-Host 'Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/reservations" -ContentType "application/json" -Body ''{"dinnerId": 100, "guestId": 50, "reservationDate": "2026-02-10T19:00:00", "restaurantName": "Italian Kitchen"}''' -ForegroundColor Yellow
```

Run it with:
```powershell
.\start-all.ps1
```

---

## 📝 **Service Startup Order (Important!)**

Follow this order for clean startup:

1. ✅ **Docker Compose** (Kafka, MailDev, etc.)
2. ✅ **Discovery Service** (Eureka)
3. ✅ **Reservation Service**
4. ✅ **Notification Service**
5. ✅ **API Gateway** (Optional)
6. ✅ **Other Services** (Optional)

---

## 🎉 **Success Indicators**

You know everything is working when:

✅ All Docker containers show "Up" status
✅ Eureka Dashboard shows registered services
✅ Service logs show "Started successfully"
✅ POST request returns 201 Created
✅ Kafka logs show message published
✅ Notification Service logs show event consumed
✅ **Email appears in MailDev UI at http://localhost:1080** 🎯

---

## 📧 **Configuring Email Content**

To customize email content, edit:
```
notificationService/src/main/java/com/buberdinner/NotificationService/...
```

Update the email templates in your notification service implementation.

---

## 🔗 **Useful URLs Summary**

| Service | URL | Purpose |
|---------|-----|---------|
| **MailDev** | http://localhost:1080 | **📧 View Emails** |
| Eureka | http://localhost:8761 | Service Registry |
| Reservation API | http://localhost:8083 | Create Reservations |
| Notification API | http://localhost:8085 | Notification Service |
| API Gateway | http://localhost:8080 | Gateway |
| PgAdmin | http://localhost:5050 | PostgreSQL UI |
| Mongo Express | http://localhost:8081 | MongoDB UI |
| Zipkin | http://localhost:9411 | Distributed Tracing |

---

## 🏆 **Final Test Command**

After everything is running, execute this to see the full flow:

```powershell
# Create a reservation
Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/reservations" `
  -ContentType "application/json" `
  -Body '{"dinnerId": 100, "guestId": 50, "reservationDate": "2026-02-10T19:00:00", "restaurantName": "Italian Kitchen"}'

# Then immediately open MailDev
Start-Process "http://localhost:1080"
```

**You should see the email notification arrive within seconds!** 🎊

---

**Created:** February 8, 2026
**Status:** ✅ Complete Runbook
**Architecture:** Event-Driven Microservices with Kafka
**Email System:** MailDev (SMTP Capture)

