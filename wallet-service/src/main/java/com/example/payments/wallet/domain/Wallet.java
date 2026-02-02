package com.example.payments.wallet.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wallets")
public class Wallet {
    @Id
    private String walletId;
    private BigDecimal balance;
    private String currency;

    public Wallet(String walletId, BigDecimal balance, String currency) {
        this.walletId = walletId;
        this.balance = balance;
        this.currency = currency;
    }
}
