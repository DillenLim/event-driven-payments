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
public class FundsReservedEvent extends BaseEvent {
    private String paymentId;
    private String sourceWalletId;
    private String destinationWalletId;
    private BigDecimal amount;
    private String currency;
    private String reservationId;
}
