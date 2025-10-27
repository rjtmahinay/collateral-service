-- Create Collateral table
CREATE TABLE IF NOT EXISTS collateral (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collateral_id VARCHAR(255) NOT NULL UNIQUE,
    customer_id VARCHAR(255) NOT NULL,
    account_id VARCHAR(255),
    type VARCHAR(50) NOT NULL,
    description TEXT,
    estimated_value DECIMAL(19,2),
    market_value DECIMAL(19,2),
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(50) NOT NULL,
    location VARCHAR(255),
    evaluation_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    available_value DECIMAL(19,2),
    encumbered_value DECIMAL(19,2) DEFAULT 0,
    legal_description TEXT,
    ownership_documents TEXT,
    last_inspection_date TIMESTAMP,
    risk_rating VARCHAR(50)
);

-- Create Encumbrance table
CREATE TABLE IF NOT EXISTS encumbrance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    encumbrance_id VARCHAR(255) NOT NULL UNIQUE,
    collateral_id VARCHAR(255) NOT NULL,
    loan_id VARCHAR(255),
    customer_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    effective_date TIMESTAMP,
    expiry_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    description TEXT,
    priority INTEGER DEFAULT 1,
    legal_reference VARCHAR(255),
    notes TEXT,
    FOREIGN KEY (collateral_id) REFERENCES collateral(collateral_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_collateral_customer_id ON collateral(customer_id);
CREATE INDEX IF NOT EXISTS idx_collateral_account_id ON collateral(account_id);
CREATE INDEX IF NOT EXISTS idx_collateral_status ON collateral(status);
CREATE INDEX IF NOT EXISTS idx_collateral_type ON collateral(type);

CREATE INDEX IF NOT EXISTS idx_encumbrance_collateral_id ON encumbrance(collateral_id);
CREATE INDEX IF NOT EXISTS idx_encumbrance_customer_id ON encumbrance(customer_id);
CREATE INDEX IF NOT EXISTS idx_encumbrance_loan_id ON encumbrance(loan_id);
CREATE INDEX IF NOT EXISTS idx_encumbrance_status ON encumbrance(status);
CREATE INDEX IF NOT EXISTS idx_encumbrance_effective_date ON encumbrance(effective_date);
CREATE INDEX IF NOT EXISTS idx_encumbrance_expiry_date ON encumbrance(expiry_date);

-- Create AutoValuation table
CREATE TABLE IF NOT EXISTS auto_valuation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valuation_id VARCHAR(255) NOT NULL UNIQUE,
    collateral_id VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    location VARCHAR(255),
    description TEXT,
    status VARCHAR(50) NOT NULL,
    estimated_value DECIMAL(19,2),
    low_range DECIMAL(19,2),
    high_range DECIMAL(19,2),
    currency VARCHAR(3) DEFAULT 'USD',
    methodology VARCHAR(255),
    confidence_score DECIMAL(5,4),
    valuation_date TIMESTAMP,
    request_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    message TEXT,
    FOREIGN KEY (collateral_id) REFERENCES collateral(collateral_id)
);

-- Create TitleRegistry table
CREATE TABLE IF NOT EXISTS title_registry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title_id VARCHAR(255) NOT NULL UNIQUE,
    collateral_id VARCHAR(255) NOT NULL,
    title_number VARCHAR(255),
    legal_description TEXT,
    status VARCHAR(50) NOT NULL,
    current_owner VARCHAR(255),
    previous_owner VARCHAR(255),
    registration_date TIMESTAMP,
    is_valid BOOLEAN DEFAULT FALSE,
    verification_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    message TEXT,
    notes TEXT,
    FOREIGN KEY (collateral_id) REFERENCES collateral(collateral_id)
);

-- Create indexes for AutoValuation table
CREATE INDEX IF NOT EXISTS idx_auto_valuation_collateral_id ON auto_valuation(collateral_id);
CREATE INDEX IF NOT EXISTS idx_auto_valuation_type ON auto_valuation(type);
CREATE INDEX IF NOT EXISTS idx_auto_valuation_location ON auto_valuation(location);
CREATE INDEX IF NOT EXISTS idx_auto_valuation_status ON auto_valuation(status);
CREATE INDEX IF NOT EXISTS idx_auto_valuation_valuation_date ON auto_valuation(valuation_date);
CREATE INDEX IF NOT EXISTS idx_auto_valuation_request_date ON auto_valuation(request_date);

-- Create indexes for TitleRegistry table
CREATE INDEX IF NOT EXISTS idx_title_registry_collateral_id ON title_registry(collateral_id);
CREATE INDEX IF NOT EXISTS idx_title_registry_title_number ON title_registry(title_number);
CREATE INDEX IF NOT EXISTS idx_title_registry_current_owner ON title_registry(current_owner);
CREATE INDEX IF NOT EXISTS idx_title_registry_status ON title_registry(status);
CREATE INDEX IF NOT EXISTS idx_title_registry_is_valid ON title_registry(is_valid);
CREATE INDEX IF NOT EXISTS idx_title_registry_verification_date ON title_registry(verification_date);
