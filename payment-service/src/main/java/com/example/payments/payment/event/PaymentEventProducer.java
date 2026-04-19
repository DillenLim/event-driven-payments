package com.example.payments.payment.event;

import com.example.payments.events.PaymentCompletedEvent;
import com.example.payments.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Producer for Payment lifecycle events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "payments.lifecycle";

    public void emitEvent(String topic, String key, Object event) {
        log.info("Emitting event: {} to topic: {}", event, topic);
        kafkaTemplate.send(topic, key, event);
    }

    public void emitPaymentCompleted(String paymentId) {
        log.info("Emitting PaymentCompletedEvent for payment: {}", paymentId);
        
        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .eventType("PaymentCompletedEvent")
                .paymentId(paymentId)
                .completedAt(Instant.now())
                .build();
        
        var message = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY, paymentId)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("eventType", event.getClass().getSimpleName())
                .build();
        
        kafkaTemplate.send(message);
        log.info("PaymentCompletedEvent sent for payment: {}", paymentId);
    }

    public void emitPaymentFailed(String paymentId, String reason) {
        log.info("Emitting PaymentFailedEvent for payment: {}", paymentId);
        
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .eventType("PaymentFailedEvent")
                .paymentId(paymentId)
                .reason(reason)
                .failedAt(Instant.now())
                .build();
        
        var message = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY, paymentId)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("eventType", event.getClass().getSimpleName())
                .build();
        
        kafkaTemplate.send(message);
        log.info("PaymentFailedEvent sent for payment: {} with reason: {}", paymentId, reason);
    }
}
