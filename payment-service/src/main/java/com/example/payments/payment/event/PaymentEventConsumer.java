package com.example.payments.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for Payment lifecycle events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    @KafkaListener(topics = "payments.lifecycle", groupId = "payment-service-group")
    public void onEvent(String event) {
        log.info("Received event: {}", event);
        // Logic to process event (e.g., update state based on other service events if needed)
        // For now, this service mostly produces, but might listen to Wallet/Ledger feedbacks if architecture dictates.
        // The spec mentions "Consumer Requirements: Idempotent, validate state"
    }
}
