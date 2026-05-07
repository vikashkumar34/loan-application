-- =====================================================
-- MOCK DATA INSERTION
-- =====================================================

-- Clear existing data (optional - for fresh start)
-- DELETE FROM disbursements;
-- DELETE FROM loan_applications;
-- DELETE FROM users;

-- Insert admin user (password: admin)
INSERT INTO users (username, password, email, full_name, role)
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@loanapp.com', 'System Administrator', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Insert normal users (password: user@123)
INSERT INTO users (username, password, email, full_name, role)
VALUES
    ('user1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'user1@example.com', 'User One', 'USER'),
    ('user2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'user2@example.com', 'User Two', 'USER'),
    ('user3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'user3@example.com', 'User Three', 'USER')
ON CONFLICT (username) DO NOTHING;

-- Insert sample loan applications for users
INSERT INTO loan_applications (user_id, amount, term_months, purpose, bank_account_number, ifsc_code, status)
SELECT
    u.id,
    CASE
        WHEN u.username = 'user1' THEN 50000.00
        WHEN u.username = 'user2' THEN 75000.00
        WHEN u.username = 'user3' THEN 100000.00
    END,
    CASE
        WHEN u.username = 'user1' THEN 6
        WHEN u.username = 'user2' THEN 12
        WHEN u.username = 'user3' THEN 24
    END,
    CASE
        WHEN u.username = 'user1' THEN 'Education loan'
        WHEN u.username = 'user2' THEN 'Home renovation'
        WHEN u.username = 'user3' THEN 'Business expansion'
    END,
    CASE
        WHEN u.username = 'user1' THEN '1111111111'
        WHEN u.username = 'user2' THEN '2222222222'
        WHEN u.username = 'user3' THEN '3333333333'
    END,
    CASE
        WHEN u.username = 'user1' THEN 'SBI0001111'
        WHEN u.username = 'user2' THEN 'HDFC0002222'
        WHEN u.username = 'user3' THEN 'ICICI0003333'
    END,
    'SUBMITTED'
FROM users u
WHERE u.username IN ('user1', 'user2', 'user3')
ON CONFLICT DO NOTHING;

-- Update one application to APPROVED status
UPDATE loan_applications
SET status = 'APPROVED', approved_date = CURRENT_TIMESTAMP
WHERE user_id = (SELECT id FROM users WHERE username = 'user2')
AND status = 'SUBMITTED'
AND id = (SELECT MIN(id) FROM loan_applications WHERE user_id = (SELECT id FROM users WHERE username = 'user2'));

-- Update one application to REJECTED status
UPDATE loan_applications
SET status = 'REJECTED', rejected_date = CURRENT_TIMESTAMP, rejection_reason = 'Insufficient credit score'
WHERE user_id = (SELECT id FROM users WHERE username = 'user3')
AND status = 'SUBMITTED'
AND id = (SELECT MIN(id) FROM loan_applications WHERE user_id = (SELECT id FROM users WHERE username = 'user3'));

-- Insert a disbursement record for the approved application
INSERT INTO disbursements (loan_application_id, transaction_reference, requested_date, approved_date, disbursed_date, disbursed_by_admin, remarks)
SELECT
    la.id,
    '123456789012',
    la.submitted_date,
    la.approved_date,
    CURRENT_TIMESTAMP,
    'admin',
    'Auto-generated disbursement for testing'
FROM loan_applications la
WHERE la.status = 'APPROVED'
AND la.user_id = (SELECT id FROM users WHERE username = 'user2')
ON CONFLICT DO NOTHING;

-- Update the disbursed loan application
UPDATE loan_applications
SET status = 'DISBURSED', disbursed_date = CURRENT_TIMESTAMP, transaction_reference = '123456789012'
WHERE status = 'APPROVED'
AND user_id = (SELECT id FROM users WHERE username = 'user2');

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Check users
-- SELECT id, username, email, full_name, role, created_at FROM users ORDER BY id;

-- Check loan applications
-- SELECT la.id, u.username, la.amount, la.term_months, la.purpose, la.status, la.submitted_date
-- FROM loan_applications la
-- JOIN users u ON la.user_id = u.id
-- ORDER BY la.id;

-- Check disbursements
-- SELECT d.id, u.username, la.amount, d.transaction_reference, d.disbursed_date, d.disbursed_by_admin
-- FROM disbursements d
-- JOIN loan_applications la ON d.loan_application_id = la.id
-- JOIN users u ON la.user_id = u.id
-- ORDER BY d.id;
