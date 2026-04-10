package com.talentmatch.controller;

import com.talentmatch.dto.CandidateRequest;
import com.talentmatch.dto.CandidateResponse;
import com.talentmatch.model.Candidate;
import com.talentmatch.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CandidateResponse> create(@Valid @RequestBody CandidateRequest req) {
        Candidate saved = service.create(toEntity(req));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    public List<CandidateResponse> findAll() {
        return service.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public CandidateResponse findById(@PathVariable UUID id) {
        return toResponse(service.findById(id));
    }

    @PutMapping("/{id}")
    public CandidateResponse update(@PathVariable UUID id, @Valid @RequestBody CandidateRequest req) {
        return toResponse(service.update(id, toEntity(req)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PostMapping("/import")
    public ResponseEntity<CandidateResponse> importFromGithub(@RequestParam String githubUsername) {
        Candidate candidate = service.importFromGithub(githubUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(candidate));
    }

    private Candidate toEntity(CandidateRequest req) {
        Candidate c = new Candidate();
        c.setFirstName(req.getFirstName());
        c.setLastName(req.getLastName());
        c.setEmail(req.getEmail());
        c.setGithubUsername(req.getGithubUsername());
        c.setSkills(req.getSkills());
        c.setYearsOfExperience(req.getYearsOfExperience());
        c.setBio(req.getBio());
        return c;
    }

    private CandidateResponse toResponse(Candidate c) {
        CandidateResponse res = new CandidateResponse();
        res.setId(c.getId());
        res.setFirstName(c.getFirstName());
        res.setLastName(c.getLastName());
        res.setEmail(c.getEmail());
        res.setGithubUsername(c.getGithubUsername());
        res.setSkills(c.getSkills());
        res.setYearsOfExperience(c.getYearsOfExperience());
        res.setBio(c.getBio());
        res.setCreatedAt(c.getCreatedAt());
        return res;
    }
}
