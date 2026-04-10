package com.talentmatch.service;

import com.talentmatch.exception.NotFoundException;
import com.talentmatch.kafka.MatchingProducer;
import com.talentmatch.model.MatchingResult;
import com.talentmatch.model.MatchingStatus;
import com.talentmatch.repository.MatchingResultRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MatchingService {

    private final MatchingResultRepository repository;
    private final CandidateService candidateService;
    private final JobOfferService jobOfferService;
    private final MatchingProducer producer;

    public MatchingService(MatchingResultRepository repository,
                           CandidateService candidateService,
                           JobOfferService jobOfferService,
                           MatchingProducer producer) {
        this.repository = repository;
        this.candidateService = candidateService;
        this.jobOfferService = jobOfferService;
        this.producer = producer;
    }

    public MatchingResult analyze(UUID candidateId, UUID jobOfferId) {
        candidateService.findById(candidateId);
        jobOfferService.findById(jobOfferId);

        MatchingResult result = new MatchingResult();
        result.setCandidateId(candidateId);
        result.setJobOfferId(jobOfferId);
        result.setStatus(MatchingStatus.PENDING);

        MatchingResult saved = repository.save(result);
        producer.publish(saved.getId(), candidateId, jobOfferId);
        return saved;
    }

    public void setProcessing(UUID id) {
        MatchingResult result = findById(id);
        result.setStatus(MatchingStatus.PROCESSING);
        repository.save(result);
    }

    public void updateResult(UUID id, Integer score, String analysis, MatchingStatus status, String errorMessage) {
        MatchingResult result = findById(id);
        result.setScore(score);
        result.setAnalysis(analysis);
        result.setStatus(status);
        result.setErrorMessage(errorMessage);
        if (status == MatchingStatus.COMPLETED || status == MatchingStatus.FAILED) {
            result.setCompletedAt(Instant.now());
        }
        repository.save(result);
    }

    public List<MatchingResult> findAll() {
        return repository.findAll();
    }

    public MatchingResult findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("MatchingResult not found: " + id));
    }

    public List<MatchingResult> findByCandidate(UUID candidateId) {
        return repository.findByCandidateId(candidateId);
    }

    public List<MatchingResult> findByJob(UUID jobOfferId) {
        return repository.findByJobOfferId(jobOfferId);
    }

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("total", repository.count());
        for (MatchingStatus status : MatchingStatus.values()) {
            stats.put(status.name().toLowerCase(), repository.countByStatus(status));
        }
        return stats;
    }
}
