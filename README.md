# Event Driven Payments

A distributed, event-driven Java Spring Boot microservices payment platform implementing a state machine, Kafka messaging, and DDD patterns.

## Services
- **Payment Service**: Manages payment lifecycle and state machine.
- **Wallet Service**: Manages reserved funds and balances.
- **Ledger Service**: Immutable record of all transactions.
- **Notification Service**: Sends alerts based on payment events.

## Architecture
The system uses a choreography-based Saga pattern. Services communicate via Kafka.
See [architecture.md](architecture.md) for details.

## Running Locally
```bash
docker-compose up -d
```
## API
POST /payments
GET /payments/{id}