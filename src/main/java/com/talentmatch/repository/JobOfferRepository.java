package com.talentmatch.repository;

import com.talentmatch.model.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobOfferRepository extends JpaRepository<JobOffer, UUID> {
}
