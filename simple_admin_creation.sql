-- Simple SQL script to create and approve an admin user
-- Run this in your PostgreSQL database

-- Step 1: Create the admin user (status = 0 for pending approval)
INSERT INTO users (
    email, 
    name, 
    password_digest, 
    mobile_number, 
    status, 
    date_of_birth, 
    role_id, 
    created_at, 
    updated_at
) VALUES (
    'admin@eone.com',                    -- email
    'System Administrator',               -- name
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- password: admin123
    '+1234567890',                       -- mobile_number
    0,                                   -- status: 0 = pending approval
    '1990-01-01',                        -- date_of_birth
    (SELECT id FROM roles WHERE name = 'ADMIN'), -- role_id
    NOW(),                               -- created_at
    NOW()                                -- updated_at
) ON CONFLICT (email) DO UPDATE SET
    name = EXCLUDED.name,
    password_digest = EXCLUDED.password_digest,
    mobile_number = EXCLUDED.mobile_number,
    status = EXCLUDED.status,
    updated_at = NOW();

-- Step 2: Approve the admin user (change status from 0 to 1)
UPDATE users 
SET status = 1, updated_at = NOW() 
WHERE email = 'admin@eone.com';

-- Step 3: Verify the admin user was created and approved
SELECT 
    u.id,
    u.email,
    u.name,
    u.status,
    CASE 
        WHEN u.status = 0 THEN 'Pending'
        WHEN u.status = 1 THEN 'Approved'
        WHEN u.status = 2 THEN 'Rejected'
        ELSE 'Unknown'
    END as status_text,
    r.name as role_name,
    u.created_at,
    u.updated_at
FROM users u
JOIN roles r ON u.role_id = r.id
WHERE u.email = 'admin@eone.com';
