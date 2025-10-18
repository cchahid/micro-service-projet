package com.buberdinner.NotificationService.infrastructure.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ADD THIS IMPORT
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity; // ADD THIS IMPORT
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j // ADD THIS ANNOTATION
public class ReservationClient {

    private final RestTemplate restTemplate;

    public Map<String, String> getGuestInfo(String dinnerId) {
        String url = "http://reservation-service/api/reservations/guest-info/{dinnerId}";
        try {
            log.debug("Fetching guest info for dinnerId: {} from {}", dinnerId, url);
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, String>>() {},
                    dinnerId
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Guest info not found for dinnerId: {}", dinnerId);
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("Error fetching guest info for dinnerId {}: {}", dinnerId, e.getMessage(), e);
            return Collections.emptyMap();
        }
    }
}