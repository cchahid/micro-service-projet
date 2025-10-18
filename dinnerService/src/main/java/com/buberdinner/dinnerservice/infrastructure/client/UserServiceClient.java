package com.buberdinner.dinnerservice.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;
    private final DiscoveryClient discoveryClient;

    public UserServiceClient(RestTemplate restTemplate, 
                            @Value("${service.user.url:http://userService}") String userServiceBaseUrl,
                            DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceBaseUrl;
        this.discoveryClient = discoveryClient;
    }

    /**
     * Checks if a user with the given ID exists.
     *
     * @param userId the user ID to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(Long userId) {
        try {
            String url = userServiceBaseUrl + "/api/users/" + userId + "/exists";
            Boolean exists = restTemplate.getForObject(url, Boolean.class);
            return exists != null && exists;
        } catch (RestClientException e) {
            // Log the error
            System.err.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a user with the given ID is a host.
     *
     * @param userId the user ID to check
     * @return true if the user is a host, false otherwise
     */
    public boolean isUserHost(Long userId) {
        try {
            String url = userServiceBaseUrl + "/api/users/" + userId + "/isHost";
            Boolean isHost = restTemplate.getForObject(url, Boolean.class);
            return isHost != null && isHost;
        } catch (RestClientException e) {
            // Log the error
            System.err.println("Error checking if user is host: " + e.getMessage());
            return false;
        }
    }
}
