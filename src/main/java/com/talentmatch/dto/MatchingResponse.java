package com.talentmatch.dto;

import com.talentmatch.model.MatchingStatus;

import java.time.Instant;
import java.util.UUID;

public class MatchingResponse {

    private UUID id;
    private UUID candidateId;
    private UUID jobOfferId;
    private Integer score;
    private String analysis;
    private MatchingStatus status;
    private Instant requestedAt;
    private Instant completedAt;
    private String errorMessage;

    public UUID getId() { return id; }
    public UUID getCandidateId() { return candidateId; }
    public UUID getJobOfferId() { return jobOfferId; }
    public Integer getScore() { return score; }
    public String getAnalysis() { return analysis; }
    public MatchingStatus getStatus() { return status; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getErrorMessage() { return errorMessage; }

    public void setId(UUID id) { this.id = id; }
    public void setCandidateId(UUID candidateId) { this.candidateId = candidateId; }
    public void setJobOfferId(UUID jobOfferId) { this.jobOfferId = jobOfferId; }
    public void setScore(Integer score) { this.score = score; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public void setStatus(MatchingStatus status) { this.status = status; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
