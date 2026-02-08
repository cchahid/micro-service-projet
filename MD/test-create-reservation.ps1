# Test Script - Create Reservation and Trigger Email Notification
# This script sends a POST request to create a reservation

Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║         Test Event-Driven Architecture - Create Reservation ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Define the reservation data
$reservationData = @{
    dinnerId = 100
    guestId = 50
    reservationDate = "2026-02-10T19:00:00"
    restaurantName = "Italian Kitchen"
} | ConvertTo-Json

Write-Host "📝 Reservation Details:" -ForegroundColor Yellow
Write-Host $reservationData -ForegroundColor White
Write-Host ""

Write-Host "🚀 Sending request to Reservation Service..." -ForegroundColor Yellow

try {
    # Send POST request
    $response = Invoke-RestMethod -Method Post `
        -Uri "http://localhost:8083/api/reservations" `
        -ContentType "application/json" `
        -Body $reservationData `
        -ErrorAction Stop

    Write-Host "✅ Reservation created successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📋 Response:" -ForegroundColor Cyan
    $response | Format-List
    Write-Host ""

    Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║                    What Happens Next                        ║" -ForegroundColor Green
    Write-Host "╠══════════════════════════════════════════════════════════════╣" -ForegroundColor Green
    Write-Host "║ 1️⃣  Reservation Service publishes event to Kafka            ║" -ForegroundColor White
    Write-Host "║ 2️⃣  Event queued in 'reservation-created' topic             ║" -ForegroundColor White
    Write-Host "║ 3️⃣  Notification Service consumes the event                 ║" -ForegroundColor White
    Write-Host "║ 4️⃣  Email notification sent to MailDev                      ║" -ForegroundColor White
    Write-Host "║ 5️⃣  Check your email at: http://localhost:1080             ║" -ForegroundColor White
    Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Green
    Write-Host ""

    # Wait a moment
    Write-Host "⏳ Waiting 3 seconds for event processing..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3

    # Open MailDev
    Write-Host "📧 Opening MailDev to view email..." -ForegroundColor Cyan
    Start-Process "http://localhost:1080"

    Write-Host ""
    Write-Host "✨ Done! Check MailDev for the email notification." -ForegroundColor Green

} catch {
    Write-Host "❌ Error creating reservation!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error Details:" -ForegroundColor Yellow
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""

    Write-Host "🔍 Troubleshooting:" -ForegroundColor Yellow
    Write-Host "1. Is Reservation Service running on port 8083?" -ForegroundColor White
    Write-Host "   Check: http://localhost:8083/actuator/health" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Is Discovery Service (Eureka) running?" -ForegroundColor White
    Write-Host "   Check: http://localhost:8761" -ForegroundColor Gray
    Write-Host ""
    Write-Host "3. Are Docker services running?" -ForegroundColor White
    Write-Host "   Run: docker-compose ps" -ForegroundColor Gray
    Write-Host ""
}

Write-Host ""
Write-Host "💡 To create another reservation, run this script again!" -ForegroundColor Cyan
Write-Host "   .\test-create-reservation.ps1" -ForegroundColor Gray
Write-Host ""

