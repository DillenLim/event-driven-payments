package com.example.payments.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * Event for fund reservation failure, consumed by Payment service.
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
