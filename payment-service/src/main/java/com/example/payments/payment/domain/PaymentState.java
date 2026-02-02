package com.example.payments.payment.domain;

/**
 * Payment lifecycle states.
 */
public enum PaymentState {
    CREATED,
    FUNDS_RESERVED,
    AUTHORIZATION_IN_PROGRESS,
    AUTHORIZED,
    COMPLETED,
    FAILED,
    CANCELLED,
    EXPIRED
}
