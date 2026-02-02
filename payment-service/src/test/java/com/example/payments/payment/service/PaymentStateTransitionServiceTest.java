package com.example.payments.payment.service;

import com.example.payments.payment.domain.PaymentState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentStateTransitionServiceTest {

    private final PaymentStateTransitionService service = new PaymentStateTransitionService();

    @Test
    void validateTransition_ShouldPassForValidTransition() {
        assertDoesNotThrow(() -> service.validateTransition(PaymentState.CREATED, PaymentState.FUNDS_RESERVED));
    }

    @Test
    void validateTransition_ShouldThrowForInvalidTransition() {
        assertThrows(IllegalStateException.class, () -> service.validateTransition(PaymentState.CREATED, PaymentState.COMPLETED));
    }
}
