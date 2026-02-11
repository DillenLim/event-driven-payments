# Payment State Machine

The Payment Service enforces a strict state machine to manage the lifecycle of a payment. All transitions are validated by `PaymentStateTransitionService`.

## States

| State | Description |
| :--- | :--- |
| `CREATED` | Initial state when a payment request is received. |
| `FUNDS_RESERVED` | Wallet has successfully reserved funds. |
| `AUTHORIZATION_IN_PROGRESS` | Intermediate state while authorization logic runs. |
| `AUTHORIZED` | Payment is authorized and ready for settlement. |
| `COMPLETED` | Transaction recorded in Ledger, payment finalized. |
| `FAILED` | Payment failed due to insufficient funds or error. |
| `CANCELLED` | Payment was cancelled by user or system. |
| `EXPIRED` | Payment timed out (optional logic). |

## Valid Transitions

The following transitions are strictly enforced:

- `CREATED` -> `FUNDS_RESERVED`
- `FUNDS_RESERVED` -> `AUTHORIZATION_IN_PROGRESS`
- `FUNDS_RESERVED` -> `FAILED`
- `FUNDS_RESERVED` -> `CANCELLED`
- `FUNDS_RESERVED` -> `EXPIRED`
- `AUTHORIZATION_IN_PROGRESS` -> `AUTHORIZED`
- `AUTHORIZED` -> `COMPLETED`

Any attempt to transition outside these paths will result in an `IllegalStateException`.
