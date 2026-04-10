package com.talentmatch.integration.ai;

import com.talentmatch.model.Candidate;
import com.talentmatch.model.JobOffer;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildMatchingPrompt(Candidate candidate, JobOffer jobOffer) {
        return """
                You are a recruiting expert. Analyze the match between this candidate and job offer.

                Candidate:
                - Name: %s %s
                - Skills: %s
                - Years of Experience: %d
                - Bio: %s

                Job Offer:
                - Title: %s at %s
                - Required Skills: %s
                - Description: %s
                - Location: %s

                Respond ONLY with a JSON object (no markdown, no explanation) in this exact format:
                {"score": <integer 0-100>, "analysis": "<brief text explaining the match>"}
                """.formatted(
                candidate.getFirstName(),
                candidate.getLastName(),
                String.join(", ", candidate.getSkills()),
                candidate.getYearsOfExperience(),
                candidate.getBio() != null ? candidate.getBio() : "",
                jobOffer.getTitle(),
                jobOffer.getCompany(),
                String.join(", ", jobOffer.getRequiredSkills()),
                jobOffer.getDescription(),
                jobOffer.getLocation()
        );
    }
}
