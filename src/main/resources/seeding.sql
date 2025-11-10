-- ====================================================================================
-- EONE DATABASE SEEDING - INITIAL DATA (DML ONLY)
-- ====================================================================================
-- This file contains ONLY initial data seeding (DML - Data Manipulation Language)
-- Location: src/main/resources/seeding.sql
-- 
-- PURPOSE:
--   - Populates the database with initial/seed data
--   - Does NOT contain any schema definitions (no CREATE TABLE statements)
--   - Use this file AFTER running migration.sql to populate initial data
--
-- USAGE:
--   - Run migration.sql first to create the database structure
--   - Then run this seeding.sql file to populate initial data
--   - Or use Spring Boot which runs schema.sql automatically (contains both)
--
-- IMPORTANT NOTES:
--   - All INSERT statements use ON CONFLICT or WHERE NOT EXISTS to prevent duplicate errors
--   - This file assumes tables already exist (run migration.sql first)
--   - Status values: 0=Pending, 1=Approved, 2=Rejected, 3=Blocked
-- ====================================================================================


-- ====================================================================================
-- SECTION 1: ROLES SEEDING
-- ====================================================================================
-- Purpose: Create basic roles (Admin, Teacher, Student)
-- Description: These roles are required for the application to function
-- Note: Uses ON CONFLICT to prevent errors if roles already exist
-- Note: DataInitializer.java also creates these roles, so this is a backup
-- ====================================================================================

-- Insert roles if they don't exist
INSERT INTO roles (name, created_at, updated_at) VALUES
    ('ADMIN', NOW(), NOW()),
    ('Teacher', NOW(), NOW()),
    ('Student', NOW(), NOW())
ON CONFLICT (name) DO UPDATE SET 
    updated_at = EXCLUDED.updated_at;


-- ====================================================================================
-- SECTION 2: LOOKUP TABLES SEEDING
-- ====================================================================================
-- Purpose: Populate lookup/reference tables with initial data
-- Description: These tables store reference data for the application
-- ====================================================================================

-- User Statuses
INSERT INTO user_statuses (id, name, label, description, is_active, display_order, created_at, updated_at) VALUES
    (0, 'PENDING', 'Pending', 'User registered but not approved', TRUE, 1, NOW(), NOW()),
    (1, 'APPROVED', 'Approved', 'User approved and can log in', TRUE, 2, NOW(), NOW()),
    (2, 'REJECTED', 'Rejected', 'User registration rejected', TRUE, 3, NOW(), NOW()),
    (3, 'BLOCKED', 'Blocked', 'User blocked by admin, cannot log in', TRUE, 4, NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    label = EXCLUDED.label,
    description = EXCLUDED.description,
    updated_at = EXCLUDED.updated_at;

-- Teacher Types
INSERT INTO teacher_types (code, name, description, is_active, display_order, created_at, updated_at) VALUES
    ('CLASS_TEACHER', 'Class Teacher', 'Class teacher who can approve/reject student join requests', TRUE, 1, NOW(), NOW()),
    ('SUBJECT_TEACHER', 'Subject Teacher', 'Subject teacher who can only view students', TRUE, 2, NOW(), NOW())
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    updated_at = EXCLUDED.updated_at;

-- Grades
INSERT INTO grades (code, name, min_marks, max_marks, description, display_order, is_active, created_at, updated_at) VALUES
    ('O', 'Outstanding', 90, 100, 'Outstanding performance', 1, TRUE, NOW(), NOW()),
    ('A', 'Excellent', 80, 89, 'Excellent performance', 2, TRUE, NOW(), NOW()),
    ('B', 'Good', 70, 79, 'Good performance', 3, TRUE, NOW(), NOW()),
    ('C', 'Average', 60, 69, 'Average performance', 4, TRUE, NOW(), NOW()),
    ('D', 'Below Average', 50, 59, 'Below average performance', 5, TRUE, NOW(), NOW()),
    ('F', 'Fail', 0, 49, 'Failed - needs improvement', 6, TRUE, NOW(), NOW())
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    min_marks = EXCLUDED.min_marks,
    max_marks = EXCLUDED.max_marks,
    description = EXCLUDED.description,
    updated_at = EXCLUDED.updated_at;

-- File Types
INSERT INTO file_types (extension, mime_type, category, max_size_mb, is_allowed_for_assignments, is_allowed_for_notes, is_active, display_order, created_at, updated_at) VALUES
    ('pdf', 'application/pdf', 'document', 10, TRUE, TRUE, TRUE, 1, NOW(), NOW()),
    ('docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'document', 10, TRUE, TRUE, TRUE, 2, NOW(), NOW()),
    ('doc', 'application/msword', 'document', 10, TRUE, TRUE, TRUE, 3, NOW(), NOW()),
    ('ppt', 'application/vnd.ms-powerpoint', 'document', 10, TRUE, TRUE, TRUE, 4, NOW(), NOW()),
    ('pptx', 'application/vnd.openxmlformats-officedocument.presentationml.presentation', 'document', 10, TRUE, TRUE, TRUE, 5, NOW(), NOW()),
    ('jpg', 'image/jpeg', 'image', 5, TRUE, TRUE, TRUE, 6, NOW(), NOW()),
    ('jpeg', 'image/jpeg', 'image', 5, TRUE, TRUE, TRUE, 7, NOW(), NOW()),
    ('png', 'image/png', 'image', 5, TRUE, TRUE, TRUE, 8, NOW(), NOW()),
    ('gif', 'image/gif', 'image', 5, TRUE, TRUE, TRUE, 9, NOW(), NOW())
ON CONFLICT (extension) DO UPDATE SET
    mime_type = EXCLUDED.mime_type,
    category = EXCLUDED.category,
    max_size_mb = EXCLUDED.max_size_mb,
    updated_at = EXCLUDED.updated_at;

-- ====================================================================================
-- SECTION 3: ADMIN USER SEEDING
-- ====================================================================================
-- Purpose: Create default admin user for system access
-- Description: Creates an admin user that can log in and manage the system
-- Default Credentials:
--   Email: admin@eone.com
--   Password: admin123
--   Status: Approved (1)
-- Note: Only inserts if user doesn't exist (prevents duplicate admin users)
-- Note: DataInitializer.java creates admin@gmail.com, this creates admin@eone.com
-- ⚠️ IMPORTANT: Change the password in production!
-- ====================================================================================

-- Insert admin user if it doesn't exist
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
) 
SELECT 
    'admin@eone.com',                    -- Admin email
    'System Administrator',               -- Admin name
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- Password: admin123 (BCrypt hashed)
    '+1234567890',                       -- Mobile number
    1,                                   -- Status: 1 = Approved (can log in immediately)
    '1990-01-01',                        -- Date of birth
    (SELECT id FROM roles WHERE name = 'ADMIN' LIMIT 1), -- Get ADMIN role ID
    NOW(),                               -- Created at
    NOW()                                -- Updated at
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@eone.com'
);


