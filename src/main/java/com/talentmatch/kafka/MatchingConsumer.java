package com.talentmatch.kafka;

import com.talentmatch.exception.ExternalServiceException;
import com.talentmatch.integration.ai.OllamaService;
import com.talentmatch.model.Candidate;
import com.talentmatch.model.JobOffer;
import com.talentmatch.model.MatchingStatus;
import com.talentmatch.service.CandidateService;
import com.talentmatch.service.JobOfferService;
import com.talentmatch.service.MatchingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@Component
public class MatchingConsumer {

    private final MatchingService matchingService;
    private final CandidateService candidateService;
    private final JobOfferService jobOfferService;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;

    public MatchingConsumer(MatchingService matchingService,
                            CandidateService candidateService,
                            JobOfferService jobOfferService,
                            OllamaService ollamaService,
                            ObjectMapper objectMapper) {
        this.matchingService = matchingService;
        this.candidateService = candidateService;
        this.jobOfferService = jobOfferService;
        this.ollamaService = ollamaService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.matching-requests}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        UUID matchingId = null;
        try {
            JsonNode node = objectMapper.readTree(message);
            matchingId = UUID.fromString(node.get("matchingId").asText());
            UUID candidateId = UUID.fromString(node.get("candidateId").asText());
            UUID jobOfferId = UUID.fromString(node.get("jobOfferId").asText());

            matchingService.setProcessing(matchingId);

            Candidate candidate = candidateService.findById(candidateId);
            JobOffer jobOffer = jobOfferService.findById(jobOfferId);

            Map<String, Object> result = ollamaService.analyze(candidate, jobOffer);
            Integer score = (Integer) result.get("score");
            String analysis = (String) result.get("analysis");

            matchingService.updateResult(matchingId, score, analysis, MatchingStatus.COMPLETED, null);

        } catch (ExternalServiceException e) {
            if (matchingId != null) {
                matchingService.updateResult(matchingId, null, null, MatchingStatus.FAILED, e.getMessage());
            }
        } catch (Exception e) {
            if (matchingId != null) {
                matchingService.updateResult(matchingId, null, null, MatchingStatus.FAILED, "Processing error: " + e.getMessage());
            }
        }
    }
}
