# Buber Dinner - Project Summary

## Overview

Buber Dinner is a microservices-based application that connects hosts who want to offer dining experiences with guests who want to enjoy them. The platform allows hosts to create dinner events with details such as location, time, cuisine type, and price, while guests can browse and book these events.

## Architecture

The application follows a microservices architecture with the following components:

### Backend Services

1. **Discovery Service**
   - Implemented using Netflix Eureka
   - Provides service registration and discovery
   - Enables dynamic communication between services

2. **User Service**
   - Handles user authentication and authorization
   - Manages user profiles and roles (HOST/GUEST)
   - Provides endpoints for user registration, login, and profile management
   - Secures endpoints using JWT-based authentication

3. **Dinner Service**
   - Manages dinner events (create, read, update, delete)
   - Validates host IDs through the User Service
   - Handles dinner-related business logic
   - Provides endpoints for dinner management and queries

### Frontend

- Built with React and React Bootstrap
- Responsive design for desktop and mobile devices
- Implements JWT-based authentication
- Features:
  - User registration and login
  - Dinner browsing with search and filtering
  - Dinner creation and management for hosts
  - User profiles
  - Role-based access control

## Communication Between Services

- **Service Discovery**: Services register with Eureka and discover each other dynamically
- **REST APIs**: Services communicate via RESTful HTTP endpoints
- **Client-Side Load Balancing**: Using Spring Cloud LoadBalancer
- **Authentication**: JWT tokens passed between services for secure communication

## Data Flow

1. **User Registration/Login**:
   - User submits credentials to User Service
   - User Service authenticates and returns JWT token
   - Frontend stores token for subsequent requests

2. **Dinner Creation**:
   - Host submits dinner details to Dinner Service
   - Dinner Service validates host ID with User Service
   - Dinner Service creates dinner and returns details

3. **Dinner Browsing**:
   - Frontend requests dinners from Dinner Service
   - Dinner Service returns list of available dinners
   - Frontend displays dinners with filtering and search options

## Technologies Used

### Backend
- Java 17
- Spring Boot 3.2.3
- Spring Cloud 2023.0.0
- Spring Security with JWT
- Spring Data JPA
- H2 Database (for development)
- Maven

### Frontend
- React 18
- React Router 6
- React Bootstrap
- Axios for API communication
- Context API for state management

## Features Implemented

### User Management
- User registration (Host/Guest)
- User authentication
- User profiles
- Role-based access control

### Dinner Management
- Dinner creation
- Dinner editing
- Dinner deletion
- Dinner browsing with search and filtering
- Dinner details view

### Service Discovery
- Eureka server for service registration
- Dynamic service discovery
- Client-side load balancing

## Future Enhancements

1. **Menu Service**
   - Implement a dedicated service for menu management
   - Allow hosts to create and manage menus for their dinners
   - Enable guests to view detailed menu information

2. **Booking Service**
   - Implement a service for managing dinner bookings
   - Allow guests to book seats at dinners
   - Provide booking management for hosts

3. **Payment Integration**
   - Integrate with payment gateways
   - Enable secure payment processing
   - Implement payment status tracking

4. **Review System**
   - Allow guests to leave reviews for dinners they attended
   - Implement host ratings
   - Display aggregate ratings and reviews

5. **API Gateway**
   - Implement an API Gateway for centralized routing
   - Add rate limiting and circuit breakers
   - Enhance security with centralized authentication

6. **Messaging System**
   - Implement asynchronous communication between services
   - Use message queues for event-driven architecture
   - Improve system resilience and scalability

## Conclusion

The Buber Dinner application demonstrates a modern microservices architecture with a responsive frontend. The implementation showcases service discovery, inter-service communication, and a clean separation of concerns. The project provides a solid foundation for future enhancements and scaling.