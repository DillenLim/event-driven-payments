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
