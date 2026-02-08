# COMPLETE ANGULAR FRONTEND CODE - COPY & PASTE READY

## ⚠️ IMPORTANT SETUP FIRST

Run these commands to create the project structure:

```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet

# Create project
ng new frontend --routing --style=scss --strict

# Navigate to frontend
cd frontend

# Install dependencies
npm install @angular/material @angular/cdk bootstrap socket.io-client jwt-decode

# Generate structure
mkdir -p src/app/models src/app/services src/app/components src/app/guards src/app/interceptors

# Generate services (optional - or copy code manually)
ng generate service services/dinner --skip-tests
ng generate service services/reservation --skip-tests
ng generate service services/auth --skip-tests
ng generate service services/review --skip-tests
ng generate service services/notification --skip-tests

# Generate guards
ng generate guard guards/auth --skip-tests

# Generate interceptors (create manually - not auto-generated)

# Generate components
ng generate component components/navbar --skip-tests
ng generate component components/home --skip-tests
ng generate component components/dinner-list --skip-tests
ng generate component components/dinner-detail --skip-tests
ng generate component components/reservation-create --skip-tests
ng generate component components/my-reservations --skip-tests
ng generate component components/add-review --skip-tests
ng generate component components/login --skip-tests
ng generate component components/profile --skip-tests
```

---

## 📦 1. MODELS (TypeScript Interfaces)

### src/app/models/dinner.ts

```typescript
export interface Dinner {
  id: number;
  name: string;
  description: string;
  startTime: string;
  endTime: string;
  cuisineType: string;
  maxGuestCount: number;
  hostId: number;
  address: string;
  imageUrl?: string;
  price?: number;
}

export interface DinnerRequest {
  name: string;
  description: string;
  startTime: string;
  endTime: string;
  cuisineType: string;
  maxGuestCount: number;
  hostId: number;
  address: string;
  price?: number;
}

export interface Menu {
  id: number;
  name: string;
  description: string;
  dinnerIds: number[];
}
```

### src/app/models/reservation.ts

```typescript
export interface Reservation {
  id: string;
  dinnerId: number;
  guestId: number;
  reservationDate: string;
  restaurantName: string;
  status?: string;
  createdAt?: string;
}

export interface CreateReservationRequest {
  dinnerId: number;
  guestId: number;
  reservationDate: string;
  restaurantName: string;
}

export interface ReservationResponse {
  reservationId: string;
  dinnerId: number;
  guestId: number;
  reservationDate: string;
}
```

### src/app/models/review.ts

```typescript
export interface Review {
  id: number;
  dinnerId: number;
  userId: number;
  rating: number;
  comment: string;
  createdAt?: string;
}

export interface ReviewRequest {
  dinnerId: number;
  userId: number;
  rating: number;
  comment: string;
}
```

### src/app/models/user.ts

```typescript
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role?: string;
  createdAt?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}
```

### src/app/models/notification.ts

```typescript
export interface Notification {
  id: string;
  userId: number;
  type: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}
```

---

## 🔧 2. ENVIRONMENT CONFIGURATION

