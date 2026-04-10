package com.talentmatch.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import com.talentmatch.kafka.MatchingProducer;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9999",
        "spring.kafka.listener.auto-startup=false"
})
class CandidateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaAdmin kafkaAdmin;

    @MockitoBean
    private MatchingProducer matchingProducer;

    @Test
    void createAndRetrieve() throws Exception {
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Alice","lastName":"Smith",
                                "email":"alice.ci@example.com",
                                "skills":["Java","Spring"],"yearsOfExperience":5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice.ci@example.com"));
    }

    @Test
    void duplicateEmailReturns400() throws Exception {
        String body = """
                {"firstName":"Bob","lastName":"Jones",
                "email":"duplicate.ci@example.com",
                "skills":["Python"],"yearsOfExperience":2}
                """;

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void notFoundReturns404() throws Exception {
        mockMvc.perform(get("/api/candidates/00000000-0000-0000-0000-000000000099"))
                .andExpect(status().isNotFound());
    }
}
