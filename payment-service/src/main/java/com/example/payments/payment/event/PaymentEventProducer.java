package com.example.payments.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer for Payment lifecycle events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public void emitEvent(String topic, String key, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            log.info("Emitting event: {} to topic: {}", payload, topic);
            kafkaTemplate.send(topic, key, payload);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Error serializing event: {}", event, e);
            throw new RuntimeException("Error serializing event", e);
        }
    }
}
