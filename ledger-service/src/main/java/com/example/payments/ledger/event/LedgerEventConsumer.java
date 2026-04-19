package com.example.payments.ledger.event;

import com.example.payments.events.FundsReservedEvent;
import com.example.payments.events.TransactionRecordedEvent;
import com.example.payments.ledger.domain.LedgerEntry;
import com.example.payments.ledger.domain.ProcessedEvent;
import com.example.payments.ledger.repository.ProcessedEventRepository;
import com.example.payments.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class LedgerEventConsumer {

    private final LedgerService ledgerService;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payments.lifecycle", groupId = "ledger-service-group")
    @Transactional
    public void onEvent(Object event) {
        try {
            if (event instanceof FundsReservedEvent) {
                FundsReservedEvent fundsReservedEvent = (FundsReservedEvent) event;
                String eventId = fundsReservedEvent.getEventId();

                log.info("Received FundsReservedEvent: {}", eventId);

                // Check idempotency
                if (processedEventRepository.existsById(eventId)) {
                    log.info("Event {} already processed. Skipping.", eventId);
                    return;
                }

                // Record ledger entry
                LedgerEntry entry = ledgerService.recordEntry(
                        fundsReservedEvent.getPaymentId(),
                        fundsReservedEvent.getSourceWalletId(),
                        fundsReservedEvent.getDestinationWalletId(),
                        fundsReservedEvent.getAmount(),
                        fundsReservedEvent.getCurrency()
                );

                log.info("Ledger entry recorded with id: {} for payment: {}", 
                        entry.getId(), fundsReservedEvent.getPaymentId());

                // Emit TransactionRecordedEvent
                TransactionRecordedEvent transactionRecordedEvent = TransactionRecordedEvent.builder()
                        .eventType("TransactionRecordedEvent")
                        .paymentId(fundsReservedEvent.getPaymentId())
                        .ledgerEntryId(entry.getId().toString())
                        .debitWalletId(fundsReservedEvent.getSourceWalletId())
                        .creditWalletId(fundsReservedEvent.getDestinationWalletId())
                        .amount(fundsReservedEvent.getAmount())
                        .build();

                kafkaTemplate.send("payments.lifecycle", transactionRecordedEvent);
                log.info("TransactionRecordedEvent emitted for payment: {}", fundsReservedEvent.getPaymentId());

                // Save to ProcessedEvent table
                processedEventRepository.save(new ProcessedEvent(eventId, Instant.now()));
                log.info("Event {} marked as processed", eventId);
            }
        } catch (Exception e) {
            log.error("Error processing FundsReservedEvent", e);
            throw new RuntimeException("Failed to process FundsReservedEvent", e);
        }
    }
}