### src/environments/environment.ts

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  
  // Service URLs
  dinnerServiceUrl: 'http://localhost:8084/api',
  reservationServiceUrl: 'http://localhost:8083/api',
  userServiceUrl: 'http://localhost:8087/api',
  notificationServiceUrl: 'http://localhost:8085/api'
};
```

### src/environments/environment.prod.ts

```typescript
export const environment = {
  production: true,
  apiUrl: '/api',
  
  dinnerServiceUrl: '/api',
  reservationServiceUrl: '/api',
  userServiceUrl: '/api',
  notificationServiceUrl: '/api'
};
```

---

## 🔌 3. SERVICES

### src/app/services/dinner.service.ts

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Dinner, DinnerRequest } from '../models/dinner';

@Injectable({
  providedIn: 'root'
})
export class DinnerService {
  private apiUrl = `${environment.apiUrl}/dinners`;

  constructor(private http: HttpClient) {}

  getAllDinners(): Observable<Dinner[]> {
    return this.http.get<Dinner[]>(this.apiUrl);
  }

  getDinnerById(id: number): Observable<Dinner> {
    return this.http.get<Dinner>(`${this.apiUrl}/${id}`);
  }

  getDinnersByHostId(hostId: number): Observable<Dinner[]> {
    return this.http.get<Dinner[]>(`${this.apiUrl}/host/${hostId}`);
  }

  createDinner(dinnerData: Dinner, imageFile: File): Observable<Dinner> {
    const formData = new FormData();
    formData.append('dinner', new Blob([JSON.stringify(dinnerData)], { type: 'application/json' }));
    formData.append('image', imageFile);
    return this.http.post<Dinner>(this.apiUrl, formData);
  }

  updateDinner(id: number, dinner: Partial<Dinner>): Observable<Dinner> {
    return this.http.put<Dinner>(`${this.apiUrl}/${id}`, dinner);
  }

  deleteDinner(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  rescheduleDinner(id: number, newStartTime: string, newEndTime: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/reschedule`, { newStartTime, newEndTime });
  }
}
```

### src/app/services/reservation.service.ts

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Reservation, CreateReservationRequest, ReservationResponse } from '../models/reservation';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/v1/reservations`;

  constructor(private http: HttpClient) {}

  getAllReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.apiUrl);
  }

  getReservationById(id: string): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/${id}`);
  }

  getMyReservations(guestId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/guest/${guestId}`);
  }

  createReservation(reservation: CreateReservationRequest): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(this.apiUrl, reservation);
  }

  cancelReservation(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

### src/app/services/auth.service.ts

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { LoginRequest, LoginResponse, User, SignupRequest } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          localStorage.setItem('token', response.token);
          localStorage.setItem('currentUser', JSON.stringify(response.user));
          this.currentUserSubject.next(response.user);
        })
      );
  }

  signup(userData: SignupRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/signup`, userData)
      .pipe(
        tap(response => {
          localStorage.setItem('token', response.token);
          localStorage.setItem('currentUser', JSON.stringify(response.user));
          this.currentUserSubject.next(response.user);
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
```

### src/app/services/review.service.ts

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Review, ReviewRequest } from '../models/review';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) {}

  getReviewsByDinnerId(dinnerId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/dinner/${dinnerId}`);
  }

  createReview(review: ReviewRequest): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, review);
  }

  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

### src/app/services/notification.service.ts

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Notification } from '../models/notification';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getNotifications(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user/${userId}`);
  }

  markAsRead(id: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/read`, {});
  }
}
```

---

## 🛡️ 4. GUARDS & INTERCEPTORS

### src/app/guards/auth.guard.ts

```typescript
import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
```

### src/app/interceptors/auth.interceptor.ts

```typescript
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.logout();
        }
        return throwError(() => error);
      })
    );
  }
}
```

---

## 🧩 5. COMPONENTS (HTML + TS)

### src/app/components/navbar/navbar.component.html

```html
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
  <div class="container">
    <a class="navbar-brand" routerLink="/">🍽️ Buber Dinner</a>
    
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item">
          <a class="nav-link" routerLink="/dinners" routerLinkActive="active">Dinners</a>
        </li>
        
        <li class="nav-item" *ngIf="isLoggedIn">
          <a class="nav-link" routerLink="/my-reservations" routerLinkActive="active">My Reservations</a>
        </li>
        
        <li class="nav-item" *ngIf="isLoggedIn">
          <a class="nav-link" routerLink="/profile" routerLinkActive="active">Profile</a>
        </li>
        
        <li class="nav-item" *ngIf="!isLoggedIn">
          <a class="nav-link" routerLink="/login" routerLinkActive="active">Login</a>
        </li>
        
        <li class="nav-item" *ngIf="isLoggedIn">
          <button class="btn btn-outline-light btn-sm ms-2" (click)="logout()">Logout</button>
        </li>
      </ul>
    </div>
  </div>
</nav>
```

### src/app/components/navbar/navbar.component.ts

```typescript
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
```

### src/app/components/home/home.component.html

```html
<div class="container mt-5">
  <div class="row mb-5">
    <div class="col-lg-8">
      <h1>Welcome to Buber Dinner! 🍽️</h1>
      <p class="lead">Discover amazing dinners and make reservations</p>
      <a routerLink="/dinners" class="btn btn-primary btn-lg">Browse Dinners →</a>
    </div>
    <div class="col-lg-4">
      <div class="card bg-light">
        <div class="card-body">
          <h5 class="card-title">Featured Features</h5>
          <ul class="list-unstyled">
            <li>✅ Browse dinners</li>
            <li>✅ Make reservations</li>
            <li>✅ Read & write reviews</li>
            <li>✅ Manage bookings</li>
          </ul>
        </div>
      </div>
    </div>
  </div>

  <hr class="my-5">

  <h2>Latest Dinners</h2>
  <div class="row g-4">
    <div class="col-md-6" *ngFor="let dinner of latestDinners">
      <div class="card h-100">
        <img [src]="dinner.imageUrl" class="card-img-top" alt="{{ dinner.name }}" style="height: 200px; object-fit: cover;">
        <div class="card-body">
          <h5 class="card-title">{{ dinner.name }}</h5>
          <p class="card-text">{{ dinner.description | slice:0:100 }}...</p>
          <p class="text-muted">{{ dinner.cuisineType }} • Seats: {{ dinner.maxGuestCount }}</p>
          <a [routerLink]="['/dinners', dinner.id]" class="btn btn-primary btn-sm">View Details</a>
        </div>
      </div>
    </div>
  </div>
</div>
```

### src/app/components/home/home.component.ts

```typescript
import { Component, OnInit } from '@angular/core';
import { DinnerService } from '../../services/dinner.service';
import { Dinner } from '../../models/dinner';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  latestDinners: Dinner[] = [];

  constructor(private dinnerService: DinnerService) {}

  ngOnInit(): void {
    this.loadLatestDinners();
  }

  loadLatestDinners(): void {
    this.dinnerService.getAllDinners().subscribe({
      next: (dinners) => {
        this.latestDinners = dinners.slice(0, 6);
      },
      error: (error) => {
        console.error('Error loading dinners:', error);
      }
    });
  }
}
```

### src/app/components/dinner-list/dinner-list.component.html

```html
<div class="container mt-5">
  <h1>Available Dinners</h1>

  <div class="row mb-4">
    <div class="col-md-6">
      <input 
        type="text" 
        class="form-control" 
        placeholder="Search dinners..." 
        [(ngModel)]="searchTerm"
        (input)="filterDinners()">
    </div>
    <div class="col-md-6">
      <select class="form-select" [(ngModel)]="cuisineFilter" (change)="filterDinners()">
        <option value="">All Cuisines</option>
        <option value="Italian">Italian</option>
        <option value="French">French</option>
        <option value="Asian">Asian</option>
        <option value="American">American</option>
      </select>
    </div>
  </div>

  <div class="row g-4">
    <div class="col-md-6 col-lg-4" *ngFor="let dinner of filteredDinners">
      <div class="card h-100">
        <img [src]="dinner.imageUrl || 'assets/placeholder.jpg'" class="card-img-top" alt="{{ dinner.name }}" style="height: 200px; object-fit: cover;">
        <div class="card-body">
          <h5 class="card-title">{{ dinner.name }}</h5>
          <p class="card-text">{{ dinner.description | slice:0:80 }}...</p>
          <p class="text-muted small">
            📍 {{ dinner.address }}<br>
            🍴 {{ dinner.cuisineType }}<br>
            👥 {{ dinner.maxGuestCount }} guests
          </p>
          <a [routerLink]="['/dinners', dinner.id]" class="btn btn-primary btn-sm">View & Book</a>
        </div>
      </div>
    </div>
  </div>

  <div *ngIf="filteredDinners.length === 0" class="alert alert-info mt-4">
    No dinners found matching your criteria.
  </div>
</div>
```

### src/app/components/dinner-list/dinner-list.component.ts

```typescript
import { Component, OnInit } from '@angular/core';
import { DinnerService } from '../../services/dinner.service';
import { Dinner } from '../../models/dinner';

@Component({
  selector: 'app-dinner-list',
  templateUrl: './dinner-list.component.html',
  styleUrls: ['./dinner-list.component.scss']
})
export class DinnerListComponent implements OnInit {
  dinners: Dinner[] = [];
  filteredDinners: Dinner[] = [];
  searchTerm = '';
  cuisineFilter = '';

  constructor(private dinnerService: DinnerService) {}

  ngOnInit(): void {
    this.loadDinners();
  }

  loadDinners(): void {
    this.dinnerService.getAllDinners().subscribe({
      next: (dinners) => {
        this.dinners = dinners;
        this.filteredDinners = dinners;
      },
      error: (error) => {
        console.error('Error loading dinners:', error);
      }
    });
  }

  filterDinners(): void {
    this.filteredDinners = this.dinners.filter(dinner => {
      const matchesSearch = dinner.name.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesCuisine = !this.cuisineFilter || dinner.cuisineType === this.cuisineFilter;
      return matchesSearch && matchesCuisine;
    });
  }
}
```

### src/app/components/dinner-detail/dinner-detail.component.html

```html
<div class="container mt-5" *ngIf="dinner">
  <div class="row">
    <div class="col-lg-8">
      <img [src]="dinner.imageUrl" class="img-fluid rounded mb-4" alt="{{ dinner.name }}" style="max-height: 400px; object-fit: cover;">
      
      <h1>{{ dinner.name }}</h1>
      <p class="lead">{{ dinner.description }}</p>

      <div class="row mb-4">
        <div class="col-md-4">
          <strong>🍴 Cuisine Type:</strong> {{ dinner.cuisineType }}
        </div>
        <div class="col-md-4">
          <strong>👥 Max Guests:</strong> {{ dinner.maxGuestCount }}
        </div>
        <div class="col-md-4">
          <strong>📍 Location:</strong> {{ dinner.address }}
        </div>
      </div>

      <div class="row mb-4">
        <div class="col-md-6">
          <strong>⏰ Start Time:</strong> {{ dinner.startTime | date:'short' }}
        </div>
        <div class="col-md-6">
          <strong>⏰ End Time:</strong> {{ dinner.endTime | date:'short' }}
        </div>
      </div>

      <hr>

      <h3>Reviews ({{ reviews.length }})</h3>
      <div *ngFor="let review of reviews" class="card mb-3">
        <div class="card-body">
          <div class="d-flex justify-content-between">
            <strong>Rating: {{ review.rating }}/5 ⭐</strong>
            <small class="text-muted">{{ review.createdAt | date:'short' }}</small>
          </div>
          <p class="card-text mt-2">{{ review.comment }}</p>
        </div>
      </div>

      <button class="btn btn-secondary" (click)="toggleAddReview()">
        {{ showAddReview ? 'Cancel' : 'Add Review' }}
      </button>

      <div *ngIf="showAddReview" class="card mt-3">
        <div class="card-body">
          <form (ngSubmit)="submitReview()">
            <div class="mb-3">
              <label>Rating:</label>
              <select [(ngModel)]="newReview.rating" name="rating" class="form-select">
                <option value="">Select rating</option>
                <option value="1">1 - Poor</option>
                <option value="2">2 - Fair</option>
                <option value="3">3 - Good</option>
                <option value="4">4 - Very Good</option>
                <option value="5">5 - Excellent</option>
              </select>
            </div>
            <div class="mb-3">
              <label>Comment:</label>
              <textarea [(ngModel)]="newReview.comment" name="comment" class="form-control" rows="3"></textarea>
            </div>
            <button type="submit" class="btn btn-primary" [disabled]="!newReview.rating || !newReview.comment">
              Submit Review
            </button>
          </form>
        </div>
      </div>
    </div>

    <div class="col-lg-4">
      <div class="card sticky-top" style="top: 20px;">
        <div class="card-body">
          <h4 class="card-title">Book This Dinner</h4>
          <button 
            class="btn btn-primary btn-lg w-100" 
            (click)="goToReservation()"
            [disabled]="!isLoggedIn">
            Make a Reservation
          </button>
          <small class="text-muted" *ngIf="!isLoggedIn" class="d-block mt-2">
            ⚠️ Please <a routerLink="/login">login</a> to make a reservation
          </small>
        </div>
      </div>
    </div>
  </div>
</div>

<div *ngIf="!dinner" class="container mt-5">
  <div class="alert alert-info">Loading dinner details...</div>
</div>
```

### src/app/components/dinner-detail/dinner-detail.component.ts

```typescript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DinnerService } from '../../services/dinner.service';
import { ReviewService } from '../../services/review.service';
import { AuthService } from '../../services/auth.service';
import { Dinner } from '../../models/dinner';
import { Review, ReviewRequest } from '../../models/review';

@Component({
  selector: 'app-dinner-detail',
  templateUrl: './dinner-detail.component.html',
  styleUrls: ['./dinner-detail.component.scss']
})
export class DinnerDetailComponent implements OnInit {
  dinner: Dinner | null = null;
  reviews: Review[] = [];
  showAddReview = false;
  isLoggedIn = false;
  newReview: any = { rating: '', comment: '' };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dinnerService: DinnerService,
    private reviewService: ReviewService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
    });

    this.route.paramMap.subscribe(params => {
      const id = parseInt(params.get('id')!);
      this.loadDinner(id);
      this.loadReviews(id);
    });
  }

  loadDinner(id: number): void {
    this.dinnerService.getDinnerById(id).subscribe({
      next: (dinner) => {
        this.dinner = dinner;
      },
      error: (error) => {
        console.error('Error loading dinner:', error);
      }
    });
  }

  loadReviews(dinnerId: number): void {
    this.reviewService.getReviewsByDinnerId(dinnerId).subscribe({
      next: (reviews) => {
        this.reviews = reviews;
      },
      error: (error) => {
        console.error('Error loading reviews:', error);
      }
    });
  }

  toggleAddReview(): void {
    this.showAddReview = !this.showAddReview;
  }

  submitReview(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !this.dinner) return;

    const review: ReviewRequest = {
      dinnerId: this.dinner.id,
      userId: currentUser.id,
      rating: parseInt(this.newReview.rating),
      comment: this.newReview.comment
    };

    this.reviewService.createReview(review).subscribe({
      next: (newReview) => {
        this.reviews.push(newReview);
        this.newReview = { rating: '', comment: '' };
        this.showAddReview = false;
        alert('Review submitted successfully!');
      },
      error: (error) => {
        console.error('Error submitting review:', error);
        alert('Error submitting review');
      }
    });
  }

  goToReservation(): void {
    if (this.dinner) {
      this.router.navigate(['/reservations/create', this.dinner.id]);
    }
  }
}
```

### src/app/components/reservation-create/reservation-create.component.html

```html
<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-lg-6">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h4>Create Reservation</h4>
        </div>
        <div class="card-body">
          <form (ngSubmit)="submitReservation()" *ngIf="!reservationCreated">
            <div class="mb-3">
              <label class="form-label">Dinner:</label>
              <div class="form-control" style="background-color: #f5f5f5;">
                {{ dinnerName }}
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Date & Time:</label>
              <input 
                type="datetime-local" 
                [(ngModel)]="reservation.reservationDate" 
                name="reservationDate"
                class="form-control"
                required>
            </div>

            <div class="mb-3">
              <label class="form-label">Restaurant Name:</label>
              <input 
                type="text" 
                [(ngModel)]="reservation.restaurantName" 
                name="restaurantName"
                class="form-control"
                placeholder="Enter restaurant name"
                required>
            </div>

            <div class="d-grid gap-2">
              <button type="submit" class="btn btn-primary btn-lg" [disabled]="loading">
                <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
                {{ loading ? 'Creating...' : 'Create Reservation' }}
              </button>
              <a routerLink="/dinners" class="btn btn-secondary">Cancel</a>
            </div>

            <div *ngIf="error" class="alert alert-danger mt-3">{{ error }}</div>
          </form>

          <div *ngIf="reservationCreated" class="alert alert-success">
            <h5>✅ Reservation Created Successfully!</h5>
            <p>Reservation ID: {{ reservationCreated.reservationId }}</p>
            <a routerLink="/my-reservations" class="btn btn-primary mt-3">View My Reservations</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
```

### src/app/components/reservation-create/reservation-create.component.ts

```typescript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ReservationService } from '../../services/reservation.service';
import { DinnerService } from '../../services/dinner.service';
import { AuthService } from '../../services/auth.service';
import { Dinner } from '../../models/dinner';
import { CreateReservationRequest, ReservationResponse } from '../../models/reservation';

@Component({
  selector: 'app-reservation-create',
  templateUrl: './reservation-create.component.html',
  styleUrls: ['./reservation-create.component.scss']
})
export class ReservationCreateComponent implements OnInit {
  dinnerId: number | null = null;
  dinnerName = '';
  loading = false;
  error = '';
  reservationCreated: ReservationResponse | null = null;

  reservation: any = {
    dinnerId: null,
    guestId: null,
    reservationDate: '',
    restaurantName: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reservationService: ReservationService,
    private dinnerService: DinnerService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.reservation.guestId = currentUser.id;

    this.route.paramMap.subscribe(params => {
      this.dinnerId = parseInt(params.get('id')!);
      if (this.dinnerId) {
        this.reservation.dinnerId = this.dinnerId;
        this.loadDinner(this.dinnerId);
      }
    });
  }

  loadDinner(id: number): void {
    this.dinnerService.getDinnerById(id).subscribe({
      next: (dinner) => {
        this.dinnerName = dinner.name;
        this.reservation.restaurantName = dinner.address;
      },
      error: (error) => {
        console.error('Error loading dinner:', error);
      }
    });
  }

  submitReservation(): void {
    if (!this.reservation.reservationDate || !this.reservation.restaurantName) {
      this.error = 'Please fill in all fields';
      return;
    }

    this.loading = true;
    this.error = '';

    const request: CreateReservationRequest = {
      dinnerId: this.reservation.dinnerId,
      guestId: this.reservation.guestId,
      reservationDate: this.reservation.reservationDate,
      restaurantName: this.reservation.restaurantName
    };

    this.reservationService.createReservation(request).subscribe({
      next: (response) => {
        this.reservationCreated = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error creating reservation:', error);
        this.error = 'Error creating reservation. Please try again.';
        this.loading = false;
      }
    });
  }
}
```

### src/app/components/my-reservations/my-reservations.component.html

```html
<div class="container mt-5">
  <h1>My Reservations</h1>

  <div *ngIf="reservations.length > 0" class="row g-4">
    <div class="col-md-6" *ngFor="let reservation of reservations">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">Reservation #{{ reservation.id | slice:0:8 }}...</h5>
          <p class="card-text">
            <strong>Dinner ID:</strong> {{ reservation.dinnerId }}<br>
            <strong>Restaurant:</strong> {{ reservation.restaurantName }}<br>
            <strong>Date:</strong> {{ reservation.reservationDate | date:'medium' }}<br>
            <strong>Status:</strong> 
            <span class="badge bg-success" *ngIf="reservation.status === 'CONFIRMED'">{{ reservation.status }}</span>
            <span class="badge bg-warning" *ngIf="reservation.status === 'PENDING'">{{ reservation.status }}</span>
          </p>
          <button 
            class="btn btn-danger btn-sm" 
            (click)="cancelReservation(reservation.id)"
            [disabled]="canceling">
            Cancel Reservation
          </button>
        </div>
      </div>
    </div>
  </div>

  <div *ngIf="reservations.length === 0" class="alert alert-info mt-4">
    <p>You haven't made any reservations yet.</p>
    <a routerLink="/dinners" class="btn btn-primary">Browse Dinners</a>
  </div>
</div>
```

### src/app/components/my-reservations/my-reservations.component.ts

```typescript
import { Component, OnInit } from '@angular/core';
import { ReservationService } from '../../services/reservation.service';
import { AuthService } from '../../services/auth.service';
import { Reservation } from '../../models/reservation';

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrls: ['./my-reservations.component.scss']
})
export class MyReservationsComponent implements OnInit {
  reservations: Reservation[] = [];
  canceling = false;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadMyReservations();
  }

  loadMyReservations(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) return;

    this.reservationService.getMyReservations(currentUser.id).subscribe({
      next: (response) => {
        this.reservations = response;
      },
      error: (error) => {
        console.error('Error loading reservations:', error);
      }
    });
  }

  cancelReservation(id: string): void {
    if (!confirm('Are you sure you want to cancel this reservation?')) return;

    this.canceling = true;
    this.reservationService.cancelReservation(id).subscribe({
      next: () => {
        this.reservations = this.reservations.filter(r => r.id !== id);
        alert('Reservation canceled successfully');
        this.canceling = false;
      },
      error: (error) => {
        console.error('Error canceling reservation:', error);
        alert('Error canceling reservation');
        this.canceling = false;
      }
    });
  }
}
```

### src/app/components/login/login.component.html

```html
<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-lg-5">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h4>Login</h4>
        </div>
        <div class="card-body">
          <form (ngSubmit)="login()" *ngIf="!loginSuccess">
            <div class="mb-3">
              <label class="form-label">Username:</label>
              <input 
                type="text" 
                [(ngModel)]="credentials.username" 
                name="username"
                class="form-control"
                required>
            </div>

            <div class="mb-3">
              <label class="form-label">Password:</label>
              <input 
                type="password" 
                [(ngModel)]="credentials.password" 
                name="password"
                class="form-control"
                required>
            </div>

            <button type="submit" class="btn btn-primary w-100 mb-3" [disabled]="loading">
              <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
              {{ loading ? 'Logging in...' : 'Login' }}
            </button>

            <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
          </form>

          <div *ngIf="loginSuccess" class="alert alert-success">
            <h5>✅ Login Successful!</h5>
            <p>Redirecting...</p>
          </div>

          <p class="text-center mt-3">
            Don't have an account? <a href="#" (click)="toggleSignup()">Sign up here</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</div>
```

### src/app/components/login/login.component.ts

```typescript
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  credentials: LoginRequest = { username: '', password: '' };
  loading = false;
  error = '';
  loginSuccess = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    if (!this.credentials.username || !this.credentials.password) {
      this.error = 'Please fill in all fields';
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.loginSuccess = true;
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 1500);
      },
      error: (error) => {
        console.error('Login error:', error);
        this.error = 'Invalid credentials';
        this.loading = false;
      }
    });
  }

  toggleSignup(): void {
    // Implement signup logic here
  }
}
```

### src/app/components/profile/profile.component.html

```html
<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-lg-6">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h4>My Profile</h4>
        </div>
        <div class="card-body" *ngIf="user">
          <p><strong>Username:</strong> {{ user.username }}</p>
          <p><strong>Email:</strong> {{ user.email }}</p>
          <p><strong>Name:</strong> {{ user.firstName }} {{ user.lastName }}</p>
          <p><strong>Role:</strong> {{ user.role }}</p>
          <p><strong>Member Since:</strong> {{ user.createdAt | date:'long' }}</p>
        </div>
      </div>
    </div>
  </div>
</div>
```

### src/app/components/profile/profile.component.ts

```typescript
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  user: User | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
  }
}
```

---

## 📍 6. APP ROUTING & MODULE

### src/app/app-routing.module.ts

```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { DinnerListComponent } from './components/dinner-list/dinner-list.component';
import { DinnerDetailComponent } from './components/dinner-detail/dinner-detail.component';
import { ReservationCreateComponent } from './components/reservation-create/reservation-create.component';
import { MyReservationsComponent } from './components/my-reservations/my-reservations.component';
import { LoginComponent } from './components/login/login.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'dinners', component: DinnerListComponent },
  { path: 'dinners/:id', component: DinnerDetailComponent },
  { path: 'reservations/create/:id', component: ReservationCreateComponent, canActivate: [AuthGuard] },
  { path: 'my-reservations', component: MyReservationsComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

### src/app/app.module.ts

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';

// Components
import { NavbarComponent } from './components/navbar/navbar.component';
import { HomeComponent } from './components/home/home.component';
import { DinnerListComponent } from './components/dinner-list/dinner-list.component';
import { DinnerDetailComponent } from './components/dinner-detail/dinner-detail.component';
import { ReservationCreateComponent } from './components/reservation-create/reservation-create.component';
import { MyReservationsComponent } from './components/my-reservations/my-reservations.component';
import { LoginComponent } from './components/login/login.component';
import { ProfileComponent } from './components/profile/profile.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomeComponent,
    DinnerListComponent,
    DinnerDetailComponent,
    ReservationCreateComponent,
    MyReservationsComponent,
    LoginComponent,
    ProfileComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

### src/app/app.component.html

```html
<app-navbar></app-navbar>
<div class="main-content">
  <router-outlet></router-outlet>
</div>
<footer class="bg-light py-4 mt-5">
  <div class="container text-center text-muted">
    <p>&copy; 2026 Buber Dinner. All rights reserved.</p>
  </div>
</footer>
```

### src/app/app.component.scss

```scss
.main-content {
  min-height: calc(100vh - 200px);
}

footer {
  margin-top: auto;
}
```

---

## 🎨 7. GLOBAL STYLES

### src/styles.scss

```scss
@import 'bootstrap/dist/css/bootstrap.css';

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f8f9fa;
}

.card {
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  }
}

.btn {
  border-radius: 5px;
  font-weight: 500;

  &:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }
}

.navbar {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.container {
  max-width: 1200px;
}
```

---

## 🚀 RUN THE FRONTEND

```powershell
cd frontend
ng serve --open
```

Your app will open at **http://localhost:4200** 🎉

---


