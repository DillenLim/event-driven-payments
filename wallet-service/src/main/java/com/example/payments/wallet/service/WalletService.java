package com.example.payments.wallet.service;

import com.example.payments.wallet.domain.Wallet;
import com.example.payments.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet createWallet(String walletId, BigDecimal initialBalance, String currency) {
        return walletRepository.save(new Wallet(walletId, initialBalance, currency));
    }

    public Optional<Wallet> getWallet(String walletId) {
        return walletRepository.findById(walletId);
    }

    @Transactional
    public boolean reserveFunds(String walletId, BigDecimal amount) {
        // Simple mock implementation
        // In real world: check balance, deduct held amount
        return walletRepository.findById(walletId)
                .map(wallet -> {
                    if (wallet.getBalance().compareTo(amount) >= 0) {
                        wallet.setBalance(wallet.getBalance().subtract(amount));
                        walletRepository.save(wallet);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}
