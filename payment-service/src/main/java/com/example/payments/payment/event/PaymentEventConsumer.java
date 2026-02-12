package com.example.payments.payment.event;

import com.example.payments.common.domain.ProcessedEvent;
import com.example.payments.common.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Consumer for Payment lifecycle events with idempotency support.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final com.example.payments.payment.service.PaymentService paymentService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payments.lifecycle", groupId = "payment-service-group")
    @Transactional
    public void onEvent(String message) {
        try {
            if (message.contains("FundsReservedEvent")) {
                FundsReservedEvent event = objectMapper.readValue(message, FundsReservedEvent.class);

                if (processedEventRepository.existsById(event.getEventId())) {
                    log.info("Event {} already processed. Skipping.", event.getEventId());
                    return;
                }

                log.info("Processing FundsReservedEvent: {}", event);

                if ("SUCCESS".equals(event.getStatus())) {
                    paymentService.authorizePayment(event.getAggregateId());
                } else {
                    log.warn("Funds reservation failed for payment: {}", event.getAggregateId());
                    paymentService.failPayment(event.getAggregateId(), "Funds reservation failed");
                }

                processedEventRepository.save(new ProcessedEvent(event.getEventId(), Instant.now()));
            } else if (message.contains("FundsReservationFailedEvent")) {
                // Direct failure event from wallet
                com.example.payments.payment.event.FundsReservationFailedEvent event = objectMapper.readValue(message,
                        com.example.payments.payment.event.FundsReservationFailedEvent.class);

                if (processedEventRepository.existsById(event.getEventId())) {
                    log.info("Event {} already processed. Skipping.", event.getEventId());
                    return;
                }

                log.warn("Processing FundsReservationFailedEvent: {}", event);
                paymentService.failPayment(event.getAggregateId(), event.getReason());

                processedEventRepository.save(new ProcessedEvent(event.getEventId(), Instant.now()));
            } else if (message.contains("TransactionRecordedEvent")) {
                TransactionRecordedEvent event = objectMapper.readValue(message, TransactionRecordedEvent.class);

                if (processedEventRepository.existsById(event.getEventId())) {
                    log.info("Event {} already processed. Skipping.", event.getEventId());
                    return;
                }

                log.info("Processing TransactionRecordedEvent: {}", event);

                if ("SUCCESS".equals(event.getStatus())) {
                    paymentService.completePayment(event.getAggregateId());
                } else {
                    log.warn("Transaction recording failed for payment: {}", event.getAggregateId());
                    paymentService.failPayment(event.getAggregateId(), "Ledger recording failed");
                }

                processedEventRepository.save(new ProcessedEvent(event.getEventId(), Instant.now()));
            }
        } catch (Exception e) {
            log.error("Failed to process event", e);
        }
    }
}
