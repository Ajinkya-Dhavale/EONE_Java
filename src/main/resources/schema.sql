-- ====================================================================================
-- EONE DATABASE SETUP - COMPLETE SCHEMA FILE (LEGACY - FOR SPRING BOOT AUTO-RUN)
-- ====================================================================================
-- This file combines migration.sql and seeding.sql for Spring Boot automatic execution
-- Location: src/main/resources/schema.sql
-- 
-- NOTE: For manual database setup, use:
--   - migration.sql (DDL only - tables and schema)
--   - seeding.sql (DML only - initial data)
--
-- Spring Boot automatically runs this file on application startup when:
--   - spring.sql.init.mode=always (configured in application.properties)
--   - Database tables don't exist or need to be created
--
-- FILE STRUCTURE:
--   1. Database Extensions Setup
--   2. Core Application Tables (Schema - DDL)
--   3. ActiveStorage Tables (Optional - for Rails compatibility)
--   4. Initial Data Setup (Roles and Admin User - DML)
--
-- IMPORTANT NOTES:
--   - This file contains both DDL (Data Definition Language) and DML (Data Manipulation Language)
--   - Tables are created with IF NOT EXISTS to prevent errors if they already exist
--   - All INSERT statements use ON CONFLICT to prevent duplicate errors
--   - Status values: 0=Pending, 1=Approved, 2=Rejected, 3=Blocked
--   - Spring Boot runs this file automatically on application startup
-- ====================================================================================


-- ====================================================================================
-- SECTION 1: DATABASE EXTENSIONS SETUP
-- ====================================================================================
-- Purpose: Enable required PostgreSQL extensions
-- Description: Required for stored procedures, functions, and advanced SQL operations
-- ====================================================================================

-- Enable plpgsql extension (required for stored procedures and functions)
CREATE EXTENSION IF NOT EXISTS plpgsql;


-- ====================================================================================
-- SECTION 2: CORE APPLICATION TABLES (SCHEMA - DDL)
-- ====================================================================================
-- Purpose: Create all core application tables for EONE system
-- Description: These tables define the database structure for the entire application
-- Note: Tables are created in order to handle foreign key dependencies
-- ====================================================================================

