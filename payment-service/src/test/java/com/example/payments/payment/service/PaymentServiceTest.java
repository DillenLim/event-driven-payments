package com.example.payments.payment.service;

import com.example.payments.payment.domain.Payment;
import com.example.payments.payment.domain.PaymentState;
import com.example.payments.payment.repository.PaymentRepository;
import com.example.payments.payment.web.CreatePaymentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_ShouldReturnCreatedPayment() {
        // Given
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .debitorId("user1")
                .beneficiaryId("user2")
                .build();

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Payment payment = paymentService.createPayment(request);

        // Then
        assertNotNull(payment);
        assertEquals(PaymentState.CREATED, payment.getState());
        assertEquals(new BigDecimal("100.00"), payment.getAmount());
        verify(paymentRepository).save(any(Payment.class));
    }
}
