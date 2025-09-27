-- SQL script to create an admin user and approve it
-- This script assumes the tables already exist (created by JPA/Hibernate)

-- First, ensure the ADMIN role exists (it should already be there from data.sql)
-- If not, uncomment the following line:
-- INSERT INTO roles (name, created_at, updated_at) VALUES ('ADMIN', NOW(), NOW()) ON CONFLICT (name) DO NOTHING;

-- Get the ADMIN role ID
DO $$
DECLARE
    admin_role_id BIGINT;
BEGIN
    SELECT id INTO admin_role_id FROM roles WHERE name = 'ADMIN';
    
    IF admin_role_id IS NULL THEN
        RAISE EXCEPTION 'ADMIN role not found. Please ensure the ADMIN role exists in the roles table.';
    END IF;
    
    -- Create admin user
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
        admin_role_id,                       -- role_id (ADMIN)
        NOW(),                               -- created_at
        NOW()                                -- updated_at
    ) ON CONFLICT (email) DO UPDATE SET
        name = EXCLUDED.name,
        password_digest = EXCLUDED.password_digest,
        mobile_number = EXCLUDED.mobile_number,
        status = EXCLUDED.status,
        updated_at = NOW();
    
    RAISE NOTICE 'Admin user created/updated successfully with email: admin@eone.com';
    RAISE NOTICE 'Password: admin123';
    RAISE NOTICE 'Status: Pending approval (0)';
END $$;

-- Now approve the admin user
UPDATE users 
SET status = 1, updated_at = NOW() 
WHERE email = 'admin@eone.com' AND status = 0;

-- Verify the admin user was created and approved
SELECT 
    u.id,
    u.email,
    u.name,
    u.status,
    r.name as role_name,
    u.created_at,
    u.updated_at
FROM users u
JOIN roles r ON u.role_id = r.id
WHERE u.email = 'admin@eone.com';

-- Show all admin users
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
WHERE r.name = 'ADMIN';
