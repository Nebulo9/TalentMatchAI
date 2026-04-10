package com.talentmatch.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class MatchingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID candidateId;
    private UUID jobOfferId;

    private Integer score;

    @Column(length = 3000)
    private String analysis;

    @Enumerated(EnumType.STRING)
    private MatchingStatus status;

    private Instant requestedAt;
    private Instant completedAt;

    @Column(length = 2000)
    private String errorMessage;

    @PrePersist
    public void prePersist() {
        this.requestedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getCandidateId() { return candidateId; }
    public UUID getJobOfferId() { return jobOfferId; }
    public Integer getScore() { return score; }
    public String getAnalysis() { return analysis; }
    public MatchingStatus getStatus() { return status; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getErrorMessage() { return errorMessage; }

    public void setCandidateId(UUID candidateId) { this.candidateId = candidateId; }
    public void setJobOfferId(UUID jobOfferId) { this.jobOfferId = jobOfferId; }
    public void setScore(Integer score) { this.score = score; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public void setStatus(MatchingStatus status) { this.status = status; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
