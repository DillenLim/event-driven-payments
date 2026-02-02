package com.example.payments.ledger.service;

import com.example.payments.ledger.domain.LedgerEntry;
import com.example.payments.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    @Transactional
    public void recordEntry(String transactionId, String walletId, BigDecimal amount, String type) {
        ledgerRepository.save(new LedgerEntry(transactionId, walletId, amount, type));
    }
}
