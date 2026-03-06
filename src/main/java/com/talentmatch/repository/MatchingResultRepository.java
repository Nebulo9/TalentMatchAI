package com.talentmatch.repository;

import com.talentmatch.model.MatchingResult;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchingResultRepository extends JpaRepository<MatchingResult, UUID> {
    List<MatchingResult> findByCandidateId(UUID candidateId);
    List<MatchingResult> findByJobOfferId(UUID jobOfferId);
}