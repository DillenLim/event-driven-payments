# Architecture

## Domain Driven Design
The project is split into bounded contexts:
- Payment
- Wallet
- Ledger
- Notification

### Code Sharing Strategy
**Shared infrastructure** (common module):
- `ProcessedEvent`: Idempotency tracking
- Build-time dependency only

**Separate domain logic** (per service):
- Entities, repositories, services
- Event schemas (duplicated for autonomy)

This hybrid approach preserves bounded context independence while reducing infrastructure duplication.

## Event Driven
We use Kafka for async communication.
- `payments.lifecycle`: Key topic for payment state changes.

## Saga Flow
The following sequence diagram illustrates the choreography-based Saga pattern used in this project:

```mermaid
sequenceDiagram
    participant U as User
    participant P as Payment Service
    participant K as Kafka
    participant W as Wallet Service
    participant L as Ledger Service
    participant N as Notification Service

    U->>P: POST /payments
    P->>P: Validate & Create Payment (PENDING)
    P->>K: Publish PaymentCreatedEvent
    K->>W: Consume PaymentCreatedEvent
    W->>W: Reserve Funds
    W->>K: Publish FundsReservedEvent
    K->>P: Consume FundsReservedEvent
    P->>P: Update State (AUTHORIZATION_IN_PROGRESS)
    P->>P: Update State (AUTHORIZED)
    P->>K: Publish PaymentAuthorizedEvent
    K->>L: Consume PaymentAuthorizedEvent
    L->>L: Record Transaction
    L->>K: Publish TransactionRecordedEvent
    K->>P: Consume TransactionRecordedEvent
    P->>P: Update State (COMPLETED)
    P->>K: Publish PaymentCompletedEvent
    K->>N: Consume PaymentCompletedEvent
    N->>U: Send Email Notification
```

## State Machine
Payment States:
- CREATED
- FUNDS_RESERVED
- AUTHORIZATION_IN_PROGRESS
- AUTHORIZED
- COMPLETED
- FAILED
- CANCELLED
- EXPIRED

Transitions are enforced by `PaymentStateTransitionService`.

## Failure Handling & Compensating Transactions

The system implements **compensating transactions** to handle failures at each step of the Saga:

### Failure Scenarios

| Scenario | Trigger | Compensating Action |
| :--- | :--- | :--- |
| **Insufficient Funds** | Wallet cannot reserve funds | Emit `FundsReservationFailedEvent` → Payment transitions to `FAILED` |
| **Ledger Failure** | Ledger fails to record transaction | Emit `PaymentCancelledEvent` → Wallet releases reserved funds |
| **User Cancellation** | Payment cancelled after reservation | Emit `PaymentCancelledEvent` → Wallet releases funds |

### Key State Transitions for Failure
- `CREATED` → `FAILED` (if funds reservation fails)
- `AUTHORIZATION_IN_PROGRESS` → `FAILED` (if authorization logic fails)
- `FUNDS_RESERVED` → `CANCELLED` (user-initiated cancellation)
