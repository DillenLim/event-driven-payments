package com.example.payments.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransactionRecordedEvent extends BaseEvent {
    private String paymentId;
    private String ledgerEntryId;
    private String debitWalletId;
    private String creditWalletId;
    private BigDecimal amount;
}
