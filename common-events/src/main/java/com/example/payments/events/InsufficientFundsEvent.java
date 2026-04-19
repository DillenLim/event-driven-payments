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
public class InsufficientFundsEvent extends BaseEvent {
    private String paymentId;
    private String walletId;
    private BigDecimal requestedAmount;
    private BigDecimal availableBalance;
}
