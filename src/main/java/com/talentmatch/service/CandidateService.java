package com.talentmatch.service;

import com.talentmatch.model.Candidate;
import com.talentmatch.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CandidateService {

    private final CandidateRepository repository;

    public CandidateService(CandidateRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public Candidate create(Candidate candidate) {

        repository.findByEmail(candidate.getEmail())
                .ifPresent(c -> {
                    throw new RuntimeException("Email already exists");
                });

        return repository.save(candidate);
    }

    // READ ALL
    public List<Candidate> findAll() {
        return repository.findAll();
    }

    // READ BY ID
    public Candidate findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
    }

    // UPDATE
    public Candidate update(UUID id, Candidate updated) {

        Candidate existing = findById(id);

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setGithubUsername(updated.getGithubUsername());
        existing.setSkills(updated.getSkills());
        existing.setYearsOfExperience(updated.getYearsOfExperience());
        existing.setBio(updated.getBio());

        return repository.save(existing);
    }

    // DELETE
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}

