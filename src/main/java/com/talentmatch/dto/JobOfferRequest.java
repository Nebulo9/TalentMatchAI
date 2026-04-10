package com.talentmatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class JobOfferRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String company;

    @NotEmpty
    private List<String> requiredSkills;

    @NotBlank
    private String description;

    @NotBlank
    private String location;

    private String salaryRange;

    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getSalaryRange() { return salaryRange; }

    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
}
