package com.example.payments.wallet.service;

import com.example.payments.wallet.domain.Wallet;
import com.example.payments.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
        log.info("Attempting to reserve {} for wallet: {}", amount, walletId);
        return walletRepository.findById(walletId)
                .map(wallet -> {
                    if (wallet.getBalance().compareTo(amount) >= 0) {
                        wallet.setBalance(wallet.getBalance().subtract(amount));
                        walletRepository.save(wallet);
                        log.info("Successfully reserved {} for wallet: {}. New balance: {}",
                                amount, walletId, wallet.getBalance());
                        return true;
                    }
                    log.warn("Insufficient funds in wallet: {}. Required: {}, Available: {}",
                            walletId, amount, wallet.getBalance());
                    return false;
                })
                .orElseGet(() -> {
                    log.error("Wallet not found: {}", walletId);
                    return false;
                });
    }

    /**
     * Releases (unreserves) funds back to wallet as a compensating transaction.
     * Called when payment fails or is cancelled after funds were reserved.
     */
    @Transactional
    public void releaseFunds(String walletId, BigDecimal amount) {
        log.info("Releasing funds: {} back to wallet: {}", amount, walletId);
        walletRepository.findById(walletId)
                .ifPresentOrElse(
                        wallet -> {
                            wallet.setBalance(wallet.getBalance().add(amount));
                            walletRepository.save(wallet);
                            log.info("Successfully released {} to wallet: {}. New balance: {}",
                                    amount, walletId, wallet.getBalance());
                        },
                        () -> log.error("Failed to release funds: Wallet not found: {}", walletId));
    }
}
