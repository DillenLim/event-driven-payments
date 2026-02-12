package com.example.payments.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for the Payment Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("💳 Event-Driven Payment Service API")
                        .description(
                                """
                                        **Event-Driven Payment Service** - A production-ready microservice implementing the Saga pattern.

                                        ### Features
                                        - ✅ **Idempotent Event Processing** - Prevents duplicate processing
                                        - ✅ **State Machine Transitions** - Enforces valid payment flows
                                        - ✅ **Compensating Transactions** - Handles failures gracefully
                                        - ✅ **Kafka Event Streaming** - Asynchronous event-driven architecture

                                        ### Payment Flow
                                        1. Create Payment → `CREATED`
                                        2. Reserve Funds → `FUNDS_RESERVED`
                                        3. Authorize Payment → `AUTHORIZED`
                                        4. Record Transaction → `COMPLETED`

                                        ### Architecture
                                        This service orchestrates payments through an event-driven saga, coordinating with:
                                        - **Wallet Service** - Fund reservation and deduction
                                        - **Ledger Service** - Immutable transaction recording
                                        - **Notification Service** - User notifications
                                        """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Payment Service Team")
                                .email("payments@example.com")
                                .url("https://github.com/DillenLim/event-driven-payments"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
