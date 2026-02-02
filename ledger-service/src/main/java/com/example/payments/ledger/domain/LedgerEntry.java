package com.example.payments.ledger.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ledger_entries")
public class LedgerEntry {
    @Id
    private UUID id;
    private String transactionId;
    private String walletId;
    private BigDecimal amount;
    private String type; // DEBIT / CREDIT
    private Instant timestamp;

    public LedgerEntry(String transactionId, String walletId, BigDecimal amount, String type) {
        this.id = UUID.randomUUID();
        this.transactionId = transactionId;
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now();
    }
}
