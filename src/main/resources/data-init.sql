-- AI Crop Advisory Platform — Database Initialization
-- Run this after the application creates tables (spring.jpa.hibernate.ddl-auto=update)

-- Create default admin and farmer accounts
-- Passwords are BCrypt-hashed: 'admin123' and 'farmer123'
INSERT IGNORE INTO users (username, email, password, full_name, phone, village, district, state, land_area_acres, role, enabled, created_at)
VALUES
('admin', 'admin@krishimitra.in',
 '$2a$10$N7K.8lkD/RCuI/FoqHpyLOhE4WtKbCF9pJ6zV1WjHMfpUoqH4.T2G',
 'System Administrator', '9999999999', 'N/A', 'N/A', 'N/A', 0, 'ADMIN', 1, NOW()),
('farmer1', 'farmer1@example.com',
 '$2a$10$rT5hpNsXJsLgH6L2QF7c2OEsupFGcvNqrHyJsMn4E0S1jKLQvUdYq',
 'Ramesh Kumar', '9876543210', 'Mandya Village', 'Mandya', 'Karnataka', 5.5, 'FARMER', 1, NOW()),
('farmer2', 'farmer2@example.com',
 '$2a$10$rT5hpNsXJsLgH6L2QF7c2OEsupFGcvNqrHyJsMn4E0S1jKLQvUdYq',
 'Priya Devi', '9876543211', 'Nellore Village', 'Nellore', 'Andhra Pradesh', 3.0, 'FARMER', 1, NOW());

-- Note: BCrypt hash for 'admin123' and 'farmer123' may vary by implementation.
-- The application will auto-register users via the /auth/register endpoint.
-- These seeds are optional — the app works without them.
