package com.example.payments.payment.domain;

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
    private UUID id;
    private BigDecimal amount;
    private String currency;
    private String debitorId;
    private String beneficiaryId;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    private Instant createdAt;
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
