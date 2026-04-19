package com.example.payments.payment.exception;

public class InvalidPaymentStateException extends RuntimeException {
    public InvalidPaymentStateException(String currentState, String targetState) {
        super("Invalid state transition from " + currentState + " to " + targetState);
    }
}
