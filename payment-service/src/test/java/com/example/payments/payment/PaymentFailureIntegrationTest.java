package com.example.payments.payment;

import com.example.payments.payment.domain.Payment;
import com.example.payments.payment.domain.PaymentState;
import com.example.payments.payment.event.FundsReservationFailedEvent;
import com.example.payments.payment.event.PaymentEventProducer;
import com.example.payments.payment.service.PaymentService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for payment failure scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PaymentFailureIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @Test
    void testInsufficientFundsScenario() throws Exception {
        // Create payment
        MvcResult result = mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"amount\": 100.0, \"currency\": \"USD\", \"debitorId\": \"debitor123\", \"beneficiaryId\": \"beneficiary456\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        String paymentId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        // Simulate FundsReservationFailedEvent (insufficient funds)
        FundsReservationFailedEvent failedEvent = new FundsReservationFailedEvent(paymentId, "Insufficient funds");
        paymentEventProducer.emitEvent("payments.lifecycle", paymentId, failedEvent);

        // Wait for payment to reach FAILED state
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Payment payment = paymentService.getPayment(UUID.fromString(paymentId));
            assertThat(payment.getState()).isEqualTo(PaymentState.FAILED);
        });
    }
}
