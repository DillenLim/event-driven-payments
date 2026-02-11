package com.example.payments.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public class PaymentAuthorizedEvent extends BaseEvent {
    private BigDecimal amount;
    private String currency;
    private String debitorId;
    private String beneficiaryId;

    public PaymentAuthorizedEvent(String aggregateId, BigDecimal amount, String currency, String debitorId,
            String beneficiaryId) {
        super(aggregateId, "PaymentAuthorizedEvent");
        this.amount = amount;
        this.currency = currency;
        this.debitorId = debitorId;
        this.beneficiaryId = beneficiaryId;
    }
}
