package com.talentmatch.service;

import com.talentmatch.model.JobOffer;
import com.talentmatch.repository.JobOfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JobOfferService {

    private final JobOfferRepository repository;

    public JobOfferService(JobOfferRepository repository) {
        this.repository = repository;
    }

    public JobOffer create(JobOffer jobOffer) {
        return repository.save(jobOffer);
    }

    public List<JobOffer> findAll() {
        return repository.findAll();
    }

    public JobOffer findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobOffer not found"));
    }

    public JobOffer update(UUID id, JobOffer updated) {

        JobOffer existing = findById(id);

        existing.setTitle(updated.getTitle());
        existing.setCompany(updated.getCompany());
        existing.setRequiredSkills(updated.getRequiredSkills());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setSalaryRange(updated.getSalaryRange());

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
