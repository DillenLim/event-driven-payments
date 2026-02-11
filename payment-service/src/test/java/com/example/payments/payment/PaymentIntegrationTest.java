package com.example.payments.payment;

import com.example.payments.payment.domain.PaymentState;
import com.example.payments.payment.web.CreatePaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private com.example.payments.payment.repository.PaymentRepository paymentRepository;

    @Test
    void createPayment_ShouldReturn201() throws Exception {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .amount(new BigDecimal("500.00"))
                .currency("EUR")
                .debitorId("alice")
                .beneficiaryId("bob")
                .build();

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.state").value(PaymentState.CREATED.name()));
    }

    @Test
    void testFullPaymentSaga() throws Exception {
        // 1. Create Payment
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .debitorId("saga-user")
                .beneficiaryId("saga-merchant")
                .build();

        String responseJson = mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        com.example.payments.payment.domain.Payment payment = objectMapper.readValue(responseJson,
                com.example.payments.payment.domain.Payment.class);
        String paymentId = payment.getId().toString();

        // 2. Simulate FundsReservedEvent (from Wallet Service)
        com.example.payments.payment.event.FundsReservedEvent fundsReservedEvent = new com.example.payments.payment.event.FundsReservedEvent(
                paymentId,
                new BigDecimal("100.00"),
                "USD",
                "SUCCESS");
        kafkaTemplate.send("payments.lifecycle", objectMapper.writeValueAsString(fundsReservedEvent));

        // Wait for AUTHORIZED state
        awaitPaymentState(payment.getId(), PaymentState.AUTHORIZED);

        // 3. Simulate TransactionRecordedEvent (from Ledger Service)
        com.example.payments.payment.event.TransactionRecordedEvent transactionRecordedEvent = new com.example.payments.payment.event.TransactionRecordedEvent(
                paymentId,
                java.util.UUID.randomUUID().toString(),
                "SUCCESS");
        kafkaTemplate.send("payments.lifecycle", objectMapper.writeValueAsString(transactionRecordedEvent));

        // Wait for COMPLETED state
        awaitPaymentState(payment.getId(), PaymentState.COMPLETED);
    }

    private void awaitPaymentState(java.util.UUID paymentId, PaymentState expectedState) throws InterruptedException {
        int maxRetries = 10;
        for (int i = 0; i < maxRetries; i++) {
            com.example.payments.payment.domain.Payment p = paymentRepository.findById(paymentId).orElseThrow();
            if (p.getState() == expectedState) {
                return;
            }
            Thread.sleep(500);
        }
        throw new AssertionError("Payment did not reach state " + expectedState + " within timeout");
    }
}