-- ====================================================================================
-- SECTION 3: BATCH YEARS SEEDING
-- ====================================================================================
-- Purpose: Create initial batch year options for classroom creation
-- Description: These are the standard batch year options available in the system
-- Usage: Used as reference data for classroom batch selection dropdown
-- Note: Uses ON CONFLICT to prevent errors if batch years already exist
-- ====================================================================================

-- Insert batch years if they don't exist
INSERT INTO batch_years (name, code, display_order, description, is_active, created_at, updated_at) VALUES
    ('First Year', 'FY', 1, 'First year undergraduate students', true, NOW(), NOW()),
    ('Second Year', 'SY', 2, 'Second year undergraduate students', true, NOW(), NOW()),
    ('Third Year', 'TY', 3, 'Third year undergraduate students', true, NOW(), NOW()),
    ('Fourth Year', 'FTH', 4, 'Fourth year undergraduate students', true, NOW(), NOW())
ON CONFLICT (name) DO UPDATE SET 
    updated_at = EXCLUDED.updated_at,
    display_order = EXCLUDED.display_order;

-- ====================================================================================
-- SECTION 4: ADDITIONAL SEED DATA (OPTIONAL)
-- ====================================================================================
-- Purpose: Add any additional seed data here
-- Description: You can add test data, sample classrooms, subjects, etc.
-- Note: Uncomment and modify these sections as needed for your development/testing
-- ====================================================================================

-- Example: Create sample classrooms (uncomment if needed)
-- INSERT INTO classrooms (name, batch, year, is_active, created_at, updated_at) VALUES
--     ('Class 10-A', '2024', '2024-2025', true, NOW(), NOW()),
--     ('Class 10-B', '2024', '2024-2025', true, NOW(), NOW()),
--     ('Class 11-A', '2024', '2024-2025', true, NOW(), NOW())
-- ON CONFLICT (name) DO NOTHING;

-- Example: Create sample subjects (uncomment if needed)
-- Note: Requires teachers and classrooms to exist first
-- INSERT INTO subjects (name, days_list, start_time, end_time, teacher_id, classroom_id, created_at, updated_at)
-- SELECT 
--     'Mathematics',
--     ARRAY['Monday', 'Wednesday', 'Friday'],
--     '09:00 AM',
--     '10:00 AM',
--     (SELECT id FROM users WHERE role_id = (SELECT id FROM roles WHERE name = 'Teacher') LIMIT 1),
--     (SELECT id FROM classrooms LIMIT 1),
--     NOW(),
--     NOW()
-- WHERE NOT EXISTS (
--     SELECT 1 FROM subjects WHERE name = 'Mathematics'
-- );

-- Example: Create test users (uncomment if needed)
-- INSERT INTO users (email, name, password_digest, mobile_number, status, role_id, created_at, updated_at)
-- SELECT 
--     'teacher@test.com',
--     'Test Teacher',
--     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- Password: admin123
--     '+1234567891',
--     1, -- Approved
--     (SELECT id FROM roles WHERE name = 'Teacher' LIMIT 1),
--     NOW(),
--     NOW()
-- WHERE NOT EXISTS (
--     SELECT 1 FROM users WHERE email = 'teacher@test.com'
-- );

-- ====================================================================================
-- END OF SEEDING FILE
-- ====================================================================================
-- 
-- This file contains ONLY initial data seeding (DML - Data Manipulation Language)
-- It does NOT contain any schema definitions (no CREATE TABLE statements)
--
-- VERIFICATION:
--   After running this file, verify:
--   1. Roles were created: SELECT * FROM roles;
--   2. Admin user was created: SELECT * FROM users WHERE email = 'admin@eone.com';
--   3. Test admin login with:
--      Email: admin@eone.com
--      Password: admin123
--
-- IMPORTANT NOTES:
--   - Default admin password is 'admin123' - CHANGE THIS IN PRODUCTION!
--   - Status values: 0=Pending, 1=Approved, 2=Rejected, 3=Blocked
--   - All INSERT statements use ON CONFLICT or WHERE NOT EXISTS to prevent duplicates
--   - This file assumes tables already exist (run migration.sql first)
--   - DataInitializer.java also creates roles and admin user (admin@gmail.com)
--   - This SQL file creates admin@eone.com as an alternative admin user
--
-- ====================================================================================

