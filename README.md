# Event Driven Payments

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green)
![Kafka](https://img.shields.io/badge/Kafka-3.6-black)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

A distributed, event-driven Java Spring Boot microservices payment platform implementing a state machine, Kafka messaging, and Domain-Driven Design (DDD) patterns.

## Architecture

The system uses a choreography-based Saga pattern where services react to events.

```mermaid
flowchart TD
    User((User)) -->|POST /payments| P[Payment Service]
    P -->|PaymentCreatedEvent| K{Kafka}
    K -->|PaymentCreatedEvent| W[Wallet Service]
    
    %% Success Path
    W -->|FundsReservedEvent SUCCESS| K
    K -->|FundsReservedEvent| P
    P -->|PaymentAuthorizedEvent| K
    K -->|PaymentAuthorizedEvent| L[Ledger Service]
    L -->|TransactionRecordedEvent| K
    K -->|TransactionRecordedEvent| P
    P -->|PaymentCompletedEvent| N[Notification Service]
    
    %% Failure Path
    W -.->|FundsReservationFailedEvent| K
    K -.->|FundsReservationFailedEvent| P
    P -.->|PaymentFailedEvent| N
    
    %% Compensating Transaction
    P -.->|PaymentCancelledEvent| K
    K -.->|PaymentCancelledEvent| W
    
    classDef successPath fill:#4caf50,stroke:#2e7d32,color:#fff
    classDef failurePath fill:#f44336,stroke:#c62828,color:#fff
    
    style P fill:#2196f3,stroke:#1565c0,color:#fff
    style W fill:#2196f3,stroke:#1565c0,color:#fff
    style L fill:#2196f3,stroke:#1565c0,color:#fff
    style N fill:#ff9800,stroke:#e65100,color:#fff
    style K fill:#9c27b0,stroke:#6a1b9a,color:#fff
```

## Documentation

-   [Architecture Overview](architecture.md)
-   [Payment State Machine](state-machine.md)
-   [Event Catalog](events.md)

## Services

-   **Payment Service**: Manages payment lifecycle and state machine (Saga Orchestrator logic via choreography).
-   **Wallet Service**: Manages user pockets, balances, and fund reservations.
-   **Ledger Service**: Immutable double-entry ledger for all financial transactions.
-   **Notification Service**: Sends emails/alerts based on payment events.

## Prerequisites

-   Java 17+
-   Docker & Docker Compose
-   Maven 3.8+

## How to Run

1.  **Clone the repository**
    ```bash
    git clone https://github.com/yourusername/event-driven-payments.git
    cd event-driven-payments
    ```

2.  **Start Infrastructure & Services**
    ```bash
    docker-compose up -d --build
    ```

3.  **Access APIs**
    -   **Payment Service**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
    -   **Wallet Service**: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
    -   **Ledger Service**: [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html)

## Testing

To run the end-to-end regression tests (requires Docker):

```bash
docker run --rm -v "$(pwd)":/usr/src/app -w /usr/src/app/payment-service maven:3.8-openjdk-17 mvn test -Dtest=PaymentIntegrationTest
```

## API Usage

### Create a Payment

```bash
curl -X POST http://localhost:8081/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "currency": "USD",
    "debitorId": "user-123",
    "beneficiaryId": "merchant-456"
  }'
```

### Check Payment Status

```bash
curl http://localhost:8081/payments/{paymentId}
```
