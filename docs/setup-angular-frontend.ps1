# Quick Start Angular Frontend
# This script automates the Angular project setup

Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║       Angular Frontend Setup - Buber Dinner Project         ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Check if Node.js is installed
Write-Host "[1/7] Checking Node.js installation..." -ForegroundColor Yellow
try {
    $nodeVersion = node --version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Node.js is installed ($nodeVersion)" -ForegroundColor Green
    } else {
        Write-Host "❌ Node.js is not installed!" -ForegroundColor Red
        Write-Host "   Please install Node.js from https://nodejs.org/" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "❌ Node.js is not installed!" -ForegroundColor Red
    Write-Host "   Please install Node.js from https://nodejs.org/" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Check if Angular CLI is installed
Write-Host "[2/7] Checking Angular CLI..." -ForegroundColor Yellow
$ngVersion = ng version 2>&1 | Select-String "Angular CLI"
if ($ngVersion) {
    Write-Host "✅ Angular CLI is installed" -ForegroundColor Green
} else {
    Write-Host "⚠️  Angular CLI not found. Installing globally..." -ForegroundColor Yellow
    npm install -g @angular/cli
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Angular CLI installed successfully" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to install Angular CLI" -ForegroundColor Red
        exit 1
    }
}
Write-Host ""

# Navigate to project directory
Write-Host "[3/7] Navigating to project directory..." -ForegroundColor Yellow
$projectPath = "C:\Users\chahid\IdeaProjects\micro-service-projet"
if (Test-Path $projectPath) {
    Set-Location $projectPath
    Write-Host "✅ Found project at: $projectPath" -ForegroundColor Green
} else {
    Write-Host "❌ Project directory not found: $projectPath" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Check if frontend already exists
Write-Host "[4/7] Checking if frontend already exists..." -ForegroundColor Yellow
if (Test-Path "frontend") {
    Write-Host "⚠️  Frontend directory already exists!" -ForegroundColor Yellow
    $response = Read-Host "Do you want to delete it and start fresh? (y/N)"
    if ($response -eq 'y' -or $response -eq 'Y') {
        Write-Host "   Removing existing frontend directory..." -ForegroundColor Gray
        Remove-Item -Path "frontend" -Recurse -Force
        Write-Host "✅ Existing frontend removed" -ForegroundColor Green
    } else {
        Write-Host "   Keeping existing frontend. Exiting..." -ForegroundColor Yellow
        exit 0
    }
}
Write-Host ""

# Create Angular project
Write-Host "[5/7] Creating Angular project..." -ForegroundColor Yellow
Write-Host "   This may take a few minutes..." -ForegroundColor Gray
Write-Host ""

# Create project with routing and SCSS
ng new frontend --routing --style=scss --strict --skip-git

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Angular project created successfully" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to create Angular project" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Navigate to frontend
Set-Location frontend

# Install additional dependencies
Write-Host "[6/7] Installing additional dependencies..." -ForegroundColor Yellow
Write-Host "   Installing Angular Material, Bootstrap, and other packages..." -ForegroundColor Gray

npm install --save @angular/material @angular/cdk bootstrap

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Dependencies installed successfully" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to install dependencies" -ForegroundColor Red
}
Write-Host ""

# Generate project structure
Write-Host "[7/7] Generating project structure..." -ForegroundColor Yellow

# Create directories
New-Item -ItemType Directory -Force -Path "src/app/models" | Out-Null
New-Item -ItemType Directory -Force -Path "src/app/services" | Out-Null
New-Item -ItemType Directory -Force -Path "src/app/components" | Out-Null
New-Item -ItemType Directory -Force -Path "src/app/guards" | Out-Null
New-Item -ItemType Directory -Force -Path "src/app/interceptors" | Out-Null

# Generate models
ng generate interface models/reservation --skip-tests | Out-Null
ng generate interface models/dinner --skip-tests | Out-Null
ng generate interface models/user --skip-tests | Out-Null

# Generate services
ng generate service services/reservation --skip-tests | Out-Null
ng generate service services/dinner --skip-tests | Out-Null
ng generate service services/user --skip-tests | Out-Null
ng generate service services/auth --skip-tests | Out-Null

# Generate components
ng generate component components/home --skip-tests | Out-Null
ng generate component components/navigation --skip-tests | Out-Null
ng generate component components/reservation-list --skip-tests | Out-Null
ng generate component components/reservation-create --skip-tests | Out-Null
ng generate component components/dinner-list --skip-tests | Out-Null
ng generate component components/dinner-detail --skip-tests | Out-Null

# Generate guard
ng generate guard guards/auth --skip-tests | Out-Null

Write-Host "✅ Project structure generated" -ForegroundColor Green
Write-Host ""

# Summary
Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║              Angular Frontend Setup Complete!                ║" -ForegroundColor Green
Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

Write-Host "📦 Project Structure Created:" -ForegroundColor Cyan
Write-Host "   ✅ Models: reservation, dinner, user" -ForegroundColor White
Write-Host "   ✅ Services: reservation, dinner, user, auth" -ForegroundColor White
Write-Host "   ✅ Components: home, navigation, reservation-list, etc." -ForegroundColor White
Write-Host "   ✅ Guards: auth guard for protected routes" -ForegroundColor White
Write-Host ""

Write-Host "📚 Next Steps:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1️⃣  Copy code templates from ANGULAR_CODE_TEMPLATES.md" -ForegroundColor White
Write-Host "   - Models, Services, Components" -ForegroundColor Gray
Write-Host "   - HTTP Interceptors, Guards" -ForegroundColor Gray
Write-Host ""

Write-Host "2️⃣  Configure environment files:" -ForegroundColor White
Write-Host "   cd frontend/src/environments" -ForegroundColor Gray
Write-Host "   - Edit environment.ts with your API URLs" -ForegroundColor Gray
Write-Host ""

Write-Host "3️⃣  Enable CORS in your backend services:" -ForegroundColor White
Write-Host "   - Add CORS configuration to Spring Boot services" -ForegroundColor Gray
Write-Host "   - Allow origin: http://localhost:4200" -ForegroundColor Gray
Write-Host ""

Write-Host "4️⃣  Start the development server:" -ForegroundColor White
Write-Host "   cd frontend" -ForegroundColor Gray
Write-Host "   ng serve --open" -ForegroundColor Gray
Write-Host ""

Write-Host "5️⃣  Access your app:" -ForegroundColor White
Write-Host "   http://localhost:4200" -ForegroundColor Gray
Write-Host ""

Write-Host "💡 Useful Commands:" -ForegroundColor Cyan
Write-Host "   ng serve              - Start dev server" -ForegroundColor Gray
Write-Host "   ng build              - Build for production" -ForegroundColor Gray
Write-Host "   ng generate component - Create new component" -ForegroundColor Gray
Write-Host "   ng test               - Run unit tests" -ForegroundColor Gray
Write-Host ""

Write-Host "📖 Documentation Files:" -ForegroundColor Cyan
Write-Host "   - FRONTEND_ANGULAR_SETUP.md    - Complete setup guide" -ForegroundColor Gray
Write-Host "   - ANGULAR_CODE_TEMPLATES.md    - Ready-to-use code" -ForegroundColor Gray
Write-Host ""

Write-Host "🎉 Your Angular frontend is ready to develop!" -ForegroundColor Green
Write-Host ""

