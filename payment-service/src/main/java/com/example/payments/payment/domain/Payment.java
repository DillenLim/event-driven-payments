package com.example.payments.payment.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Aggregate root for Payment domain.
 * Represents a payment transaction progressing through the event-driven
 * workflow.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
@Schema(description = "Payment entity representing a transaction in the payment processing lifecycle")
public class Payment {

    @Id
    @Schema(description = "Globally unique identifier for the payment transaction", example = "550e8400-e29b-41d4-a716-446655440000", format = "uuid")
    private UUID id;

    @Schema(description = "Total transaction amount with precision up to 2 decimal places", example = "150.00")
    private BigDecimal amount;

    @Schema(description = "ISO 4217 three-letter currency code", example = "USD", maxLength = 3)
    private String currency;

    @Schema(description = "Unique identifier of the debitor (payer) account", example = "user-123")
    private String debitorId;

    @Schema(description = "Unique identifier of the beneficiary (payee) account", example = "merchant-456")
    private String beneficiaryId;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Current state of the payment in the processing lifecycle (CREATED, FUNDS_RESERVED, AUTHORIZED, COMPLETED, FAILED, CANCELLED)", example = "CREATED")
    private PaymentState state;

    @Schema(description = "ISO 8601 timestamp indicating when the payment was initially created", example = "2026-02-12T11:20:00Z", format = "date-time")
    private Instant createdAt;

    @Schema(description = "ISO 8601 timestamp indicating the last state transition or modification", example = "2026-02-12T11:20:00Z", format = "date-time")
    private Instant updatedAt;

    public Payment(BigDecimal amount, String currency, String debitorId, String beneficiaryId) {
        this.id = UUID.randomUUID();
        this.amount = amount;
        this.currency = currency;
        this.debitorId = debitorId;
        this.beneficiaryId = beneficiaryId;
        this.state = PaymentState.CREATED;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
