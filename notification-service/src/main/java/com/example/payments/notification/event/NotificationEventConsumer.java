package com.example.payments.notification.event;

import com.example.payments.events.BaseEvent;
import com.example.payments.events.PaymentCompletedEvent;
import com.example.payments.events.PaymentFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationEventConsumer {

    @KafkaListener(topics = "payments.lifecycle", groupId = "notification-service-group")
    public void onEvent(BaseEvent event) {
        if (event instanceof PaymentCompletedEvent completedEvent) {
            handlePaymentCompleted(completedEvent);
        } else if (event instanceof PaymentFailedEvent failedEvent) {
            handlePaymentFailed(failedEvent);
        }
        // Ignore all other event types (PaymentCreatedEvent, FundsReservedEvent, etc.)
    }

    private void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Payment {} completed successfully at {}", event.getPaymentId(), event.getCompletedAt());
        log.info("Sending success notification for payment {}", event.getPaymentId());
        // Placeholder for actual notification sending - email/SMS
    }

    private void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Payment {} failed: {} at {}", event.getPaymentId(), event.getReason(), event.getFailedAt());
        log.info("Sending failure notification for payment {}", event.getPaymentId());
        // Placeholder for actual notification sending
    }
}
