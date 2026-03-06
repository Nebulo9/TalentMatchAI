package com.talentmatch.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
public class JobOffer {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String title;

    @NotBlank
    private String company;

    @NotEmpty
    @ElementCollection
    private List<String> requiredSkills;

    @NotBlank
    @Column(length = 2000)
    private String description;

    @NotBlank
    private String location;

    private String salaryRange;

    private Instant postedAt;

    @PrePersist
    public void prePersist(){
        this.postedAt = Instant.now();
    }

    // ++++ GETTERS AND SETTERS ++++


    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public Instant getPostedAt() {
        return postedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }
}