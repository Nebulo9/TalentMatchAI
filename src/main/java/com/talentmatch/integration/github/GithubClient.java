package com.talentmatch.integration.github;

import com.talentmatch.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class GithubClient {

    private final RestTemplate restTemplate;

    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.api.user-agent}")
    private String userAgent;

    public GithubClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getRepos(String username) {
        String url = baseUrl + "/users/" + username + "/repos?per_page=100";
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(buildHeaders()),
                    new ParameterizedTypeReference<>() {}
            );
            return Objects.requireNonNullElse(response.getBody(), List.of());
        } catch (RestClientException e) {
            throw new ExternalServiceException("GITHUB", "Failed to fetch repos for " + username + ": " + e.getMessage());
        }
    }

    public Map<String, Object> getUser(String username) {
        String url = baseUrl + "/users/" + username;
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(buildHeaders()),
                    new ParameterizedTypeReference<>() {}
            );
            return Objects.requireNonNullElse(response.getBody(), Map.of());
        } catch (RestClientException e) {
            throw new ExternalServiceException("GITHUB", "Failed to fetch user profile for " + username + ": " + e.getMessage());
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");
        headers.set("User-Agent", userAgent);
        return headers;
    }
}
