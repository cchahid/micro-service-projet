# Frontend Setup Guide - Angular for Microservices

## 🎯 Overview

This guide will help you create an Angular frontend for your Buber Dinner microservices project.

## 📋 Prerequisites

- Node.js 18+ installed
- npm or yarn package manager
- Angular CLI

## 🛠️ Installation Steps

### Step 1: Install Angular CLI

```powershell
npm install -g @angular/cli
```

Verify installation:
```powershell
ng version
```

### Step 2: Create Angular Project

Navigate to your project root and create the Angular app:

```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet

# Create Angular app in a new 'frontend' folder
ng new frontend --routing --style=scss --strict
```

**When prompted:**
- Would you like to add Angular routing? → **Yes**
- Which stylesheet format? → **SCSS**

### Step 3: Navigate to Frontend

```powershell
cd frontend
```

### Step 4: Install Additional Dependencies

```powershell
# HTTP client (already included in Angular)
# RxJS (already included)

# Optional but recommended packages:
npm install @angular/material @angular/cdk
npm install bootstrap
npm install rxjs
npm install socket.io-client  # For real-time updates
npm install jwt-decode  # For JWT token handling
```

---

## 📁 Project Structure

After creation, your structure will be:

```
micro-service-projet/
├── frontend/                    # ← NEW Angular app
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/     # UI components
│   │   │   ├── services/       # API services
│   │   │   ├── models/         # TypeScript interfaces
│   │   │   ├── guards/         # Route guards
│   │   │   └── app.component.ts
│   │   ├── assets/
│   │   ├── environments/
│   │   └── index.html
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
├── reservationService/
├── notificationService/
├── dinnerService/
├── userService/
├── api-gateway/
└── discoveryService/
```

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     Angular Frontend (Port 4200)                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │ Reservations │  │   Dinners    │  │    Users     │         │
│  │  Component   │  │  Component   │  │  Component   │         │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘         │
│         │                  │                  │                  │
│         └──────────────────┴──────────────────┘                 │
│                            │                                     │
│                   ┌────────▼────────┐                          │
│                   │  API Services   │                          │
│                   │  (HTTP Client)  │                          │
│                   └────────┬────────┘                          │
└────────────────────────────┼──────────────────────────────────┘
                             │
                             │ HTTP Requests
                             ▼
                  ┌──────────────────────┐
                  │   API Gateway        │
                  │   (Port 8080)        │
                  └──────────┬───────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
  ┌────────────┐      ┌────────────┐     ┌────────────┐
  │Reservation │      │   Dinner   │     │    User    │
  │  Service   │      │  Service   │     │  Service   │
  │ (Port 8083)│      │ (Port 8084)│     │ (Port 8087)│
  └────────────┘      └────────────┘     └────────────┘
```

---

## 🔧 Configuration

### Configure API Gateway URL

Create environment files for different stages:

**src/environments/environment.ts** (Development):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',  // Via API Gateway
  // Direct service URLs (for debugging)
  reservationServiceUrl: 'http://localhost:8083/api',
  dinnerServiceUrl: 'http://localhost:8084/api',
  userServiceUrl: 'http://localhost:8087/api',
  notificationServiceUrl: 'http://localhost:8085/api',
  // WebSocket for real-time updates
  wsUrl: 'ws://localhost:8080/ws'
};
```

**src/environments/environment.prod.ts** (Production):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-domain.com/api',
  wsUrl: 'wss://your-domain.com/ws'
};
```

---

## 📦 Generate Core Structure

Run these commands to generate the basic structure:

```powershell
# Generate models
ng generate interface models/reservation
ng generate interface models/dinner
ng generate interface models/user
ng generate interface models/notification

# Generate services
ng generate service services/reservation
ng generate service services/dinner
ng generate service services/user
ng generate service services/notification
ng generate service services/auth

# Generate components
ng generate component components/reservation-list
ng generate component components/reservation-create
ng generate component components/dinner-list
ng generate component components/dinner-detail
ng generate component components/user-profile
ng generate component components/navigation
ng generate component components/home

# Generate guards
ng generate guard guards/auth
```

---

## 🎨 Install UI Framework (Optional)

### Option 1: Angular Material (Recommended)

```powershell
ng add @angular/material
```

Select a theme when prompted (e.g., Indigo/Pink)

### Option 2: Bootstrap

```powershell
npm install bootstrap

