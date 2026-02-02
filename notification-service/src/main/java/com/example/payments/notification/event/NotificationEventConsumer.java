package com.example.payments.notification.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationEventConsumer {

    @KafkaListener(topics = "payments.lifecycle", groupId = "notification-service-group")
    public void onEvent(String event) {
        log.info("Sending notification for event: {}", event);
        // Implement email/SMS logic here
    }
}
