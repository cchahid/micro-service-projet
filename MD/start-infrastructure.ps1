# Quick Start Script for Event-Driven Microservices Architecture
# This script starts all Docker infrastructure services

Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Event-Driven Microservices - Quick Start Script          ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "[1/5] Checking Docker status..." -ForegroundColor Yellow
$dockerRunning = docker info 2>&1 | Select-String "Server Version"
if (-not $dockerRunning) {
    Write-Host "❌ Docker is not running! Please start Docker Desktop first." -ForegroundColor Red
    exit 1
}
Write-Host "✅ Docker is running" -ForegroundColor Green
Write-Host ""

# Start Docker Compose services
Write-Host "[2/5] Starting Docker services (Kafka, Zookeeper, MailDev, PostgreSQL, etc.)..." -ForegroundColor Yellow
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Docker services started successfully" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to start Docker services" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Wait for Kafka to be ready
Write-Host "[3/5] Waiting for Kafka to be ready (30 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30
Write-Host "✅ Kafka should be ready now" -ForegroundColor Green
Write-Host ""

# Show running containers
Write-Host "[4/5] Docker services status:" -ForegroundColor Yellow
docker-compose ps
Write-Host ""

# Open useful URLs
Write-Host "[5/5] Opening useful URLs in browser..." -ForegroundColor Yellow
Start-Sleep -Seconds 2

# Open MailDev
Write-Host "📧 Opening MailDev (Email UI)..." -ForegroundColor Cyan
Start-Process "http://localhost:1080"

Start-Sleep -Seconds 1

# Open Eureka (will open once you start it)
Write-Host "📊 Eureka Dashboard will be available at: http://localhost:8761" -ForegroundColor Cyan

Write-Host ""
Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║                    Infrastructure Ready!                    ║" -ForegroundColor Green
Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

Write-Host "📋 Next Steps:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1️⃣  Start Discovery Service (Eureka):" -ForegroundColor White
Write-Host "   cd discoveryService" -ForegroundColor Gray
Write-Host "   mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""

Write-Host "2️⃣  Start Reservation Service (in new terminal):" -ForegroundColor White
Write-Host "   cd reservationService" -ForegroundColor Gray
Write-Host "   mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""

Write-Host "3️⃣  Start Notification Service (in new terminal):" -ForegroundColor White
Write-Host "   cd notificationService" -ForegroundColor Gray
Write-Host "   mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""

Write-Host "4️⃣  Test by creating a reservation:" -ForegroundColor White
Write-Host '   Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/reservations" `' -ForegroundColor Gray
Write-Host '     -ContentType "application/json" `' -ForegroundColor Gray
Write-Host "     -Body '{\"dinnerId\": 100, \"guestId\": 50, \"reservationDate\": \"2026-02-10T19:00:00\", \"restaurantName\": \"Italian Kitchen\"}'" -ForegroundColor Gray
Write-Host ""

Write-Host "5️⃣  Check emails at: http://localhost:1080 📧" -ForegroundColor White
Write-Host ""

Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                    Useful URLs                              ║" -ForegroundColor Cyan
Write-Host "╠══════════════════════════════════════════════════════════════╣" -ForegroundColor Cyan
Write-Host "║ MailDev (Emails):      http://localhost:1080               ║" -ForegroundColor White
Write-Host "║ Eureka Dashboard:      http://localhost:8761               ║" -ForegroundColor White
Write-Host "║ Reservation API:       http://localhost:8083               ║" -ForegroundColor White
Write-Host "║ Notification API:      http://localhost:8085               ║" -ForegroundColor White
Write-Host "║ PgAdmin:               http://localhost:5050               ║" -ForegroundColor White
Write-Host "║ Mongo Express:         http://localhost:8081               ║" -ForegroundColor White
Write-Host "║ Zipkin Tracing:        http://localhost:9411               ║" -ForegroundColor White
Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

Write-Host "💡 Tip: Keep this terminal open. When done, run:" -ForegroundColor Yellow
Write-Host "   docker-compose down" -ForegroundColor Gray
Write-Host ""

