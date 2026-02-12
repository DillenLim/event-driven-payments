package com.example.payments.payment.web;

import com.example.payments.payment.domain.Payment;
import com.example.payments.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Payment operations.
 * Provides endpoints for creating and retrieving payment transactions.
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Endpoints for managing payment transactions. Supports payment creation, retrieval, and status tracking within the event-driven payment processing system.")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Initiates a new payment transaction with the specified amount and parties. The payment begins in CREATED state and progresses through the payment lifecycle via event-driven orchestration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payment request data", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error during payment creation", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Payment> createPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payment creation request containing amount, currency, and party identifiers", required = true, content = @Content(schema = @Schema(implementation = CreatePaymentRequest.class))) @RequestBody CreatePaymentRequest request) {
        Payment payment = paymentService.createPayment(request);
        return ResponseEntity.status(201).body(payment);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Retrieve payment details", description = "Fetches the current state and details of a payment transaction by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment details retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found with the specified ID", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error during payment retrieval", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Payment> getPayment(
            @Parameter(description = "Unique identifier of the payment transaction", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }
}
