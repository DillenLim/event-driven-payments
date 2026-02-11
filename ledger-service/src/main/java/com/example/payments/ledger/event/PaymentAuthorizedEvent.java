package com.example.payments.ledger.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@NoArgsConstructor
public class PaymentAuthorizedEvent {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private BigDecimal amount;
    private String currency;
    private String debitorId;
    private String beneficiaryId;
}
