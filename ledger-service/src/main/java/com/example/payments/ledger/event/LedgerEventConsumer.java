package com.example.payments.ledger.event;

import com.example.payments.common.domain.ProcessedEvent;
import com.example.payments.common.repository.ProcessedEventRepository;
import com.example.payments.ledger.service.LedgerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class LedgerEventConsumer {

    private final LedgerService ledgerService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;
    private final LedgerEventProducer ledgerEventProducer;

    @KafkaListener(topics = "payments.lifecycle", groupId = "ledger-service-group")
    @Transactional
    public void onEvent(String message) {
        try {
            if (message.contains("PaymentAuthorizedEvent")) {
                PaymentAuthorizedEvent event = objectMapper.readValue(message, PaymentAuthorizedEvent.class);

                if (processedEventRepository.existsById(event.getEventId())) {
                    log.info("Event {} already processed. Skipping.", event.getEventId());
                    return;
                }

                log.info("Recording Ledger for PaymentAuthorizedEvent: {}", event);

                // Record logic placeholder
                // ledgerService.recordEntry(...)
                // For now, simulate success:
                String transactionId = java.util.UUID.randomUUID().toString();

                TransactionRecordedEvent recordedEvent = new TransactionRecordedEvent(
                        event.getAggregateId(),
                        transactionId);

                ledgerEventProducer.emitEvent("payments.lifecycle", event.getAggregateId(), recordedEvent);

                processedEventRepository.save(new ProcessedEvent(event.getEventId(), Instant.now()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse event", e);
        }
    }
}
