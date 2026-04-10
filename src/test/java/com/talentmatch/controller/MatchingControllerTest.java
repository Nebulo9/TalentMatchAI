package com.talentmatch.controller;

import com.talentmatch.model.MatchingResult;
import com.talentmatch.model.MatchingStatus;
import com.talentmatch.security.CustomUserDetailsService;
import com.talentmatch.security.SecurityConfig;
import com.talentmatch.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.userdetails.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchingController.class)
@Import(SecurityConfig.class)
class MatchingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatchingService matchingService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpSecurity() {
        when(customUserDetailsService.loadUserByUsername("admin"))
                .thenReturn(User.withUsername("admin")
                        .password("{noop}admin123")
                        .roles("ADMIN")
                        .build());
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
    }

    @Test
    void analyzeWithoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/matching/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"candidateId":"00000000-0000-0000-0000-000000000001",
                                "jobOfferId":"00000000-0000-0000-0000-000000000002"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void analyzeWithAuth_returns202() throws Exception {
        MatchingResult result = matchingResult();
        when(matchingService.analyze(any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/matching/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"candidateId":"00000000-0000-0000-0000-000000000001",
                                "jobOfferId":"00000000-0000-0000-0000-000000000002"}
                                """)
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void listResults_returns200() throws Exception {
        when(matchingService.findAll()).thenReturn(List.of(matchingResult()));

        mockMvc.perform(get("/api/matching/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private MatchingResult matchingResult() {
        MatchingResult r = new MatchingResult();
        r.setCandidateId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        r.setJobOfferId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        r.setStatus(MatchingStatus.PENDING);
        return r;
    }
}
