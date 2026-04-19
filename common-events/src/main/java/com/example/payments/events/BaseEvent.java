package com.example.payments.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEvent {
    @lombok.Builder.Default
    private String eventId = UUID.randomUUID().toString();
    
    private String eventType;
    
    @lombok.Builder.Default
    private Instant timestamp = Instant.now();
}
