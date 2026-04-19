package com.example.payments.payment.event;

import com.example.payments.events.FundsReservedEvent;
import com.example.payments.events.InsufficientFundsEvent;
import com.example.payments.events.TransactionRecordedEvent;
import com.example.payments.payment.domain.Payment;
import com.example.payments.payment.domain.PaymentState;
import com.example.payments.payment.domain.ProcessedEvent;
import com.example.payments.payment.repository.PaymentRepository;
import com.example.payments.payment.repository.ProcessedEventRepository;
import com.example.payments.payment.service.PaymentStateTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Consumer for Payment lifecycle events.
 * Handles saga orchestration by processing events from Wallet and Ledger services.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentStateTransitionService stateTransitionService;
    private final PaymentEventProducer paymentEventProducer;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "payments.lifecycle", groupId = "payment-service-group")
    public void onEvent(Object event) {
        log.info("Received event: {}", event);

        if (event instanceof FundsReservedEvent fundsReservedEvent) {
            handleFundsReserved(fundsReservedEvent);
        } else if (event instanceof TransactionRecordedEvent transactionRecordedEvent) {
            handleTransactionRecorded(transactionRecordedEvent);
        } else if (event instanceof InsufficientFundsEvent insufficientFundsEvent) {
            handleInsufficientFunds(insufficientFundsEvent);
        } else {
            log.debug("Ignoring event type: {}", event.getClass().getSimpleName());
        }
    }

    @Transactional
    private void handleFundsReserved(FundsReservedEvent event) {
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Event {} already processed, skipping", event.getEventId());
            return;
        }

        log.info("Handling FundsReservedEvent for payment: {}", event.getPaymentId());
        
        Payment payment = paymentRepository.findById(UUID.fromString(event.getPaymentId()))
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + event.getPaymentId()));
        
        stateTransitionService.validateTransition(payment.getState(), PaymentState.AUTHORIZED);
        payment.setState(PaymentState.AUTHORIZED);
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);

        processedEventRepository.save(new ProcessedEvent(event.getEventId(), Instant.now()));
        
        log.info("Payment {} transitioned to AUTHORIZED", event.getPaymentId());
    }

    @Transactional
    private void handleTransactionRecorded(TransactionRecordedEvent event) {
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Event {} already processed, skipping", event.getEventId());
            return;
        }

        log.info("Handling TransactionRecordedEvent for payment: {}", event.getPaymentId());
        
        Payment payment = paymentRepository.findById(UUID.fromString(event.getPaymentId()))
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + event.getPaymentId()));
        
        stateTransitionService.validateTransition(payment.getState(), PaymentState.COMPLETED);
        payment.setState(PaymentState.COMPLETED);
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);
        
        // Emit PaymentCompletedEvent
        paymentEventProducer.emitPaymentCompleted(event.getPaymentId());

        processedEventRepository.save(new ProcessedEvent(event.getEventId(), Instant.now()));
        
        log.info("Payment {} transitioned to COMPLETED", event.getPaymentId());
    }

    @Transactional
    private void handleInsufficientFunds(InsufficientFundsEvent event) {
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Event {} already processed, skipping", event.getEventId());
            return;
        }

        log.info("Handling InsufficientFundsEvent for payment: {}", event.getPaymentId());
        
        Payment payment = paymentRepository.findById(UUID.fromString(event.getPaymentId()))
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + event.getPaymentId()));
        
        stateTransitionService.validateTransition(payment.getState(), PaymentState.FAILED);
        payment.setState(PaymentState.FAILED);
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);
        
        // Emit PaymentFailedEvent
        String reason = String.format("Insufficient funds: requested %s, available %s", 
                event.getRequestedAmount(), event.getAvailableBalance());
        paymentEventProducer.emitPaymentFailed(event.getPaymentId(), reason);

        processedEventRepository.save(new ProcessedEvent(event.getEventId(), Instant.now()));
        
        log.info("Payment {} transitioned to FAILED: {}", event.getPaymentId(), reason);
    }
}
