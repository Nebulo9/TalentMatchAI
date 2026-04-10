package com.talentmatch.actuator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("ollama")
public class OllamaHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;

    @Value("${ollama.base-url}")
    private String baseUrl;

    public OllamaHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/api/tags", String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up().withDetail("ollama", "reachable").build();
            }
            return Health.down().withDetail("status", response.getStatusCode().value()).build();
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
