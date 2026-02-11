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
public class FundsReservedEvent extends BaseEvent {
    private BigDecimal amount;
    private String currency;
    private String status;

    public FundsReservedEvent(String aggregateId, BigDecimal amount, String currency, String status) {
        super(aggregateId, "FundsReservedEvent");
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }
}
