package com.talentmatch.integration;

import com.talentmatch.kafka.MatchingProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9999",
        "spring.kafka.listener.auto-startup=false"
})
class MatchingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaAdmin kafkaAdmin;

    @MockitoBean
    private MatchingProducer matchingProducer;

    @Test
    void analyzeCreates202AndPending() throws Exception {
        doNothing().when(matchingProducer).publish(any(), any(), any());

        MvcResult candidateResult = mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Charlie","lastName":"Brown",
                                "email":"charlie.matching@example.com",
                                "skills":["Java"],"yearsOfExperience":4}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String candidateJson = candidateResult.getResponse().getContentAsString();
        JsonNode candidateNode = objectMapper.readTree(candidateJson);
        String candidateId = candidateNode.get("id").asText();

        MvcResult jobResult = mockMvc.perform(post("/api/job-offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Java Engineer","company":"MatchCorp",
                                "requiredSkills":["Java"],"description":"Great role","location":"Remote"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String jobJson = jobResult.getResponse().getContentAsString();
        JsonNode jobNode = objectMapper.readTree(jobJson);
        String jobId = jobNode.get("id").asText();

        mockMvc.perform(post("/api/matching/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"candidateId\":\"" + candidateId + "\",\"jobOfferId\":\"" + jobId + "\"}")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
