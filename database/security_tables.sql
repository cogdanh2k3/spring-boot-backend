-- Add security-related tables for EduQuizz Backend
-- Author: Generated for security features implementation
-- Date: 2025-01-19

-- Table for Password Reset PINs
CREATE TABLE IF NOT EXISTS password_reset_pins (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    pin VARCHAR(6) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_email (email),
    INDEX idx_pin (pin)
);

-- Table for Login Attempts (Brute-force protection)
CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGSERIAL PRIMARY KEY,
    ip_address VARCHAR(50) NOT NULL,
    username VARCHAR(255) NOT NULL,
    attempt_time TIMESTAMP NOT NULL,
    success BOOLEAN NOT NULL,
    INDEX idx_ip_time (ip_address, attempt_time),
    INDEX idx_user_time (username, attempt_time)
);

-- Comments for documentation
COMMENT ON TABLE password_reset_pins IS 'Stores temporary PINs for password reset functionality';
COMMENT ON TABLE login_attempts IS 'Tracks login attempts for brute-force protection';

COMMENT ON COLUMN password_reset_pins.pin IS '6-digit PIN sent to user email';
COMMENT ON COLUMN password_reset_pins.expires_at IS 'PIN expires 15 minutes after creation';
COMMENT ON COLUMN password_reset_pins.used IS 'Prevents PIN reuse';

COMMENT ON COLUMN login_attempts.ip_address IS 'Client IP address for rate limiting';
COMMENT ON COLUMN login_attempts.success IS 'Whether login attempt was successful';
