# Microservices Communication in Buber Dinner


This document explains how the microservices in the Buber Dinner application communicate with each other.

## Service Communication

### User Service and Dinner Service

The User Service and Dinner Service are connected via REST API calls. The Dinner Service uses the User Service to validate host IDs before creating or updating dinners.

#### User Service Endpoints

The User Service provides the following endpoints for the Dinner Service:

- `GET /api/users/{id}/exists` - Checks if a user with the given ID exists
- `GET /api/users/{id}/isHost` - Checks if a user with the given ID exists and has the role of HOST
- `GET /api/users/{id}` - Returns the user DTO for the given ID

#### Dinner Service Client

The Dinner Service includes a `UserServiceClient` class that uses RestTemplate to make HTTP requests to the User Service endpoints. This client is used in the `DinnerApplicationServiceImpl` to validate host IDs before creating or updating dinners.

### Menu Service (Future Implementation)

The Menu Service is not yet implemented, but the Dinner Service includes a placeholder `MenuServiceClient` class that will be used to validate menu IDs when the Menu Service is available.

## Configuration

The service URLs are configured in the `application.properties` files:

```properties
# In dinnerService/src/main/resources/application.properties
service.user.url=http://localhost:8080
service.menu.url=http://localhost:8082
```

## How to Use

When creating or updating a dinner, the Dinner Service will automatically validate that:

1. The host ID corresponds to an existing user with the HOST role
2. The menu ID corresponds to an existing menu (when the Menu Service is implemented)

If either validation fails, the Dinner Service will throw an IllegalArgumentException with an appropriate error message.

## Service Discovery with Eureka

The Buber Dinner application now uses Eureka for service discovery. This allows services to find and communicate with each other dynamically without hardcoded URLs.

### Eureka Server

The Eureka Server is a standalone service that maintains a registry of all available service instances. It is the central component of the service discovery mechanism.

#### Configuration

The Eureka Server is configured in `discoveryService/src/main/resources/application.properties`:

```properties
spring.application.name=discoveryService
server.port=8761

# Eureka Server Configuration
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.server.wait-time-in-ms-when-sync-empty=0
eureka.instance.hostname=localhost
```

### Eureka Clients

Both the User Service and Dinner Service are configured as Eureka clients, which register with the Eureka Server and discover other services through it.

#### User Service Configuration

The User Service is configured in `userService/src/main/resources/application.properties`:

```properties
# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true
```

#### Dinner Service Configuration

The Dinner Service is configured in `dinnerService/src/main/resources/application.properties`:

```properties
# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true

# Service URLs (for direct communication, will be replaced by service discovery)
service.user.url=http://userService
service.menu.url=http://menuService
```

### How to Use

1. Start the Eureka Server first: `cd discoveryService && mvn spring-boot:run`
2. Start the User Service: `cd userService && mvn spring-boot:run`
3. Start the Dinner Service: `cd dinnerService && mvn spring-boot:run`
4. Access the Eureka Dashboard at http://localhost:8761 to see registered services

### Benefits

- **Dynamic Service Discovery**: Services can find each other without hardcoded URLs
- **Load Balancing**: Multiple instances of the same service can be load balanced automatically
- **Resilience**: If a service instance goes down, clients can automatically switch to another instance
- **Scalability**: New service instances can be added dynamically without configuration changes

## Future Improvements

1. Implement the Menu Service and complete the `MenuServiceClient` implementation
2. Add circuit breakers to handle service unavailability gracefully
3. Add authentication and authorization for inter-service communication
4. Implement an API Gateway for centralized routing and security
