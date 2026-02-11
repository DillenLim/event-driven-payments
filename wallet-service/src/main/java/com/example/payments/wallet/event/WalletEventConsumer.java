package com.example.payments.wallet.event;

import com.example.payments.wallet.domain.ProcessedEvent;
import com.example.payments.wallet.repository.ProcessedEventRepository;
import com.example.payments.wallet.service.WalletService;
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
public class WalletEventConsumer {

    private final WalletService walletService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;
    private final WalletEventProducer walletEventProducer;

    @KafkaListener(topics = "payments.lifecycle", groupId = "wallet-service-group")
    @Transactional
    public void onEvent(String message) {
        try {
            // Simple check to identify event type (in real world uses headers or wrapper)
            if (message.contains("PaymentCreatedEvent")) {
                PaymentCreatedEvent event = objectMapper.readValue(message, PaymentCreatedEvent.class);

                if (processedEventRepository.existsById(event.getEventId())) {
                    log.info("Event {} already processed. Skipping.", event.getEventId());
                    return;
                }

                log.info("Processing PaymentCreatedEvent: {}", event);

                boolean success = walletService.reserveFunds(event.getDebitorId(), event.getAmount());

                if (success) {
                    FundsReservedEvent fundsReservedEvent = new FundsReservedEvent(
                            event.getAggregateId(), // paymentId
                            event.getAmount(),
                            event.getCurrency());
                    walletEventProducer.emitEvent("payments.lifecycle", event.getAggregateId(), fundsReservedEvent);
                } else {
                    log.warn("Failed to reserve funds for payment: {}", event.getAggregateId());
                    // In a real system, we might emit PaymentFailedEvent here
                }

                processedEventRepository.save(new ProcessedEvent(event.getEventId(), Instant.now()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse event", e);
        }
    }
}
