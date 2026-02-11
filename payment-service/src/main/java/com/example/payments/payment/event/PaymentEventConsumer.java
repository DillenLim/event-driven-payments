package com.example.payments.payment.event;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final com.example.payments.payment.service.PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payments.lifecycle", groupId = "payment-service-group")
    public void onEvent(String message) {
        try {
            if (message.contains("FundsReservedEvent")) {
                FundsReservedEvent event = objectMapper.readValue(message, FundsReservedEvent.class);
                log.info("Processing FundsReservedEvent: {}", event);

                if ("SUCCESS".equals(event.getStatus())) {
                    paymentService.authorizePayment(event.getAggregateId());
                } else {
                    log.warn("Funds reservation failed for payment: {}", event.getAggregateId());
                    // Handle failure (e.g. cancel payment)
                }
            } else if (message.contains("TransactionRecordedEvent")) {
                TransactionRecordedEvent event = objectMapper.readValue(message, TransactionRecordedEvent.class);
                log.info("Processing TransactionRecordedEvent: {}", event);

                if ("SUCCESS".equals(event.getStatus())) {
                    paymentService.completePayment(event.getAggregateId());
                } else {
                    log.warn("Transaction recording failed for payment: {}", event.getAggregateId());
                }
            }
        } catch (Exception e) {
            log.error("Failed to process event", e);
        }
    }
}
