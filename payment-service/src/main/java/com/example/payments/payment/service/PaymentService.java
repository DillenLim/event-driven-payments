package com.example.payments.payment.service;

import com.example.payments.payment.domain.Payment;
import com.example.payments.payment.repository.PaymentRepository;
import com.example.payments.payment.web.CreatePaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for handling payment business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final com.example.payments.payment.event.PaymentEventProducer paymentEventProducer;

    @Transactional
    public Payment createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for debitor: {}", request.getDebitorId());
        Payment payment = new Payment(
                request.getAmount(),
                request.getCurrency(),
                request.getDebitorId(),
                request.getBeneficiaryId());
        Payment savedPayment = paymentRepository.save(payment);

        com.example.payments.payment.event.PaymentCreatedEvent event = new com.example.payments.payment.event.PaymentCreatedEvent(
                savedPayment.getId().toString(),
                savedPayment.getAmount(),
                savedPayment.getCurrency(),
                savedPayment.getDebitorId(),
                savedPayment.getBeneficiaryId());
        paymentEventProducer.emitEvent("payments.lifecycle", savedPayment.getId().toString(), event);

        return savedPayment;
    }

    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
    }

    @Transactional
    public void authorizePayment(String paymentId) {
        Payment payment = getPayment(UUID.fromString(paymentId));

        // Handle transition from CREATED -> FUNDS_RESERVED upon receiving
        // FundsReservedEvent
        if (payment.getState() == com.example.payments.payment.domain.PaymentState.CREATED) {
            log.info("Funds reserved for payment: {}. Transitioning to FUNDS_RESERVED.", paymentId);
            payment.setState(com.example.payments.payment.domain.PaymentState.FUNDS_RESERVED);
            payment.setUpdatedAt(java.time.Instant.now());
            payment = paymentRepository.save(payment);
        }

        // Transition state
        // Enforcing correct path: FUNDS_RESERVED -> AUTHORIZATION_IN_PROGRESS ->
        // AUTHORIZED
        if (payment.getState() == com.example.payments.payment.domain.PaymentState.FUNDS_RESERVED) {

            // Step 1: AUTHORIZATION_IN_PROGRESS
            payment.setState(com.example.payments.payment.domain.PaymentState.AUTHORIZATION_IN_PROGRESS);
            payment.setUpdatedAt(java.time.Instant.now());
            paymentRepository.save(payment); // Save intermediate state

            // Step 2: AUTHORIZED
            payment.setState(com.example.payments.payment.domain.PaymentState.AUTHORIZED);
            payment.setUpdatedAt(java.time.Instant.now());
            paymentRepository.save(payment);

            log.info("Payment authorized: {}", paymentId);

            com.example.payments.payment.event.PaymentAuthorizedEvent event = new com.example.payments.payment.event.PaymentAuthorizedEvent(
                    payment.getId().toString(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getDebitorId(),
                    payment.getBeneficiaryId());
            paymentEventProducer.emitEvent("payments.lifecycle", payment.getId().toString(), event);
        }
    }

    @Transactional
    public void completePayment(String paymentId) {
        Payment payment = getPayment(UUID.fromString(paymentId));

        if (payment.getState() == com.example.payments.payment.domain.PaymentState.AUTHORIZED) {
            payment.setState(com.example.payments.payment.domain.PaymentState.COMPLETED);
            payment.setUpdatedAt(java.time.Instant.now());
            paymentRepository.save(payment);

            log.info("Payment completed: {}", paymentId);
            // Could emit PaymentCompletedEvent here
        }
    }

    /**
     * Marks a payment as failed.
     */
    @Transactional
    public void failPayment(String paymentId, String reason) {
        Payment payment = getPayment(UUID.fromString(paymentId));

        if (payment.getState() != com.example.payments.payment.domain.PaymentState.COMPLETED &&
                payment.getState() != com.example.payments.payment.domain.PaymentState.FAILED) {

            payment.setState(com.example.payments.payment.domain.PaymentState.FAILED);
            payment.setUpdatedAt(java.time.Instant.now());
            paymentRepository.save(payment);

            log.warn("Payment failed: {} - Reason: {}", paymentId, reason);

            // Emit PaymentFailedEvent
            com.example.payments.payment.event.PaymentFailedEvent event = new com.example.payments.payment.event.PaymentFailedEvent(
                    paymentId, reason);
            paymentEventProducer.emitEvent("payments.lifecycle", paymentId, event);
        }
    }

    /**
     * Cancels a payment and triggers fund release.
     */
    @Transactional
    public void cancelPayment(String paymentId, String debitorId, java.math.BigDecimal amount, String currency) {
        Payment payment = getPayment(UUID.fromString(paymentId));

        payment.setState(com.example.payments.payment.domain.PaymentState.CANCELLED);
        payment.setUpdatedAt(java.time.Instant.now());
        paymentRepository.save(payment);

        log.info("Payment cancelled: {}", paymentId);

        // Emit PaymentCancelledEvent to trigger fund release
        com.example.payments.payment.event.PaymentCancelledEvent event = new com.example.payments.payment.event.PaymentCancelledEvent(
                paymentId, debitorId, amount, currency);
        paymentEventProducer.emitEvent("payments.lifecycle", paymentId, event);
    }
}
