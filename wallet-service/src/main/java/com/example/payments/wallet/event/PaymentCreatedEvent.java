package com.example.payments.wallet.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaymentCreatedEvent {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private BigDecimal amount;
    private String currency;
    private String debitorId;
    private String beneficiaryId;
}
