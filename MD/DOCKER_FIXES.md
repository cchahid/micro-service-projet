# Docker Container Fixes - Kafka and Mongo Express

## Issues Found

From the Docker Desktop screenshot, two containers were not running:
1. **ms_kafka** - Kafka message broker (stopped)
2. **mongo_expre** - MongoDB Express UI (stopped)

## Fixes Applied

### 1. Kafka Configuration Fix

**Issue:** Kafka container was missing the `KAFKA_BROKER_ID` environment variable, which is required for Kafka to start properly.

**Fix:** Added `KAFKA_BROKER_ID: 1` to the Kafka service configuration in `docker-compose.yml`.

**Before:**
```yaml
kafka:
  environment:
    KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    # Missing KAFKA_BROKER_ID
```

**After:**
```yaml
kafka:
  environment:
    KAFKA_BROKER_ID: 1  # Added this line
    KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
```

### 2. Mongo Express Configuration Fix

**Issue:** The `mongo-express` service was missing the `networks` configuration, which prevented it from communicating with other services.

**Fix:** Added `networks: - microservices-net` to the mongo-express service configuration.

**Before:**
```yaml
mongo-express:
  container_name: mongo_express
  depends_on:
    - mongodb
  # Missing networks configuration
```

**After:**
```yaml
mongo-express:
  container_name: mongo_express
  depends_on:
    - mongodb
  networks:
    - microservices-net  # Added this
```

## How to Apply the Fixes

The fixes have been applied to your `docker-compose.yml` file. To restart the containers with the new configuration:

```powershell
# Stop all containers
cd C:\Users\chahid\IdeaProjects\micro-service-projet
docker-compose down

# Start all containers with fixed configuration
docker-compose up -d

# Wait 30 seconds for Kafka to fully start
Start-Sleep -Seconds 30

# Check container status
docker-compose ps
```

## Verification

After restarting, verify the containers are running:

```powershell
# Check all containers
docker-compose ps

# Check Kafka logs
docker logs ms_kafka --tail 50

# Check Mongo Express logs
docker logs mongo_express --tail 50

# Verify Kafka is ready
docker exec -it ms_kafka kafka-topics --list --bootstrap-server localhost:9092
```

## Expected Results

After applying these fixes:

✅ **ms_kafka** should show status "Up"
✅ **mongo_express** should show status "Up"
✅ Kafka should be accessible on port 9092
✅ Mongo Express UI should be accessible at http://localhost:8081

## Additional Notes

### Kafka Startup Time
- Kafka typically takes 30-60 seconds to fully start
- Wait for the message "Kafka Server started" in the logs before using it

### Mongo Express Access
- URL: http://localhost:8081
- Username: admin
- Password: admin123

### If Containers Still Don't Start

If issues persist, try:

```powershell
# Clean restart with volume removal
docker-compose down -v

# Rebuild and start
docker-compose up -d --build

# Check logs for specific errors
docker-compose logs kafka
docker-compose logs mongo-express
```

## Common Kafka Errors

If Kafka still has issues, check for:
- Port 9092 already in use: `netstat -ano | findstr :9092`
- Zookeeper not ready: Kafka depends on zookeeper starting first
- Insufficient memory: Kafka needs at least 1GB RAM

## Common Mongo Express Errors

If Mongo Express has issues, check for:
- MongoDB not ready: mongo-express depends on mongodb starting first
- Wrong credentials: Verify ME_CONFIG_MONGODB_ADMINUSERNAME and ME_CONFIG_MONGODB_ADMINPASSWORD match mongodb credentials
- Port 8081 already in use: `netstat -ano | findstr :8081`

---

**Date:** February 8, 2026
**Status:** ✅ Fixes Applied
**Files Modified:** docker-compose.yml
**Action Required:** Restart containers with `docker-compose down && docker-compose up -d`

