package com.example.payments.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class PaymentCreatedEvent extends BaseEvent {
    private BigDecimal amount;
    private String currency;
    private String debitorId;
    private String beneficiaryId;

    public PaymentCreatedEvent(String paymentId, BigDecimal amount, String currency, String debitorId, String beneficiaryId) {
        super(paymentId, "PaymentCreatedEvent");
        this.amount = amount;
        this.currency = currency;
        this.debitorId = debitorId;
        this.beneficiaryId = beneficiaryId;
    }
}
