package com.talentmatch.actuator;

import com.talentmatch.service.MatchingService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "matching-stats")
public class MatchingStatsEndpoint {

    private final MatchingService matchingService;

    public MatchingStatsEndpoint(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @ReadOperation
    public Map<String, Long> matchingStats() {
        return matchingService.getStats();
    }
}
