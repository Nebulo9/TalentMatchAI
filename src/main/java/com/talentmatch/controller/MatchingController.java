package com.talentmatch.controller;

import com.talentmatch.dto.MatchingRequest;
import com.talentmatch.dto.MatchingResponse;
import com.talentmatch.model.MatchingResult;
import com.talentmatch.service.MatchingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService service;

    public MatchingController(MatchingService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public ResponseEntity<MatchingResponse> analyze(@Valid @RequestBody MatchingRequest req) {
        MatchingResult result = service.analyze(req.getCandidateId(), req.getJobOfferId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toResponse(result));
    }

    @GetMapping("/results")
    public List<MatchingResponse> findAll() {
        return service.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/results/{id}")
    public MatchingResponse findById(@PathVariable UUID id) {
        return toResponse(service.findById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public List<MatchingResponse> findByCandidate(@PathVariable UUID candidateId) {
        return service.findByCandidate(candidateId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/job/{jobOfferId}")
    public List<MatchingResponse> findByJob(@PathVariable UUID jobOfferId) {
        return service.findByJob(jobOfferId).stream().map(this::toResponse).toList();
    }

    private MatchingResponse toResponse(MatchingResult r) {
        MatchingResponse res = new MatchingResponse();
        res.setId(r.getId());
        res.setCandidateId(r.getCandidateId());
        res.setJobOfferId(r.getJobOfferId());
        res.setScore(r.getScore());
        res.setAnalysis(r.getAnalysis());
        res.setStatus(r.getStatus());
        res.setRequestedAt(r.getRequestedAt());
        res.setCompletedAt(r.getCompletedAt());
        res.setErrorMessage(r.getErrorMessage());
        return res;
    }
}
