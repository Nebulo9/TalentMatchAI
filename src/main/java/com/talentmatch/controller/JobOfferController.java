package com.talentmatch.controller;

import com.talentmatch.dto.JobOfferRequest;
import com.talentmatch.dto.JobOfferResponse;
import com.talentmatch.model.JobOffer;
import com.talentmatch.service.JobOfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-offers")
public class JobOfferController {

    private final JobOfferService service;

    public JobOfferController(JobOfferService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<JobOfferResponse> create(@Valid @RequestBody JobOfferRequest req) {
        JobOffer saved = service.create(toEntity(req));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    public List<JobOfferResponse> findAll() {
        return service.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public JobOfferResponse findById(@PathVariable UUID id) {
        return toResponse(service.findById(id));
    }

    @PutMapping("/{id}")
    public JobOfferResponse update(@PathVariable UUID id, @Valid @RequestBody JobOfferRequest req) {
        return toResponse(service.update(id, toEntity(req)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    private JobOffer toEntity(JobOfferRequest req) {
        JobOffer j = new JobOffer();
        j.setTitle(req.getTitle());
        j.setCompany(req.getCompany());
        j.setRequiredSkills(req.getRequiredSkills());
        j.setDescription(req.getDescription());
        j.setLocation(req.getLocation());
        j.setSalaryRange(req.getSalaryRange());
        return j;
    }

    private JobOfferResponse toResponse(JobOffer j) {
        JobOfferResponse res = new JobOfferResponse();
        res.setId(j.getId());
        res.setTitle(j.getTitle());
        res.setCompany(j.getCompany());
        res.setRequiredSkills(j.getRequiredSkills());
        res.setDescription(j.getDescription());
        res.setLocation(j.getLocation());
        res.setSalaryRange(j.getSalaryRange());
        res.setPostedAt(j.getPostedAt());
        return res;
    }
}
