# Events

This system uses Kafka for asynchronous event-driven communication. All events follow a standard schema and are emitted to the `payments.lifecycle` topic.

## Event Schema

All events extend `BaseEvent` and contain:
- `eventId`: Unique UUID
- `eventType`: String (e.g., `PaymentCreatedEvent`)
- `aggregateId`: The ID of the primary entity (e.g., paymentId)
- `timestamp`: UTC timestamp
- `payload`: Specific event data

## Supported Events

### PaymentCreatedEvent
- **Trigger**: New payment request received.
- **Producer**: Payment Service
- **Consumer**: Wallet Service
- **Action**: Reserves funds.

### FundsReservedEvent
- **Trigger**: Funds successfully reserved.
- **Producer**: Wallet Service
- **Consumer**: Payment Service
- **Action**: Transitions payment state to `FUNDS_RESERVED` -> `AUTHORIZATION_IN_PROGRESS` -> `AUTHORIZED`.

### PaymentAuthorizedEvent
- **Trigger**: Payment authorization logic successful.
- **Producer**: Payment Service
- **Consumer**: Ledger Service
- **Action**: Records transaction in immutable ledger.

### TransactionRecordedEvent
- **Trigger**: Ledger entry successfully created.
- **Producer**: Ledger Service
- **Consumer**: Payment Service
- **Action**: Transitions payment state to `COMPLETED`.

### PaymentCompletedEvent
- **Trigger**: Payment fully settled.
- **Producer**: Payment Service
- **Consumer**: Notification Service
- **Action**: Sends confirmation email/SMS.

---

## Failure Handling Events

The system implements **compensating transactions** to handle failures gracefully.

### FundsReservationFailedEvent
- **Trigger**: Insufficient funds or wallet service unable to reserve funds.
- **Producer**: Wallet Service
- **Consumer**: Payment Service
- **Action**: Transitions payment state to `FAILED`.

### PaymentFailedEvent
- **Trigger**: Any step in the saga fails (funds reservation, ledger recording).
- **Producer**: Payment Service
- **Consumer**: None (informational event for audit logs/notifications)
- **Action**: Records failure reason.

### PaymentCancelledEvent
- **Trigger**: Payment cancelled after funds were reserved.
- **Producer**: Payment Service
- **Consumer**: Wallet Service
- **Action**: Releases (unreserves) funds back to the wallet as a **compensating transaction**.

---

## Compensating Transaction Flow

When a payment fails after funds are reserved:
1.  Payment Service transitions to `FAILED` or `CANCELLED`.
2.  Payment Service emits `PaymentCancelledEvent` with `walletId`, `amount`, and `currency`.
3.  Wallet Service consumes the event and calls `releaseFunds()` to add the amount back to the wallet balance.