-- ------------------------------------------------------------------------------------
-- ROLES TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores user roles (Admin, Teacher, Student, etc.)
-- Description: Central role management table that defines user access levels
-- Usage: Used for role-based access control (RBAC)
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,  -- Role name: 'ADMIN', 'Teacher', 'Student', etc.
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- ------------------------------------------------------------------------------------
-- USER_STATUSES TABLE (Lookup/Reference Table)
-- ------------------------------------------------------------------------------------
-- Purpose: Stores user status options (Pending, Approved, Rejected, Blocked)
-- Description: Normalized lookup table for user statuses to ensure data consistency
-- Usage: Used as reference data for user status management
-- Design: Follows best practices for reference/lookup tables
CREATE TABLE IF NOT EXISTS user_statuses (
    id INTEGER PRIMARY KEY,                    -- Status value: 0, 1, 2, 3
    name VARCHAR(50) NOT NULL UNIQUE,          -- Status name: 'PENDING', 'APPROVED', 'REJECTED', 'BLOCKED'
    label VARCHAR(100) NOT NULL,               -- Display label: 'Pending', 'Approved', 'Rejected', 'Blocked'
    description TEXT,                          -- Status description
    is_active BOOLEAN NOT NULL DEFAULT TRUE,   -- Whether this status is active/available
    display_order INTEGER NOT NULL,            -- Order to display (1, 2, 3, 4)
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for user_statuses table
CREATE INDEX IF NOT EXISTS index_user_statuses_on_display_order ON user_statuses(display_order);
CREATE INDEX IF NOT EXISTS index_user_statuses_on_is_active ON user_statuses(is_active);

-- ------------------------------------------------------------------------------------
-- TEACHER_TYPES TABLE (Lookup/Reference Table)
-- ------------------------------------------------------------------------------------
-- Purpose: Stores teacher type options (Class Teacher, Subject Teacher)
-- Description: Normalized lookup table for teacher types to ensure data consistency
-- Usage: Used as reference data for teacher type management
-- Design: Follows best practices for reference/lookup tables
CREATE TABLE IF NOT EXISTS teacher_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,          -- Teacher type code: 'CLASS_TEACHER', 'SUBJECT_TEACHER'
    name VARCHAR(100) NOT NULL,                -- Display name: 'Class Teacher', 'Subject Teacher'
    description TEXT,                          -- Teacher type description
    is_active BOOLEAN NOT NULL DEFAULT TRUE,   -- Whether this teacher type is active/available
    display_order INTEGER NOT NULL DEFAULT 0,   -- Order to display (1, 2)
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for teacher_types table
CREATE INDEX IF NOT EXISTS index_teacher_types_on_display_order ON teacher_types(display_order);
CREATE INDEX IF NOT EXISTS index_teacher_types_on_is_active ON teacher_types(is_active);

-- ------------------------------------------------------------------------------------
-- GRADES TABLE (Lookup/Reference Table)
-- ------------------------------------------------------------------------------------
-- Purpose: Stores grade options (O, A, B, C, D, F)
-- Description: Normalized lookup table for grades to ensure data consistency
-- Usage: Used as reference data for assignment grading
-- Design: Follows best practices for reference/lookup tables
CREATE TABLE IF NOT EXISTS grades (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,          -- Grade code: 'O', 'A', 'B', 'C', 'D', 'F'
    name VARCHAR(50) NOT NULL,                 -- Grade name: 'Outstanding', 'Excellent', 'Good', etc.
    min_marks INTEGER,                         -- Minimum marks for this grade
    max_marks INTEGER,                         -- Maximum marks for this grade
    description TEXT,                          -- Grade description
    display_order INTEGER NOT NULL DEFAULT 0,  -- Order to display (1, 2, 3, 4, 5, 6)
    is_active BOOLEAN NOT NULL DEFAULT TRUE,   -- Whether this grade is active/available
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for grades table
CREATE INDEX IF NOT EXISTS index_grades_on_display_order ON grades(display_order);
CREATE INDEX IF NOT EXISTS index_grades_on_is_active ON grades(is_active);
CREATE INDEX IF NOT EXISTS index_grades_on_code ON grades(code);

-- ------------------------------------------------------------------------------------
-- FILE_TYPES TABLE (Lookup/Reference Table)
-- ------------------------------------------------------------------------------------
-- Purpose: Stores file type options (pdf, docx, jpg, png, etc.)
-- Description: Normalized lookup table for file types to ensure data consistency
-- Usage: Used as reference data for file upload validation
-- Design: Follows best practices for reference/lookup tables
CREATE TABLE IF NOT EXISTS file_types (
    id BIGSERIAL PRIMARY KEY,
    extension VARCHAR(10) NOT NULL UNIQUE,     -- File extension: 'pdf', 'docx', 'jpg', 'png', etc.
    mime_type VARCHAR(100) NOT NULL,           -- MIME type: 'application/pdf', 'image/jpeg', etc.
    category VARCHAR(50),                      -- File category: 'document', 'image', 'video'
    max_size_mb INTEGER,                       -- Maximum file size in MB
    is_allowed_for_assignments BOOLEAN NOT NULL DEFAULT TRUE,  -- Whether allowed for assignments
    is_allowed_for_notes BOOLEAN NOT NULL DEFAULT TRUE,        -- Whether allowed for notes
    is_active BOOLEAN NOT NULL DEFAULT TRUE,   -- Whether this file type is active/available
    display_order INTEGER NOT NULL DEFAULT 0,  -- Order to display
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for file_types table
CREATE INDEX IF NOT EXISTS index_file_types_on_category ON file_types(category);
CREATE INDEX IF NOT EXISTS index_file_types_on_is_active ON file_types(is_active);
CREATE INDEX IF NOT EXISTS index_file_types_on_extension ON file_types(extension);

-- ------------------------------------------------------------------------------------
-- BATCH_YEARS TABLE (Lookup/Reference Table)
-- ------------------------------------------------------------------------------------
-- Purpose: Stores batch year options (First Year, Second Year, Third Year, etc.)
-- Description: Normalized lookup table for batch years to ensure data consistency
-- Usage: Used as reference data for classroom batch selection
-- Design: Follows best practices for reference/lookup tables
CREATE TABLE IF NOT EXISTS batch_years (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,     -- Batch year name: "First Year", "Second Year", etc.
    code VARCHAR(20) UNIQUE,               -- Short code: "FY", "SY", "TY" (optional, for programmatic use)
    display_order INTEGER NOT NULL DEFAULT 0, -- Order to display in dropdown (1, 2, 3...)
    description TEXT,                       -- Optional description: "First year undergraduate students"
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Whether this batch year is active/available
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index for batch_years table
CREATE INDEX IF NOT EXISTS index_batch_years_on_display_order ON batch_years(display_order);
CREATE INDEX IF NOT EXISTS index_batch_years_on_is_active ON batch_years(is_active);

-- ------------------------------------------------------------------------------------
-- CLASSROOMS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores classroom information
-- Description: Represents classes/grades where students and teachers are assigned
-- Usage: Each classroom can have one class teacher and multiple students
-- Note: teacher_id foreign key is added after users table is created (see below)
CREATE TABLE IF NOT EXISTS classrooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,           -- Classroom name (e.g., "MScCS")
    teacher_id BIGINT,                     -- Class teacher (foreign key constraint added after users table)
    batch_year_id BIGINT REFERENCES batch_years(id) ON DELETE SET NULL, -- Foreign key to batch_years table
    batch VARCHAR(50),                     -- Batch identifier (legacy field - kept for backward compatibility)
    year VARCHAR(10),                      -- Academic year
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Whether classroom is active
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_classroom_name_batch_year UNIQUE (name, batch_year_id, year)  -- Unique combination of name, batch_year_id, and year
);

-- Index for classrooms table (created before foreign key constraint)
CREATE INDEX IF NOT EXISTS index_classrooms_on_teacher_id ON classrooms(teacher_id);

-- ------------------------------------------------------------------------------------
-- USERS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores all user accounts (Admin, Teachers, Students)
-- Description: Main user table with authentication and profile information
-- Status Values:
--   0 = Pending   (User registered but not yet approved by admin)
--   1 = Approved  (User approved and can log in)
--   2 = Rejected  (User registration rejected by admin)
--   3 = Blocked   (User blocked by admin, cannot log in)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),                    -- User email (used for login)
    name VARCHAR(255),                     -- User full name
    password_digest VARCHAR(255),          -- Hashed password (BCrypt)
    mobile_number VARCHAR(30),             -- User mobile number
    status INTEGER,                        -- User status: 0=Pending, 1=Approved, 2=Rejected, 3=Blocked
    date_of_birth DATE,                    -- User date of birth
    role_id BIGINT NOT NULL REFERENCES roles(id),  -- Foreign key to roles table
    classroom_id BIGINT REFERENCES classrooms(id), -- Foreign key to classrooms (nullable)
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    avatar VARCHAR(255),                   -- Avatar file path
    teacher_type VARCHAR(20) DEFAULT 'SUBJECT_TEACHER'  -- Teacher type: 'CLASS_TEACHER' or 'SUBJECT_TEACHER'
);

-- Indexes for users table (improves query performance)
CREATE INDEX IF NOT EXISTS index_users_on_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS index_users_on_classroom_id ON users(classroom_id);

-- Add foreign key constraint for classrooms.teacher_id (after users table exists)
-- This handles the circular reference between users and classrooms
-- Note: Hibernate will also create this constraint with ddl-auto=update, so this is a backup
-- Using a simpler approach that works with Spring Boot's SQL parser
-- The constraint will be created by Hibernate automatically, so this is commented out to avoid parser issues
-- If you need to create it manually, uncomment and run separately:
-- ALTER TABLE classrooms ADD CONSTRAINT classrooms_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES users(id);

-- ------------------------------------------------------------------------------------
-- SUBJECTS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores subject information with schedule
-- Description: Subjects taught in classrooms with time schedules
-- Usage: Each subject is taught by a teacher in a specific classroom
CREATE TABLE IF NOT EXISTS subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,            -- Subject name (e.g., "Mathematics", "Science")
    days_list TEXT[] DEFAULT '{}',         -- Array of days when subject is taught (e.g., ['Monday', 'Wednesday'])
    start_time VARCHAR(20),                -- Class start time (e.g., "09:00 AM")
    end_time VARCHAR(20),                  -- Class end time (e.g., "10:00 AM")
    teacher_id BIGINT NOT NULL REFERENCES users(id),  -- Teacher assigned to this subject
    classroom_id BIGINT NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,  -- Classroom where subject is taught
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for subjects table
CREATE INDEX IF NOT EXISTS index_subjects_on_classroom_id ON subjects(classroom_id);
CREATE INDEX IF NOT EXISTS index_subjects_on_teacher_id ON subjects(teacher_id);

-- ------------------------------------------------------------------------------------
-- CHAPTERS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores chapters for each subject
-- Description: Represents chapters/topics within a subject that teachers can manage
-- Usage: Teachers create chapters for their subjects, students view them
-- Design: Normalized table with proper foreign keys and indexes
CREATE TABLE IF NOT EXISTS chapters (
    id BIGSERIAL PRIMARY KEY,
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,  -- Subject this chapter belongs to
    name VARCHAR(255) NOT NULL,            -- Chapter name (e.g., "Introduction to AI")
    description TEXT,                      -- Chapter description/details
    chapter_number INTEGER,                 -- Chapter number (optional, for ordering)
    display_order INTEGER NOT NULL DEFAULT 0, -- Order to display chapters (1, 2, 3...)
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Whether chapter is active/visible
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_chapter_subject_name UNIQUE (subject_id, name)  -- Prevent duplicate chapter names per subject
);

-- Indexes for chapters table
CREATE INDEX IF NOT EXISTS idx_chapters_subject_id ON chapters(subject_id);
CREATE INDEX IF NOT EXISTS idx_chapters_display_order ON chapters(display_order);
CREATE INDEX IF NOT EXISTS idx_chapters_is_active ON chapters(is_active);

-- ------------------------------------------------------------------------------------
-- TOPICS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores topics within chapters
-- Description: Represents specific topics covered in a chapter
-- Usage: Teachers add topics to chapters, students view them
-- Design: Normalized table with proper foreign keys and indexes
CREATE TABLE IF NOT EXISTS topics (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL REFERENCES chapters(id) ON DELETE CASCADE,  -- Chapter this topic belongs to
    name VARCHAR(255) NOT NULL,            -- Topic name (e.g., "History of AI")
    description TEXT,                      -- Topic description/details
    display_order INTEGER NOT NULL DEFAULT 0, -- Order to display topics (1, 2, 3...)
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Whether topic is active/visible
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for topics table
CREATE INDEX IF NOT EXISTS idx_topics_chapter_id ON topics(chapter_id);
CREATE INDEX IF NOT EXISTS idx_topics_display_order ON topics(display_order);
CREATE INDEX IF NOT EXISTS idx_topics_is_active ON topics(is_active);

-- ------------------------------------------------------------------------------------
-- NOTES TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores notes/files uploaded for chapters
-- Description: Teachers can upload notes (PDF, DOCX, etc.) for each chapter
-- Usage: Teachers upload notes, students download them
-- Design: Normalized table with file metadata and proper foreign keys
CREATE TABLE IF NOT EXISTS notes (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL REFERENCES chapters(id) ON DELETE CASCADE,  -- Chapter this note belongs to
    title VARCHAR(255) NOT NULL,            -- Note title (e.g., "AI Introduction Notes")
    description TEXT,                       -- Note description/details
    file_url VARCHAR(500),                  -- File URL/path (stored in file system or cloud)
    file_name VARCHAR(255),                 -- Original file name
    file_type VARCHAR(50),                  -- File type: 'pdf', 'docx', 'doc', 'pptx', etc.
    file_size BIGINT,                       -- File size in bytes
    uploaded_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Teacher who uploaded
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Whether note is active/visible
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for notes table
CREATE INDEX IF NOT EXISTS idx_notes_chapter_id ON notes(chapter_id);
CREATE INDEX IF NOT EXISTS idx_notes_uploaded_by ON notes(uploaded_by);
CREATE INDEX IF NOT EXISTS idx_notes_is_active ON notes(is_active);

-- ------------------------------------------------------------------------------------
-- VIDEOS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores videos for chapters
-- Description: Teachers can add videos (YouTube, Vimeo, or direct file) for each chapter
-- Usage: Teachers add videos, students watch them
-- Design: Normalized table with video metadata and proper foreign keys
CREATE TABLE IF NOT EXISTS videos (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL REFERENCES chapters(id) ON DELETE CASCADE,  -- Chapter this video belongs to
    title VARCHAR(255) NOT NULL,            -- Video title (e.g., "Introduction to AI")
    description TEXT,                       -- Video description/details
    video_url VARCHAR(500) NOT NULL,        -- Video URL (YouTube, Vimeo, or direct file URL)
    video_type VARCHAR(50),                 -- Video type: 'youtube', 'vimeo', 'direct'
    thumbnail_url VARCHAR(500),             -- Video thumbnail URL (optional)
    duration INTEGER,                       -- Video duration in seconds (optional)
    uploaded_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Teacher who added video
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Whether video is active/visible
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for videos table
CREATE INDEX IF NOT EXISTS idx_videos_chapter_id ON videos(chapter_id);
CREATE INDEX IF NOT EXISTS idx_videos_uploaded_by ON videos(uploaded_by);
CREATE INDEX IF NOT EXISTS idx_videos_is_active ON videos(is_active);

-- ------------------------------------------------------------------------------------
-- ASSIGNMENTS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores assignment information
-- Description: Assignments created by teachers for students
-- Usage: Teachers create assignments for subjects, students submit them
CREATE TABLE IF NOT EXISTS assignments (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,           -- Assignment title
    description TEXT,                      -- Assignment description/details
    due_date DATE,                         -- Assignment due date
    file VARCHAR(255),                     -- Assignment file path (if any)
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,  -- Subject this assignment belongs to
    teacher_id BIGINT REFERENCES users(id), -- Teacher who created the assignment
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for assignments table
CREATE INDEX IF NOT EXISTS index_assignments_on_subject_id ON assignments(subject_id);
CREATE INDEX IF NOT EXISTS index_assignments_on_teacher_id ON assignments(teacher_id);

-- ------------------------------------------------------------------------------------
-- ASSIGNMENT SUBMISSIONS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores student assignment submissions
-- Description: Tracks which students submitted assignments and their grades
-- Usage: Students submit assignments, teachers grade them
CREATE TABLE IF NOT EXISTS assignment_submissions (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,  -- Assignment being submitted
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Student who submitted
    file VARCHAR(255),                     -- Submission file path
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    marks INTEGER,                         -- Marks awarded by teacher
    grade VARCHAR(10)                      -- Grade (e.g., "A", "B", "C")
);

-- Indexes for assignment_submissions table
CREATE INDEX IF NOT EXISTS index_assignment_submissions_on_assignment_id ON assignment_submissions(assignment_id);
CREATE INDEX IF NOT EXISTS index_assignment_submissions_on_user_id ON assignment_submissions(user_id);

-- ------------------------------------------------------------------------------------
-- NOTIFICATIONS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores system notifications
-- Description: Notifications for teachers and students (assignment submissions, join requests, etc.)
-- Usage: System sends notifications to users for various events
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT REFERENCES assignments(id) ON DELETE SET NULL,  -- Related assignment (if any)
    teacher_id BIGINT REFERENCES users(id) ON DELETE SET NULL,  -- Teacher who receives notification
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,  -- User who receives notification
    message TEXT,                          -- Notification message
    read_at TIMESTAMP,                     -- When notification was read (NULL if unread)
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for notifications table
CREATE INDEX IF NOT EXISTS index_notifications_on_assignment_id ON notifications(assignment_id);
CREATE INDEX IF NOT EXISTS index_notifications_on_teacher_id ON notifications(teacher_id);
CREATE INDEX IF NOT EXISTS index_notifications_on_user_id ON notifications(user_id);

-- ------------------------------------------------------------------------------------
-- JOIN REQUESTS TABLE
-- ------------------------------------------------------------------------------------
-- Purpose: Stores student join requests for classrooms
-- Description: Tracks when students request to join a classroom (approved by class teacher)
-- Usage: Students request to join classrooms, class teachers approve/reject
CREATE TABLE IF NOT EXISTS join_requests (
    id BIGSERIAL PRIMARY KEY,
    student_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Student requesting to join
    classroom_id BIGINT NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,  -- Classroom student wants to join
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- Request status: 'PENDING', 'APPROVED', 'REJECTED'
    approved_by_teacher_id BIGINT REFERENCES users(id) ON DELETE SET NULL,  -- Teacher who approved/rejected
    requested_at TIMESTAMP NOT NULL DEFAULT NOW(),  -- When request was made
    decided_at TIMESTAMP  -- When request was approved/rejected
);

-- Index for join_requests table
CREATE INDEX IF NOT EXISTS idx_join_requests_student_class ON join_requests(student_user_id, classroom_id);


-- ====================================================================================
-- SECTION 3: ACTIVESTORAGE TABLES (OPTIONAL - FOR RAILS COMPATIBILITY)
-- ====================================================================================
-- Purpose: ActiveStorage-compatible tables (for Rails compatibility)
-- Description: These tables are not used by the Java app but kept for DB structure alignment
-- Usage: Only needed if you're integrating with a Rails application
-- Note: You can comment out this section if you don't need Rails compatibility
-- ====================================================================================

-- ActiveStorage Blobs Table (file metadata storage)
CREATE TABLE IF NOT EXISTS active_storage_blobs (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR NOT NULL,
    filename VARCHAR NOT NULL,
    content_type VARCHAR,
    metadata TEXT,
    service_name VARCHAR NOT NULL,
    byte_size BIGINT NOT NULL,
    checksum VARCHAR,
    created_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS index_active_storage_blobs_on_key
    ON active_storage_blobs (key);

-- ActiveStorage Attachments Table (links files to records)
CREATE TABLE IF NOT EXISTS active_storage_attachments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    record_type VARCHAR NOT NULL,
    record_id BIGINT NOT NULL,
    blob_id BIGINT NOT NULL REFERENCES active_storage_blobs(id),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS index_active_storage_attachments_on_blob_id
    ON active_storage_attachments (blob_id);

CREATE UNIQUE INDEX IF NOT EXISTS index_active_storage_attachments_uniqueness
    ON active_storage_attachments (record_type, record_id, name, blob_id);

-- ActiveStorage Variant Records Table (for image variants)
CREATE TABLE IF NOT EXISTS active_storage_variant_records (
    id BIGSERIAL PRIMARY KEY,
    blob_id BIGINT NOT NULL REFERENCES active_storage_blobs(id),
    variation_digest VARCHAR NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS index_active_storage_variant_records_uniqueness
    ON active_storage_variant_records (blob_id, variation_digest);


-- ====================================================================================
-- SECTION 4: INITIAL DATA SETUP (DML)
-- ====================================================================================
-- Purpose: Insert initial/seed data into the database
-- Description: Creates basic roles and admin user for the application
-- Usage: This data is required for the application to function properly
-- Note: DataInitializer.java also creates roles and admin user, so this acts as a backup
-- ====================================================================================

-- ------------------------------------------------------------------------------------
-- INSERT ROLES
-- ------------------------------------------------------------------------------------
-- Purpose: Create basic roles (Admin, Teacher, Student)
-- Description: These roles are required for the application to function
-- Note: Uses ON CONFLICT to prevent errors if roles already exist
-- Note: DataInitializer.java also creates these roles, so this is a backup
INSERT INTO roles (name, created_at, updated_at) VALUES
    ('ADMIN', NOW(), NOW()),
    ('Teacher', NOW(), NOW()),
    ('Student', NOW(), NOW())
ON CONFLICT (name) DO UPDATE SET 
    updated_at = EXCLUDED.updated_at;

-- ------------------------------------------------------------------------------------
-- CREATE ADMIN USER
-- ------------------------------------------------------------------------------------
-- Purpose: Create default admin user for system access
-- Description: Creates an admin user that can log in and manage the system
-- Default Credentials:
--   Email: admin@eone.com
--   Password: admin123
--   Status: Approved (1)
-- Note: Only inserts if user doesn't exist (prevents duplicate admin users)
-- Note: DataInitializer.java creates admin@gmail.com, this creates admin@eone.com
-- ⚠️ IMPORTANT: Change the password in production!
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

-- ------------------------------------------------------------------------------------
-- INSERT BATCH YEARS
-- ------------------------------------------------------------------------------------
-- Purpose: Create initial batch year options for classroom creation
-- Description: These are the standard batch year options available in the system
-- Usage: Used as reference data for classroom batch selection dropdown
-- Note: Uses ON CONFLICT to prevent errors if batch years already exist
INSERT INTO batch_years (name, code, display_order, description, is_active, created_at, updated_at) VALUES
    ('First Year', 'FY', 1, 'First year undergraduate students', true, NOW(), NOW()),
    ('Second Year', 'SY', 2, 'Second year undergraduate students', true, NOW(), NOW()),
    ('Third Year', 'TY', 3, 'Third year undergraduate students', true, NOW(), NOW())
ON CONFLICT (name) DO UPDATE SET 
    updated_at = EXCLUDED.updated_at,
    display_order = EXCLUDED.display_order;

-- ====================================================================================
-- END OF SCHEMA SETUP
-- ====================================================================================
-- 
-- This file is automatically executed by Spring Boot on application startup
-- when spring.sql.init.mode=always is set in application.properties
--
-- VERIFICATION:
--   After application starts, you can verify the setup by:
--   1. Checking that all tables were created: \dt in psql
--   2. Checking that roles were created (ADMIN, Teacher, Student)
--   3. Checking that admin user was created (admin@eone.com)
--   4. Testing admin login with:
--      Email: admin@eone.com
--      Password: admin123
--
-- IMPORTANT NOTES:
--   - Default admin password is 'admin123' - CHANGE THIS IN PRODUCTION!
--   - Status values: 0=Pending, 1=Approved, 2=Rejected, 3=Blocked
--   - All timestamps use NOW() for current timestamp
--   - All foreign keys have appropriate CASCADE or SET NULL behaviors
--   - Tables are created with IF NOT EXISTS to prevent errors on re-run
--   - Data inserts use ON CONFLICT to prevent duplicate errors
--   - DataInitializer.java also creates roles and admin user (admin@gmail.com)
--   - This SQL file creates admin@eone.com as an alternative admin user
--
-- ====================================================================================
