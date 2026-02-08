# Event Driven Payments

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green)
![Kafka](https://img.shields.io/badge/Kafka-3.6-black)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

A distributed, event-driven Java Spring Boot microservices payment platform implementing a state machine, Kafka messaging, and Domain-Driven Design (DDD) patterns.

## Architecture

The system uses a choreography-based Saga pattern where services react to events.

```mermaid
graph TD
    User((User)) -->|POST /payments| P[Payment Service]
    P -->|PaymentCreated| K{Kafka}
    K -->|PaymentCreated| W[Wallet Service]
    W -->|FundsReserved| K
    K -->|FundsReserved| P
    P -->|PaymentAuthorized| K
    K -->|PaymentAuthorized| L[Ledger Service]
    L -->|TransactionRecorded| K
    K -->|TransactionRecorded| P
    P -->|PaymentCompleted| N[Notification Service]
```

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
