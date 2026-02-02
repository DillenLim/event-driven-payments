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

    @Transactional
    public Payment createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for debitor: {}", request.getDebitorId());
        Payment payment = new Payment(
                request.getAmount(),
                request.getCurrency(),
                request.getDebitorId(),
                request.getBeneficiaryId()
        );
        return paymentRepository.save(payment);
    }

    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
    }
}
