package com.example.payments.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEvent {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private Instant timestamp;

    public BaseEvent(String aggregateId, String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
    }
}
