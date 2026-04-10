package com.talentmatch.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class JobOfferResponse {

    private UUID id;
    private String title;
    private String company;
    private List<String> requiredSkills;
    private String description;
    private String location;
    private String salaryRange;
    private Instant postedAt;

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getSalaryRange() { return salaryRange; }
    public Instant getPostedAt() { return postedAt; }

    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public void setPostedAt(Instant postedAt) { this.postedAt = postedAt; }
}
