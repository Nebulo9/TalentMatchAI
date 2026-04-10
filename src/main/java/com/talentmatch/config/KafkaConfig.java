package com.talentmatch.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.matching-requests}")
    private String matchingRequestsTopic;

    @Bean
    public NewTopic matchingRequestsTopic() {
        return TopicBuilder.name(matchingRequestsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
