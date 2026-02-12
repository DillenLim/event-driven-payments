package com.example.payments.payment.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating a new payment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @Schema(description = "Payment amount", example = "150.00")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Debitor user ID", example = "user-123")
    private String debitorId;

    @Schema(description = "Beneficiary merchant ID", example = "merchant-456")
    private String beneficiaryId;
}
