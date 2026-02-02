package com.example.payments.payment.service;

import com.example.payments.payment.domain.PaymentState;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * service for validation state transition.
 */
@Service
public class PaymentStateTransitionService {

    private static final Map<PaymentState, Set<PaymentState>> VALID_TRANSITIONS = Map.of(
            PaymentState.CREATED, EnumSet.of(PaymentState.FUNDS_RESERVED),
            PaymentState.FUNDS_RESERVED, EnumSet.of(PaymentState.AUTHORIZATION_IN_PROGRESS, PaymentState.CANCELLED, PaymentState.FAILED, PaymentState.EXPIRED),
            PaymentState.AUTHORIZATION_IN_PROGRESS, EnumSet.of(PaymentState.AUTHORIZED),
            PaymentState.AUTHORIZED, EnumSet.of(PaymentState.COMPLETED)
    );

    public boolean isValidTransition(PaymentState currentState, PaymentState nextState) {
        return VALID_TRANSITIONS.getOrDefault(currentState, EnumSet.noneOf(PaymentState.class))
                .contains(nextState);
    }

    public void validateTransition(PaymentState currentState, PaymentState nextState) {
        if (!isValidTransition(currentState, nextState)) {
            throw new IllegalStateException("Invalid transition from " + currentState + " to " + nextState);
        }
    }
}
