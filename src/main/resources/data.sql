-- Sample collateral data
INSERT INTO collateral (collateral_id, customer_id, account_id, type, description, estimated_value, market_value, currency, status, location, evaluation_date, created_by, available_value, encumbered_value, legal_description, risk_rating) VALUES
('COL-001', 'CUST-001', 'ACC-001', 'REAL_ESTATE', 'Single Family Home - Main Street', 350000.00, 340000.00, 'USD', 'AVAILABLE', '123 Main Street, Anytown, ST', '2024-01-15 10:00:00', 'system', 340000.00, 0.00, 'Lot 15, Block 3, Maple Grove Subdivision', 'LOW'),
('COL-002', 'CUST-001', 'ACC-002', 'VEHICLE', '2022 Toyota Camry LE', 28000.00, 26500.00, 'USD', 'AVAILABLE', 'Customer Garage', '2024-02-01 14:30:00', 'system', 26500.00, 0.00, 'VIN: 4T1G11AK2NU123456', 'MEDIUM'),
('COL-003', 'CUST-002', 'ACC-003', 'EQUIPMENT', 'Industrial Printing Press', 75000.00, 70000.00, 'USD', 'ENCUMBERED', 'Factory Floor A', '2024-01-20 09:15:00', 'system', 20000.00, 50000.00, 'Heidelberg Speedmaster 52-4 Offset Press', 'MEDIUM'),
('COL-004', 'CUST-003', 'ACC-004', 'REAL_ESTATE', 'Commercial Building - Downtown', 850000.00, 825000.00, 'USD', 'AVAILABLE', '456 Business Ave, Downtown, ST', '2024-02-10 11:00:00', 'system', 825000.00, 0.00, '5-story office building, 15,000 sq ft total', 'LOW'),
('COL-005', 'CUST-002', 'ACC-005', 'INVENTORY', 'Electronics Inventory', 45000.00, 42000.00, 'USD', 'AVAILABLE', 'Warehouse B', '2024-02-05 16:20:00', 'system', 42000.00, 0.00, 'Consumer electronics and accessories', 'HIGH');

-- Sample encumbrance data
INSERT INTO encumbrance (encumbrance_id, collateral_id, loan_id, customer_id, amount, currency, type, status, effective_date, expiry_date, created_by, description, priority, legal_reference) VALUES
('ENC-001', 'COL-003', 'LOAN-001', 'CUST-002', 50000.00, 'USD', 'LIEN', 'ACTIVE', '2024-01-25 10:00:00', '2026-01-25 10:00:00', 'system', 'First lien on printing equipment for business loan', 1, 'UCC-1 Filing #2024-001234'),
('ENC-002', 'COL-001', 'LOAN-002', 'CUST-001', 280000.00, 'USD', 'MORTGAGE', 'ACTIVE', '2023-12-01 09:00:00', '2053-12-01 09:00:00', 'system', 'Primary mortgage on residential property', 1, 'Deed of Trust #2023-DT-5678'),
('ENC-003', 'COL-004', 'LOAN-003', 'CUST-003', 600000.00, 'USD', 'MORTGAGE', 'ACTIVE', '2024-01-15 14:00:00', '2044-01-15 14:00:00', 'system', 'Commercial mortgage on downtown building', 1, 'Commercial Deed of Trust #2024-CDT-9012');