# Add to angular.json styles:
"styles": [
  "node_modules/bootstrap/dist/css/bootstrap.min.css",
  "src/styles.scss"
]
```

---

## 🚀 Run the Development Server

```powershell
cd frontend
ng serve
```

The app will be available at: **http://localhost:4200**

With live reload - changes auto-refresh the browser!

---

## 🔗 Integrate with Your Microservices

### Enable CORS in Spring Boot Services

Add this to each service's configuration:

**reservationService/src/main/resources/application.properties:**
```properties
# CORS Configuration
spring.web.cors.allowed-origins=http://localhost:4200
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

Or create a CORS configuration class:

**src/main/java/com/.../config/CorsConfig.java:**
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

---

## 📝 Example: Reservation Service Integration

**src/app/services/reservation.service.ts:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Reservation } from '../models/reservation';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/reservations`;

  constructor(private http: HttpClient) {}

  getAllReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.apiUrl);
  }

  getReservationById(id: string): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/${id}`);
  }

  createReservation(reservation: Reservation): Observable<Reservation> {
    return this.http.post<Reservation>(this.apiUrl, reservation);
  }

  cancelReservation(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

**src/app/models/reservation.ts:**
```typescript
export interface Reservation {
  id?: string;
  dinnerId: number;
  guestId: number;
  reservationDate: Date;
  restaurantName: string;
  status?: string;
}
```

**src/app/components/reservation-create/reservation-create.component.ts:**
```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReservationService } from '../../services/reservation.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reservation-create',
  templateUrl: './reservation-create.component.html',
  styleUrls: ['./reservation-create.component.scss']
})
export class ReservationCreateComponent {
  reservationForm: FormGroup;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private router: Router
  ) {
    this.reservationForm = this.fb.group({
      dinnerId: ['', [Validators.required]],
      guestId: ['', [Validators.required]],
      reservationDate: ['', [Validators.required]],
      restaurantName: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.reservationForm.valid) {
      this.loading = true;
      this.reservationService.createReservation(this.reservationForm.value)
        .subscribe({
          next: (reservation) => {
            console.log('Reservation created:', reservation);
            this.router.navigate(['/reservations']);
          },
          error: (error) => {
            console.error('Error creating reservation:', error);
            this.error = 'Failed to create reservation';
            this.loading = false;
          }
        });
    }
  }
}
```

---

## 🔄 Real-Time Updates with WebSocket

For receiving real-time notifications (optional):

**src/app/services/websocket.service.ts:**
```typescript
import { Injectable } from '@angular/core';
import { io, Socket } from 'socket.io-client';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket: Socket;

  constructor() {
    this.socket = io(environment.wsUrl);
  }

  listen(eventName: string): Observable<any> {
    return new Observable((subscriber) => {
      this.socket.on(eventName, (data) => {
        subscriber.next(data);
      });
    });
  }

  emit(eventName: string, data: any): void {
    this.socket.emit(eventName, data);
  }
}
```

---

## 🧪 Testing

### Run Unit Tests
```powershell
ng test
```

### Run E2E Tests
```powershell
ng e2e
```

---

## 📦 Build for Production

```powershell
ng build --configuration production
```

Output will be in `dist/frontend/` - ready to deploy!

---

## 🌐 Deployment Options

1. **Static Hosting:** Deploy to Netlify, Vercel, or AWS S3
2. **Docker:** Containerize with Nginx
3. **Same Server:** Serve from Spring Boot (add Angular build to resources)

---

## 📚 Next Steps

1. ✅ Install Node.js and Angular CLI
2. ✅ Create Angular project
3. ✅ Generate components and services
4. ✅ Configure environment files
5. ✅ Enable CORS in backend services
6. ✅ Implement reservation features
7. ✅ Add authentication (JWT)
8. ✅ Style with Material/Bootstrap
9. ✅ Add real-time updates (WebSocket)
10. ✅ Deploy to production

---

**Created:** February 8, 2026  
**Framework:** Angular 17+  
**Backend:** Spring Boot Microservices  
**Architecture:** Event-Driven with API Gateway  
**Status:** ✅ Ready to implement

