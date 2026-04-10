package com.talentmatch.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String githubUsername;

    @NotEmpty
    @ElementCollection
    private List<String> skills;

    @Min(0)
    private int yearsOfExperience;

    @Column(length = 2000)
    private String bio;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getGithubUsername() { return githubUsername; }
    public List<String> getSkills() { return skills; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public String getBio() { return bio; }
    public Instant getCreatedAt() { return createdAt; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public void setBio(String bio) { this.bio = bio; }
}
