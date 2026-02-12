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
 * Aggregate root for Payment.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment {

    @Id
    @Schema(description = "Unique payment identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Payment amount", example = "150.00")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Debitor user ID", example = "user-123")
    private String debitorId;

    @Schema(description = "Beneficiary merchant ID", example = "merchant-456")
    private String beneficiaryId;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Payment state", example = "CREATED")
    private PaymentState state;

    @Schema(description = "Timestamp when payment was created", example = "2026-02-12T11:20:00Z")
    private Instant createdAt;

    @Schema(description = "Timestamp when payment was last updated", example = "2026-02-12T11:20:00Z")
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
