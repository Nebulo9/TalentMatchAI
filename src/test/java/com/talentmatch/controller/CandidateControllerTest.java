package com.talentmatch.controller;

import com.talentmatch.exception.NotFoundException;
import com.talentmatch.model.Candidate;
import com.talentmatch.security.CustomUserDetailsService;
import com.talentmatch.security.SecurityConfig;
import com.talentmatch.service.CandidateService;
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

@WebMvcTest(CandidateController.class)
@Import(SecurityConfig.class)
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CandidateService candidateService;

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
    void createCandidate_returns201() throws Exception {
        Candidate saved = candidate();
        when(candidateService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Alice","lastName":"Smith","email":"alice@example.com",
                                "skills":["Java"],"yearsOfExperience":3}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void listCandidates_returns200() throws Exception {
        when(candidateService.findAll()).thenReturn(List.of(candidate()));

        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }

    @Test
    void getCandidate_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(candidateService.findById(id)).thenThrow(new NotFoundException("Candidate not found"));

        mockMvc.perform(get("/api/candidates/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void importWithoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/candidates/import")
                        .param("githubUsername", "torvalds"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void importWithAuth_returns201() throws Exception {
        Candidate saved = candidate();
        when(candidateService.importFromGithub("torvalds")).thenReturn(saved);

        mockMvc.perform(post("/api/candidates/import")
                        .param("githubUsername", "torvalds")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isCreated());
    }

    private Candidate candidate() {
        Candidate c = new Candidate();
        c.setFirstName("Alice");
        c.setLastName("Smith");
        c.setEmail("alice@example.com");
        c.setSkills(List.of("Java"));
        c.setYearsOfExperience(3);
        return c;
    }
}
