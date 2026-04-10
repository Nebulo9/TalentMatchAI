package com.talentmatch.service;

import com.talentmatch.exception.NotFoundException;
import com.talentmatch.exception.ValidationException;
import com.talentmatch.integration.github.GithubService;
import com.talentmatch.model.Candidate;
import com.talentmatch.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CandidateService {

    private final CandidateRepository repository;
    private final GithubService githubService;

    public CandidateService(CandidateRepository repository, GithubService githubService) {
        this.repository = repository;
        this.githubService = githubService;
    }

    public Candidate create(Candidate candidate) {
        repository.findByEmail(candidate.getEmail())
                .ifPresent(c -> {
                    throw new ValidationException("Email already exists: " + candidate.getEmail());
                });
        return repository.save(candidate);
    }

    public List<Candidate> findAll() {
        return repository.findAll();
    }

    public Candidate findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Candidate not found: " + id));
    }

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

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Candidate importFromGithub(String githubUsername) {
        Candidate candidate = githubService.buildCandidateFromGithub(githubUsername);
        return create(candidate);
    }
}
