# Buber Dinner - Project Setup and Testing Guide

This guide provides instructions on how to set up, run, and test the Buber Dinner application, which consists of multiple microservices and a React frontend.

## Table of Contents

1. [System Requirements](#system-requirements)
2. [Project Structure](#project-structure)
3. [Setting Up the Environment](#setting-up-the-environment)
4. [Running the Services](#running-the-services)
5. [Testing the Application](#testing-the-application)
6. [Troubleshooting](#troubleshooting)

## System Requirements

- Java 17 or higher
- Maven 3.6 or higher
- Node.js 14 or higher
- npm 6 or higher
- Git

## Project Structure

The Buber Dinner application consists of the following components:

- **Discovery Service**: Eureka service registry for service discovery
- **User Service**: Handles user authentication, registration, and user management
- **Dinner Service**: Manages dinner events, including creation, updates, and queries
- **Frontend Service**: React-based user interface

## Setting Up the Environment

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/micro_buber_dinner.git
cd micro_buber_dinner
```

### 2. Build the Backend Services

```bash
mvn clean install
```

### 3. Set Up the Frontend

```bash
cd frontendService
npm install
```

## Running the Services

### 1. Start the Discovery Service

The Discovery Service (Eureka) must be started first, as other services will register with it.

```bash
cd discoveryService
mvn spring-boot:run
```

You can access the Eureka dashboard at: http://localhost:8761

### 2. Start the User Service

Open a new terminal window:

```bash
cd userService
mvn spring-boot:run
```

The User Service will run on port 8080.

### 3. Start the Dinner Service

Open a new terminal window:

```bash
cd dinnerService
mvn spring-boot:run
```

The Dinner Service will run on port 8081.

### 4. Start the Frontend

Open a new terminal window:

```bash
cd frontendService
npm start
```

The frontend will run on port 3000 and can be accessed at: http://localhost:3000

## Testing the Application

### 1. User Registration and Authentication

1. Open your browser and navigate to http://localhost:3000
2. Click on "Sign Up as Host" or "Sign Up as Guest" to create a new account
3. Fill in the required information and submit the form
4. Log in with your newly created account

### 2. Creating a Dinner (Host Only)

1. After logging in as a host, click on "Create Dinner" in the navigation bar
2. Fill in the dinner details:
   - Name
   - Description
   - Price
   - Start and end times
   - Address
   - Cuisine type
   - Maximum number of guests
3. Click "Create Dinner" to submit the form

### 3. Browsing Dinners

1. Click on "Dinners" in the navigation bar to see all available dinners
2. Use the search bar to find specific dinners
3. Use the filter dropdown to filter dinners by status

### 4. Viewing Dinner Details

1. Click on a dinner card to view its details
2. If you are the host of the dinner, you will see options to edit or delete it

### 5. Editing a Dinner (Host Only)

1. From the dinner details page, click "Edit Dinner"
2. Modify the dinner details as needed
3. Click "Save Changes" to update the dinner

### 6. User Profile

1. Click on "Profile" in the navigation bar to view your profile
2. If you are a host, you will see a list of dinners you have created

## Troubleshooting

### Common Issues

#### Services Not Registering with Eureka

- Ensure the Discovery Service is running before starting other services
- Check that the Eureka client configuration is correct in each service's `application.properties`

#### Authentication Issues

- Clear your browser cookies and try logging in again
- Ensure the User Service is running correctly

#### API Communication Errors

- Check that all services are running
- Verify that the service URLs are correctly configured in `application.properties`

#### Frontend Not Connecting to Backend

- Ensure the proxy configuration in `package.json` is correct
- Check for CORS issues in the browser console

### Logs

- Backend service logs can be found in the terminal windows where the services are running
- Frontend logs can be found in the browser console (F12)

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [React Documentation](https://reactjs.org/docs/getting-started.html)
- [Eureka Documentation](https://cloud.spring.io/spring-cloud-netflix/reference/html/)