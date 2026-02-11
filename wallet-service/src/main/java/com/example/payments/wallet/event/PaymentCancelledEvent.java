package com.example.payments.wallet.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event for payment cancellation, consumed by Wallet service to release funds.
 */
@Getter
@ToString
@NoArgsConstructor
public class PaymentCancelledEvent {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private Instant timestamp;
    private String walletId;
    private BigDecimal amount;
    private String currency;

    public PaymentCancelledEvent(String aggregateId, String walletId, BigDecimal amount, String currency) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.aggregateId = aggregateId;
        this.eventType = "PaymentCancelledEvent";
        this.walletId = walletId;
        this.amount = amount;
        this.currency = currency;
    }
}
