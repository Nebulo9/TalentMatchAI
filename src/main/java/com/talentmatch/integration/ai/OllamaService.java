package com.talentmatch.integration.ai;

import com.talentmatch.exception.ExternalServiceException;
import com.talentmatch.model.Candidate;
import com.talentmatch.model.JobOffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    public OllamaService(RestTemplate restTemplate, PromptBuilder promptBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> analyze(Candidate candidate, JobOffer jobOffer) {
        String prompt = promptBuilder.buildMatchingPrompt(candidate, jobOffer);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    baseUrl + "/api/generate",
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody),
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("response")) {
                throw new ExternalServiceException("OLLAMA", "Empty or invalid response from Ollama");
            }

            String jsonStr = (String) body.get("response");
            return parseResponse(jsonStr);

        } catch (RestClientException e) {
            throw new ExternalServiceException("OLLAMA", "Ollama unreachable: " + e.getMessage());
        }
    }

    private Map<String, Object> parseResponse(String json) {
        try {
            String cleaned = json.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("```[a-zA-Z]*\\n?", "").replaceAll("```", "").trim();
            }
            int start = cleaned.indexOf('{');
            int end = cleaned.lastIndexOf('}');
            if (start >= 0 && end > start) {
                cleaned = cleaned.substring(start, end + 1);
            }

            JsonNode node = objectMapper.readTree(cleaned);
            int score = node.has("score") ? node.get("score").asInt(0) : 0;
            String analysis = node.has("analysis") ? node.get("analysis").asText("") : cleaned;
            return Map.of("score", Math.min(100, Math.max(0, score)), "analysis", analysis);

        } catch (Exception e) {
            throw new ExternalServiceException("OLLAMA", "Failed to parse Ollama response: " + json);
        }
    }
}
