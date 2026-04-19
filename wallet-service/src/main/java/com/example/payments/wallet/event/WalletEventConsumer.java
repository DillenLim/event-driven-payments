package com.example.payments.wallet.event;

import com.example.payments.events.BaseEvent;
import com.example.payments.events.FundsReservedEvent;
import com.example.payments.events.InsufficientFundsEvent;
import com.example.payments.events.PaymentCreatedEvent;
import com.example.payments.wallet.domain.ProcessedEvent;
import com.example.payments.wallet.repository.ProcessedEventRepository;
import com.example.payments.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletEventConsumer {

    private final WalletService walletService;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    @KafkaListener(topics = "payments.lifecycle", groupId = "wallet-service-group")
    @Transactional
    public void onEvent(BaseEvent event) {
        try {
            // Only process PaymentCreatedEvent
            if (!(event instanceof PaymentCreatedEvent)) {
                return;
            }

            PaymentCreatedEvent paymentCreatedEvent = (PaymentCreatedEvent) event;

            // Check idempotency
            if (processedEventRepository.existsById(paymentCreatedEvent.getEventId())) {
                log.info("Event {} already processed. Skipping.", paymentCreatedEvent.getEventId());
                return;
            }

            log.info("Processing PaymentCreatedEvent: {}", paymentCreatedEvent);

            // Attempt to reserve funds
            boolean reserved = walletService.reserveFunds(
                    paymentCreatedEvent.getPaymentId(),
                    paymentCreatedEvent.getSourceWalletId(),
                    paymentCreatedEvent.getAmount()
            );

            if (reserved) {
                // Emit FundsReservedEvent
                FundsReservedEvent fundsReservedEvent = FundsReservedEvent.builder()
                        .eventType("FundsReservedEvent")
                        .paymentId(paymentCreatedEvent.getPaymentId())
                        .sourceWalletId(paymentCreatedEvent.getSourceWalletId())
                        .destinationWalletId(paymentCreatedEvent.getDestinationWalletId())
                        .amount(paymentCreatedEvent.getAmount())
                        .currency(paymentCreatedEvent.getCurrency())
                        .reservationId(UUID.randomUUID().toString())
                        .build();

                kafkaTemplate.send("payments.lifecycle", fundsReservedEvent);
                log.info("Emitted FundsReservedEvent for payment {}", paymentCreatedEvent.getPaymentId());
            } else {
                // Emit InsufficientFundsEvent
                InsufficientFundsEvent insufficientFundsEvent = InsufficientFundsEvent.builder()
                        .eventType("InsufficientFundsEvent")
                        .paymentId(paymentCreatedEvent.getPaymentId())
                        .walletId(paymentCreatedEvent.getSourceWalletId())
                        .requestedAmount(paymentCreatedEvent.getAmount())
                        .availableBalance(walletService.getWallet(paymentCreatedEvent.getSourceWalletId())
                                .map(w -> w.getBalance())
                                .orElse(java.math.BigDecimal.ZERO))
                        .build();

                kafkaTemplate.send("payments.lifecycle", insufficientFundsEvent);
                log.info("Emitted InsufficientFundsEvent for payment {}", paymentCreatedEvent.getPaymentId());
            }

            // Mark event as processed
            processedEventRepository.save(new ProcessedEvent(paymentCreatedEvent.getEventId(), Instant.now()));

        } catch (Exception e) {
            log.error("Error processing event: {}", event, e);
            throw new RuntimeException("Failed to process event", e);
        }
    }
}
