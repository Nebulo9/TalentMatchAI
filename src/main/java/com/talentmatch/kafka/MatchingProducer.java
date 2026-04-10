package com.talentmatch.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@Component
public class MatchingProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.matching-requests}")
    private String topic;

    public MatchingProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(UUID matchingId, UUID candidateId, UUID jobOfferId) {
        try {
            Map<String, String> message = Map.of(
                    "matchingId", matchingId.toString(),
                    "candidateId", candidateId.toString(),
                    "jobOfferId", jobOfferId.toString()
            );
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, matchingId.toString(), json);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to serialize Kafka message", e);
        }
    }
}
