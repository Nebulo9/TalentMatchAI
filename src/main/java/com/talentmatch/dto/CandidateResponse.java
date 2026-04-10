package com.talentmatch.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CandidateResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String githubUsername;
    private List<String> skills;
    private int yearsOfExperience;
    private String bio;
    private Instant createdAt;

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getGithubUsername() { return githubUsername; }
    public List<String> getSkills() { return skills; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public String getBio() { return bio; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public void setBio(String bio) { this.bio = bio; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
