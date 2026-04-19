package com.example.payments.payment.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String walletId, String amount) {
        super("Insufficient funds in wallet " + walletId + " for amount " + amount);
    }
}
