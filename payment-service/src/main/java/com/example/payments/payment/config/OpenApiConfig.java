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
                                                .title("Payment Service API")
                                                .description("""
                                                                ## Overview
                                                                Enterprise-grade payment processing microservice implementing event-driven architecture and the Saga pattern.

                                                                ## Key Capabilities
                                                                - **Idempotent Processing**: Prevents duplicate event processing through unique event tracking
                                                                - **State Machine Enforcement**: Ensures valid payment state transitions
                                                                - **Compensating Transactions**: Automated rollback mechanisms for failure scenarios
                                                                - **Event-Driven Integration**: Asynchronous communication via Apache Kafka
                                                                - **ACID Compliance**: Transactional consistency across distributed operations

                                                                ## Payment Lifecycle
                                                                1. **Initiation** - Payment request submitted (`CREATED`)
                                                                2. **Reservation** - Funds reserved in wallet (`FUNDS_RESERVED`)
                                                                3. **Authorization** - Payment authorized (`AUTHORIZED`)
                                                                4. **Settlement** - Transaction recorded in ledger (`COMPLETED`)

                                                                ## Service Dependencies
                                                                - **Wallet Service**: Fund reservation and settlement operations
                                                                - **Ledger Service**: Immutable transaction audit trail
                                                                - **Notification Service**: Event notifications and alerts

                                                                ## Technical Stack
                                                                - Spring Boot 3.2.1 | Java 17 | PostgreSQL | Apache Kafka 3.4
                                                                """)
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Engineering Team")
                                                                .email("engineering@company.com")
                                                                .url("https://github.com/DillenLim/event-driven-payments"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")));
        }
}
