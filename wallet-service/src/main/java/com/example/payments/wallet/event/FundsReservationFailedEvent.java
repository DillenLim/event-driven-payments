package com.example.payments.wallet.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;
import java.time.Instant;

/**
 * Event emitted when funds reservation fails (e.g., insufficient balance).
 */
@Getter
@ToString
@NoArgsConstructor
public class FundsReservationFailedEvent {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private Instant timestamp;
    private String status;
    private String reason;

    public FundsReservationFailedEvent(String aggregateId, String reason) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.aggregateId = aggregateId;
        this.eventType = "FundsReservationFailedEvent";
        this.status = "FAILED";
        this.reason = reason;
    }
}
