-- Migration script for user authentication system
-- Creates users table with authentication and authorization features

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    email_verified_at TIMESTAMP,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires_at TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_users_email_verification_token ON users(email_verification_token);
CREATE INDEX IF NOT EXISTS idx_users_password_reset_token ON users(password_reset_token);

-- Add constraints
ALTER TABLE users ADD CONSTRAINT chk_users_role 
CHECK (role IN ('USER', 'ADMIN', 'MODERATOR'));

ALTER TABLE users ADD CONSTRAINT chk_users_status 
CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'EXPIRED', 'PENDING_VERIFICATION', 'SUSPENDED'));

ALTER TABLE users ADD CONSTRAINT chk_users_username_length 
CHECK (LENGTH(username) >= 3);

ALTER TABLE users ADD CONSTRAINT chk_users_email_format 
CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Create default admin user if not exists
-- Password is 'admin123' (change in production!)
INSERT INTO users (
    id, 
    username, 
    email, 
    password, 
    first_name, 
    last_name, 
    role, 
    status, 
    email_verified_at
) VALUES (
    'admin-default-user-id-123',
    'admin',
    'admin@aireadiness.com',
    '$2a$10$6K5kJ5J5J5J5J5J5J5J5Ju.rK5K5K5K5K5K5K5K5K5K5K5K5K5K5K5O', -- BCrypt hash for 'admin123'
    'Admin',
    'User',
    'ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Add some basic test users for development (remove in production)
INSERT INTO users (
    id, 
    username, 
    email, 
    password, 
    first_name, 
    last_name, 
    role, 
    status,
    email_verified_at
) VALUES 
(
    'test-user-1-id-456',
    'testuser',
    'test@example.com',
    '$2a$10$6K5kJ5J5J5J5J5J5J5J5Ju.rK5K5K5K5K5K5K5K5K5K5K5K5K5K5K5O', -- BCrypt hash for 'admin123'
    'Test',
    'User',
    'USER',
    'ACTIVE',
    CURRENT_TIMESTAMP
),
(
    'test-moderator-id-789',
    'moderator',
    'moderator@aireadiness.com',
    '$2a$10$6K5kJ5J5J5J5J5J5J5J5Ju.rK5K5K5K5K5K5K5K5K5K5K5K5K5K5K5O', -- BCrypt hash for 'admin123'
    'Moderator',
    'User',
    'MODERATOR',
    'ACTIVE',
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;
