package com.buberdinner.dinnerservice.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class MenuServiceClient {

    private final RestTemplate restTemplate;
    private final String menuServiceBaseUrl;
    private final DiscoveryClient discoveryClient;

    public MenuServiceClient(RestTemplate restTemplate, 
                            @Value("${service.menu.url:http://menuService}") String menuServiceBaseUrl,
                            DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.menuServiceBaseUrl = menuServiceBaseUrl;
        this.discoveryClient = discoveryClient;
    }

    /**
     * Checks if a menu with the given ID exists.
     *
     * @param menuId the menu ID to check
     * @return true if the menu exists, false otherwise
     */
    public boolean menuExists(Long menuId) {
        // TODO: Implement this method when the menu service is available
        // For now, return true to allow testing of the dinner service
        return true;

        // Example implementation:
        /*
        try {
            String url = menuServiceBaseUrl + "/api/menus/" + menuId + "/exists";
            Boolean exists = restTemplate.getForObject(url, Boolean.class);
            return exists != null && exists;
        } catch (RestClientException e) {
            // Log the error
            System.err.println("Error checking if menu exists: " + e.getMessage());
            return false;
        }
        */
    }
}
