-- Create product sequence
CREATE SEQUENCE product_sequence START WITH 1 INCREMENT BY 1;

-- Create products table
CREATE TABLE products (
    id BIGINT PRIMARY KEY DEFAULT nextval ('product_sequence'),
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    category VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_sku ON products (sku);

CREATE INDEX idx_status ON products (status);

CREATE INDEX idx_category ON products (category);

CREATE INDEX idx_created_at ON products (created_at);

-- Create audit table for tracking changes
CREATE TABLE products_audit (
    id BIGINT PRIMARY KEY DEFAULT nextval ('product_sequence'),
    product_id BIGINT NOT NULL REFERENCES products (id),
    action VARCHAR(50) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_audit_product_id ON products_audit (product_id);

CREATE INDEX idx_products_audit_changed_at ON products_audit (changed_at);

-- Sample data
INSERT INTO
    products (
        sku,
        name,
        description,
        price,
        quantity,
        category,
        status
    )
VALUES (
        'LAPTOP-001',
        'Dell XPS 13',
        'High-performance ultrabook',
        999.99,
        50,
        'Electronics',
        'ACTIVE'
    ),
    (
        'PHONE-001',
        'iPhone 14',
        'Latest Apple smartphone',
        899.99,
        100,
        'Electronics',
        'ACTIVE'
    ),
    (
        'PHONE-002',
        'Samsung Galaxy S23',
        'Premium Android phone',
        799.99,
        75,
        'Electronics',
        'ACTIVE'
    ),
    (
        'TABLET-001',
        'iPad Pro',
        '12.9-inch tablet',
        1099.99,
        30,
        'Electronics',
        'ACTIVE'
    ),
    (
        'HEADPHONE-001',
        'Sony WH-1000XM5',
        'Noise-cancelling headphones',
        399.99,
        200,
        'Accessories',
        'ACTIVE'
    );