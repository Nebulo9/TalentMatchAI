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
class JobOfferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaAdmin kafkaAdmin;

    @MockitoBean
    private MatchingProducer matchingProducer;

    @Test
    void createAndList() throws Exception {
        mockMvc.perform(post("/api/job-offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Backend Developer","company":"TechCorp",
                                "requiredSkills":["Java","Kafka"],
                                "description":"Build great APIs","location":"Remote"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Backend Developer"));
    }
}
