# Event Driven Payments

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green)
![Kafka](https://img.shields.io/badge/Kafka-3.6-black)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

A distributed, event-driven Java Spring Boot microservices payment platform implementing a state machine, Kafka messaging, and Domain-Driven Design (DDD) patterns.

## Architecture

The system uses a choreography-based Saga pattern where services react to events.

```mermaid
graph LR
    User([User]) -->|POST /payments| PS[Payment Service]
    PS -->|1. PaymentCreated| K([Kafka])
    K -->|2. PaymentCreated| WS[Wallet Service]
    WS -->|3. FundsReserved| K
    K -->|4. FundsReserved| PS
    PS -->|5. PaymentAuthorized| K
    K -->|6. PaymentAuthorized| LS[Ledger Service]
    LS -->|7. TransactionRecorded| K
    K -->|8. TransactionRecorded| PS
    PS -->|9. PaymentCompleted| NS[Notification Service]
    
    WS -.->|Failure: FundsReservationFailed| K
    K -.->|Compensate: PaymentCancelled| WS
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
    -   **Payment Service**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
    -   **Wallet Service**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
    -   **Ledger Service**: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

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
