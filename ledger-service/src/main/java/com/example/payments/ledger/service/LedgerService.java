package com.example.payments.ledger.service;

import com.example.payments.ledger.domain.LedgerEntry;
import com.example.payments.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    @Transactional
    public LedgerEntry recordEntry(String paymentId, String debitWalletId, String creditWalletId, 
                                    BigDecimal amount, String currency) {
        LedgerEntry entry = new LedgerEntry();
        entry.setId(UUID.randomUUID());
        entry.setPaymentId(paymentId);
        entry.setDebitWalletId(debitWalletId);
        entry.setCreditWalletId(creditWalletId);
        entry.setAmount(amount);
        entry.setCurrency(currency);
        entry.setEntryType("PAYMENT_TRANSFER");
        entry.setCreatedAt(Instant.now());
        return ledgerRepository.save(entry);
    }
}
