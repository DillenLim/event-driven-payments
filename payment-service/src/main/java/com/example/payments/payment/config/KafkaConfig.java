package com.example.payments.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Configuration for Payment Service.
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic paymentLifecycleTopic() {
        return TopicBuilder.name("payments.lifecycle")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
