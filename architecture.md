# Architecture

## Domain Driven Design
The project is split into bounded contexts:
- Payment
- Wallet
- Ledger
- Notification

## Event Driven
We use Kafka for async communication.
- `payments.lifecycle`: Key topic for payment state changes.

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
