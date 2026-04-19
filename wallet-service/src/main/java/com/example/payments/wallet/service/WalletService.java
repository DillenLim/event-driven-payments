package com.example.payments.wallet.service;

import com.example.payments.wallet.domain.Wallet;
import com.example.payments.wallet.domain.WalletReservation;
import com.example.payments.wallet.repository.WalletRepository;
import com.example.payments.wallet.repository.WalletReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletReservationRepository walletReservationRepository;

    @Transactional
    public Wallet createWallet(String walletId, String ownerName, BigDecimal initialBalance, String currency) {
        return walletRepository.save(new Wallet(walletId, ownerName, initialBalance, currency));
    }

    public Optional<Wallet> getWallet(String walletId) {
        return walletRepository.findById(walletId);
    }

    @Transactional
    public boolean reserveFunds(String paymentId, String walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds in wallet {}. Balance: {}, Required: {}",
                    walletId, wallet.getBalance(), amount);
            return false;
        }

        // Deduct amount from wallet balance
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // Create reservation
        WalletReservation reservation = WalletReservation.builder()
                .id(UUID.randomUUID())
                .walletId(walletId)
                .paymentId(paymentId)
                .amount(amount)
                .status("RESERVED")
                .createdAt(Instant.now())
                .build();
        walletReservationRepository.save(reservation);

        log.info("Reserved {} from wallet {} for payment {}", amount, walletId, paymentId);
        return true;
    }

    @Transactional
    public void releaseFunds(String paymentId) {
        WalletReservation reservation = walletReservationRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found for payment: " + paymentId));

        if ("RELEASED".equals(reservation.getStatus())) {
            log.info("Funds already released for payment {}", paymentId);
            return;
        }

        Wallet wallet = walletRepository.findById(reservation.getWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + reservation.getWalletId()));

        // Add reserved amount back to wallet balance
        wallet.setBalance(wallet.getBalance().add(reservation.getAmount()));
        walletRepository.save(wallet);

        // Update reservation status
        reservation.setStatus("RELEASED");
        walletReservationRepository.save(reservation);

        log.info("Released {} back to wallet {} for payment {}",
                reservation.getAmount(), reservation.getWalletId(), paymentId);
    }
}
