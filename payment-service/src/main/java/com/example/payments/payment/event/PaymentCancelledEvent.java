package com.example.payments.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event emitted when a payment is cancelled, triggering fund release.
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class PaymentCancelledEvent extends BaseEvent {
    private String walletId;
    private BigDecimal amount;
    private String currency;

    public PaymentCancelledEvent(String aggregateId, String walletId, BigDecimal amount, String currency) {
        super(aggregateId, "PaymentCancelledEvent");
        this.walletId = walletId;
        this.amount = amount;
        this.currency = currency;
    }
}
