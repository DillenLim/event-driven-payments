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
                request.getBeneficiaryId()
        );
        Payment savedPayment = paymentRepository.save(payment);

        com.example.payments.payment.event.PaymentCreatedEvent event = new com.example.payments.payment.event.PaymentCreatedEvent(
                savedPayment.getId().toString(),
                savedPayment.getAmount(),
                savedPayment.getCurrency(),
                savedPayment.getDebitorId(),
                savedPayment.getBeneficiaryId()
        );
        paymentEventProducer.emitEvent("payments.lifecycle", savedPayment.getId().toString(), event);

        return savedPayment;
    }

    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
    }
}
