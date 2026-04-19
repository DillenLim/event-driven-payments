package com.example.payments.ledger.domain;

import jakarta.persistence.Column;
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
    @Column(name = "id")
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @Column(name = "debit_wallet_id", nullable = false)
    private String debitWalletId;

    @Column(name = "credit_wallet_id", nullable = false)
    private String creditWalletId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "entry_type", nullable = false, length = 50)
    private String entryType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public LedgerEntry(String paymentId, String debitWalletId, String creditWalletId, 
                       BigDecimal amount, String currency, String entryType) {
        this.id = UUID.randomUUID();
        this.paymentId = paymentId;
        this.debitWalletId = debitWalletId;
        this.creditWalletId = creditWalletId;
        this.amount = amount;
        this.currency = currency;
        this.entryType = entryType;
        this.createdAt = Instant.now();
    }
}
