package com.talentmatch.service;

import com.talentmatch.model.*;
import com.talentmatch.repository.MatchingResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MatchingService {

    private final MatchingResultRepository repository;
    private final CandidateService candidateService;
    private final JobOfferService jobOfferService;

    public MatchingService(MatchingResultRepository repository,
                           CandidateService candidateService,
                           JobOfferService jobOfferService) {
        this.repository = repository;
        this.candidateService = candidateService;
        this.jobOfferService = jobOfferService;
    }

    // CREATE MATCH REQUEST
    public MatchingResult analyze(UUID candidateId, UUID jobOfferId) {

        candidateService.findById(candidateId);
        jobOfferService.findById(jobOfferId);

        MatchingResult result = new MatchingResult();
        result.setCandidateId(candidateId);
        result.setJobOfferId(jobOfferId);
        result.setStatus(MatchingStatus.PENDING);

        return repository.save(result);
    }

    public List<MatchingResult> findAll() {
        return repository.findAll();
    }

    public MatchingResult findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matching not found"));
    }

    public List<MatchingResult> findByCandidate(UUID candidateId) {
        return repository.findByCandidateId(candidateId);
    }

    public List<MatchingResult> findByJob(UUID jobOfferId) {
        return repository.findByJobOfferId(jobOfferId);
    }
}
