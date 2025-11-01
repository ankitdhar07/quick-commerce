-- Create order sequence
CREATE SEQUENCE order_sequence START WITH 1 INCREMENT BY 1;

-- Create orders table
CREATE TABLE orders (
    id BIGINT PRIMARY KEY DEFAULT nextval ('order_sequence'),
    order_number VARCHAR(100) NOT NULL UNIQUE,
    customer_id VARCHAR(100) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    shipping_address TEXT NOT NULL,
    billing_address TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_order_number ON orders (order_number);

CREATE INDEX idx_customer_id ON orders (customer_id);

CREATE INDEX idx_status ON orders (status);

CREATE INDEX idx_created_at ON orders (created_at);

-- Create order_items table
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY DEFAULT nextval ('order_sequence'),
    order_id BIGINT NOT NULL REFERENCES orders (id),
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);

CREATE INDEX idx_order_items_product_id ON order_items (product_id);