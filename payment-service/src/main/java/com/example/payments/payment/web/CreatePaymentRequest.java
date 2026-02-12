package com.example.payments.payment.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request object for creating a new payment transaction.
 * Contains all required information to initiate a payment between a debitor and
 * beneficiary.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment creation request containing transaction details and party identifiers")
public class CreatePaymentRequest {

    @Schema(description = "Transaction amount to be processed. Must be a positive decimal value with up to 2 decimal places.", example = "150.00", required = true)
    private BigDecimal amount;

    @Schema(description = "ISO 4217 currency code for the transaction (e.g., USD, EUR, GBP).", example = "USD", required = true, maxLength = 3)
    private String currency;

    @Schema(description = "Unique identifier of the debitor (payer) initiating the payment.", example = "user-123", required = true)
    private String debitorId;

    @Schema(description = "Unique identifier of the beneficiary (payee) receiving the payment.", example = "merchant-456", required = true)
    private String beneficiaryId;
}
