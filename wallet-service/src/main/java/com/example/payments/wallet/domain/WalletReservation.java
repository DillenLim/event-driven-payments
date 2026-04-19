package com.example.payments.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallet_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletReservation {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // RESERVED, RELEASED, CONFIRMED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
