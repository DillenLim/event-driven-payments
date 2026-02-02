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

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void emitEvent(String topic, String key, Object event) {
        log.info("Emitting event: {} to topic: {}", event, topic);
        kafkaTemplate.send(topic, key, event);
    }
}
