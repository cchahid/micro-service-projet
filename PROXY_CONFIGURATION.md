# Proxy Configuration for Buber Dinner Frontend

## Overview

This document explains the proxy configuration for the Buber Dinner frontend application, which allows it to communicate with the backend microservices.

## Issue Addressed

The frontend was experiencing the following error when trying to connect to the backend services:

```
Proxy error: Could not proxy request /api/auth/signUp/Guest from localhost:3000 to http://localhost:8080 (ECONNREFUSED).
```

This error occurs when the frontend, running on port 3000, cannot establish a connection to the User Service running on port 8080.

## Solution Implemented

We've implemented a robust proxy configuration with fallback mechanisms and detailed error handling to address this issue:

### 1. Advanced Proxy Configuration

Instead of using the simple `proxy` field in `package.json`, we've created a more advanced proxy configuration using `http-proxy-middleware` in a dedicated `setupProxy.js` file.

### 2. Fallback Mechanism

The proxy now tries multiple service URLs in order:

1. `http://localhost:8080` - Direct connection to the User Service
2. `http://userService:8080` - Service name with port
3. `http://userService` - Service name only (relies on Eureka service discovery)

This ensures that the frontend can connect to the User Service even if one of the connection methods fails.

### 3. Detailed Error Handling

The proxy now provides detailed error messages and logs, making it easier to diagnose and fix connection issues:

- Console logs for all proxy requests, responses, and errors
- Detailed error page with troubleshooting steps
- Link to the Proxy Test page for further diagnostics

### 4. Proxy Test Tool

We've added a dedicated Proxy Test page at `/proxy-test` that allows you to:

- Test the connection to the User Service
- See detailed error messages
- Get troubleshooting suggestions
- Retry the connection test

## How to Use

### Running the Services

Make sure to start the services in the correct order:

1. Discovery Service (Eureka) on port 8761
2. User Service on port 8080
3. Dinner Service on port 8081
4. Frontend Service on port 3000

### Testing the Connection

1. Navigate to http://localhost:3000/proxy-test
2. The page will automatically test the connection to the User Service
3. If there are any issues, follow the troubleshooting steps provided
4. Use the "Retry Test" button to test again after making changes

### Troubleshooting

If you encounter the ECONNREFUSED error:

1. Make sure the User Service is running on port 8080
2. Check that the Discovery Service (Eureka) is running on port 8761
3. Verify that the User Service is registered with Eureka
4. Check for any firewall or network issues that might be blocking the connection
5. Check the browser console (F12) for detailed proxy logs

## Technical Details

### setupProxy.js

The `setupProxy.js` file in the `src` directory configures the proxy middleware:

```javascript
const { createProxyMiddleware } = require('http-proxy-middleware');

// List of potential service URLs to try in order
const serviceUrls = [
  'http://localhost:8080',  // Direct connection to user service
  'http://userService:8080', // Service name with port
  'http://userService'       // Service name only (relies on Eureka)
];

module.exports = function(app) {
  // Create a proxy middleware with fallback mechanism
  const proxyMiddleware = createProxyMiddleware({
    target: serviceUrls[0], // Start with the first URL
    changeOrigin: true,
    pathRewrite: {
      '^/api': '/api'
    },
    logLevel: 'debug',
    router: async (req) => {
      // Try each URL in order until one works
      for (const url of serviceUrls) {
        try {
          console.log(`[Proxy] Trying to connect to ${url}...`);
          return url;
        } catch (error) {
          console.error(`[Proxy] Failed to connect to ${url}: ${error.message}`);
        }
      }
      return serviceUrls[0];
    },
    // ... error handling and other configuration
  });

  // Apply the proxy middleware to all /api requests
  app.use('/api', proxyMiddleware);
};
```

### package.json

The `package.json` file includes the necessary dependencies:

```json
{
  "dependencies": {
    "axios": "^1.3.4",
    "bootstrap": "^5.2.3",
    "http-proxy-middleware": "^2.0.6",
    "react": "^18.2.0",
    "react-bootstrap": "^2.7.2"
  }
}
```

## Conclusion

This proxy configuration provides a robust solution for connecting the frontend to the backend microservices, with fallback mechanisms and detailed error handling to make it easier to diagnose and fix connection issues.
