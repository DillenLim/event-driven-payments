package com.example.payments.ledger.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRecordedEvent {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private Instant timestamp;
    private String transactionId;
    private String status;

    public TransactionRecordedEvent(String aggregateId, String transactionId) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.aggregateId = aggregateId;
        this.eventType = "TransactionRecordedEvent";
        this.transactionId = transactionId;
        this.status = "SUCCESS";
    }
}
