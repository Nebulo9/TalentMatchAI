package com.talentmatch.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class MatchingRequest {

    @NotNull
    private UUID candidateId;

    @NotNull
    private UUID jobOfferId;

    public UUID getCandidateId() { return candidateId; }
    public UUID getJobOfferId() { return jobOfferId; }

    public void setCandidateId(UUID candidateId) { this.candidateId = candidateId; }
    public void setJobOfferId(UUID jobOfferId) { this.jobOfferId = jobOfferId; }
}
