CREATE TABLE IF NOT EXISTS ledger_entries (
    id UUID PRIMARY KEY,
    payment_id VARCHAR(255) NOT NULL,
    debit_wallet_id VARCHAR(255) NOT NULL,
    credit_wallet_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    entry_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(255),
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ledger_payment ON ledger_entries(payment_id);
