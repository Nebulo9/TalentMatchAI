package com.talentmatch.integration.github;

import com.talentmatch.model.Candidate;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GithubService {

    private final GithubClient client;

    public GithubService(GithubClient client) {
        this.client = client;
    }

    public Candidate buildCandidateFromGithub(String githubUsername) {
        List<Map<String, Object>> repos = client.getRepos(githubUsername);
        Map<String, Object> user = client.getUser(githubUsername);

        List<String> skills = repos.stream()
                .map(r -> (String) r.get("language"))
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (skills.isEmpty()) {
            skills = List.of("Unknown");
        }

        int currentYear = Year.now().getValue();
        int oldestYear = repos.stream()
                .map(r -> (String) r.get("created_at"))
                .filter(Objects::nonNull)
                .map(s -> Integer.parseInt(s.substring(0, 4)))
                .min(Integer::compareTo)
                .orElse(currentYear);
        int yearsOfExperience = currentYear - oldestYear;

        String bio = (String) user.getOrDefault("bio", "");
        if (bio == null) bio = "";

        String fullName = (String) user.getOrDefault("name", githubUsername);
        if (fullName == null || fullName.isBlank()) fullName = githubUsername;
        String[] parts = fullName.split(" ", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        String email = (String) user.get("email");
        if (email == null || email.isBlank()) {
            email = githubUsername + "@github.local";
        }

        Candidate candidate = new Candidate();
        candidate.setFirstName(firstName);
        candidate.setLastName(lastName);
        candidate.setEmail(email);
        candidate.setGithubUsername(githubUsername);
        candidate.setSkills(skills);
        candidate.setYearsOfExperience(yearsOfExperience);
        candidate.setBio(bio);
        return candidate;
    }
}
