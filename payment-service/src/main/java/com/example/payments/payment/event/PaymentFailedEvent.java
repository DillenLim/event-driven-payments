package com.example.payments.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Event emitted when a payment fails.
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class PaymentFailedEvent extends BaseEvent {
    private String reason;

    public PaymentFailedEvent(String aggregateId, String reason) {
        super(aggregateId, "PaymentFailedEvent");
        this.reason = reason;
    }
}
