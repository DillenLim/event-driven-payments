package com.example.payments.wallet.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundsReservedEvent {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private Instant timestamp;
    private BigDecimal amount;
    private String currency;
    private String status;

    public FundsReservedEvent(String aggregateId, BigDecimal amount, String currency) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.aggregateId = aggregateId;
        this.eventType = "FundsReservedEvent";
        this.amount = amount;
        this.currency = currency;
        this.status = "SUCCESS";
    }
}
