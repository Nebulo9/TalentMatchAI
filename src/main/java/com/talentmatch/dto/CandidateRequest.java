package com.talentmatch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CandidateRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    private String githubUsername;

    @NotEmpty
    private List<String> skills;

    @Min(0)
    private int yearsOfExperience;

    private String bio;

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getGithubUsername() { return githubUsername; }
    public List<String> getSkills() { return skills; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public String getBio() { return bio; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public void setBio(String bio) { this.bio = bio; }
}
