-- Create payment sequence
CREATE SEQUENCE payment_sequence START WITH 1 INCREMENT BY 1;

-- Create payments table
CREATE TABLE payments (
    id BIGINT PRIMARY KEY DEFAULT nextval ('payment_sequence'),
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    order_id BIGINT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'INITIATED',
    reference_number VARCHAR(100),
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_transaction_id ON payments (transaction_id);

CREATE INDEX idx_order_id ON payments (order_id);

CREATE INDEX idx_status ON payments (status);

CREATE INDEX idx_created_at ON payments (created_at);

-- Create payment_history table for tracking
CREATE TABLE payment_history (
    id BIGINT PRIMARY KEY DEFAULT nextval ('payment_sequence'),
    payment_id BIGINT NOT NULL REFERENCES payments (id),
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_history_payment_id ON payment_history (payment_id);