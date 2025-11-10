# Database Files Guide

## Database Completeness Check ✅

**YES, the database schema is COMPLETE!**

All 8 core application tables are properly defined and match the Java entity models:

1. ✅ **roles** - User roles (ADMIN, Teacher, Student)
2. ✅ **users** - All user accounts with status management
3. ✅ **classrooms** - Classroom information
4. ✅ **subjects** - Subject information with schedules
5. ✅ **assignments** - Assignment information
6. ✅ **assignment_submissions** - Student submissions
7. ✅ **notifications** - System notifications
8. ✅ **join_requests** - Student join requests

Plus optional ActiveStorage tables for Rails compatibility.

---

## Database Files Structure

### 📁 Main Files (In `src/main/resources/`)

#### 1. **`migration.sql`** - Database Migration (DDL Only)
- **Purpose:** Creates all database tables, indexes, and constraints
- **Contains:** ONLY schema definitions (DDL - Data Definition Language)
- **Does NOT contain:** Any data (no INSERT statements)
- **Use when:** Setting up database structure from scratch
- **Run:** First, before seeding

**Contents:**
- Database extensions (plpgsql)
- All 8 core application tables
- All indexes and foreign key constraints
- ActiveStorage tables (optional)
- Circular reference handling (users ↔ classrooms)

#### 2. **`seeding.sql`** - Database Seeding (DML Only)
- **Purpose:** Populates database with initial/seed data
- **Contains:** ONLY data insertion (DML - Data Manipulation Language)
- **Does NOT contain:** Any schema definitions (no CREATE TABLE)
- **Use when:** Populating initial data after migration
- **Run:** After migration.sql

**Contents:**
- Roles seeding (ADMIN, Teacher, Student)
- Admin user creation (admin@eone.com)
- Optional sections for test data (commented out)

#### 3. **`schema.sql`** - Combined File (Legacy - For Spring Boot)
- **Purpose:** Combined file for Spring Boot automatic execution
- **Contains:** Both DDL and DML (migration + seeding combined)
- **Use when:** Spring Boot automatically runs this on startup
- **Note:** This is kept for backward compatibility with Spring Boot's auto-run feature

#### 4. **`data.sql`** - Additional Data (Currently Empty)
- **Purpose:** Additional data file (runs after schema.sql)
- **Contains:** Currently empty, available for future use
- **Use when:** Need to add additional data that runs after schema.sql

---

## Usage Guide

### Option 1: Manual Setup (Recommended for Production)

```bash
# Step 1: Run migration to create database structure
psql -U your_username -d eone_app -f src/main/resources/migration.sql

# Step 2: Run seeding to populate initial data
psql -U your_username -d eone_app -f src/main/resources/seeding.sql
```

### Option 2: Spring Boot Automatic Setup (Development)

Spring Boot automatically runs `schema.sql` on application startup when:
- `spring.sql.init.mode=always` is set in `application.properties`
- Files are in `src/main/resources/`

Just start the application:
```bash
mvn spring-boot:run
```

---

## File Comparison

| File | DDL (Schema) | DML (Data) | Purpose | When to Use |
|------|--------------|------------|---------|-------------|
| `migration.sql` | ✅ Yes | ❌ No | Create database structure | Manual setup, production |
| `seeding.sql` | ❌ No | ✅ Yes | Populate initial data | After migration, manual setup |
| `schema.sql` | ✅ Yes | ✅ Yes | Combined for Spring Boot | Spring Boot auto-run |
| `data.sql` | ❌ No | ✅ Yes | Additional data | After schema.sql (currently empty) |

---

## Database Schema Overview

### Core Tables

```
roles
  └── users (role_id)
        ├── classrooms (teacher_id) [circular reference]
        ├── subjects (teacher_id)
        ├── assignments (teacher_id)
        ├── assignment_submissions (user_id)
        ├── notifications (teacher_id, user_id)
        └── join_requests (student_user_id, approved_by_teacher_id)

classrooms
  ├── users (classroom_id)
  ├── subjects (classroom_id)
  └── join_requests (classroom_id)

subjects
  └── assignments (subject_id)

assignments
  ├── assignment_submissions (assignment_id)
  └── notifications (assignment_id)
```

### User Status Values

| Status | Name | Description |
|--------|------|-------------|
| 0 | Pending | User registered but not approved |
| 1 | Approved | User approved and can log in |
| 2 | Rejected | User registration rejected |
| 3 | Blocked | User blocked by admin, cannot log in |

---

## Default Admin Credentials

The application creates **two admin users**:

1. **From `seeding.sql` / `schema.sql`:**
   - **Email:** admin@eone.com
   - **Password:** admin123
   - **Status:** Approved (1)

2. **From `DataInitializer.java`:**
   - **Email:** admin@gmail.com
   - **Password:** admin123
   - **Status:** Approved (1)

⚠️ **IMPORTANT:** Change the admin passwords in production!

---

## Verification Steps

After running migration and seeding:

1. **Check tables were created:**
   ```sql
   \dt
   ```

2. **Check roles were created:**
   ```sql
   SELECT * FROM roles;
   ```

3. **Check admin user was created:**
   ```sql
   SELECT * FROM users WHERE email = 'admin@eone.com';
   ```

4. **Test admin login:**
   - Email: admin@eone.com
   - Password: admin123

---

## Important Notes

1. **Migration First, Then Seeding:**
   - Always run `migration.sql` before `seeding.sql`
   - Seeding requires tables to exist

2. **Idempotent Operations:**
   - All CREATE statements use `IF NOT EXISTS`
   - All INSERT statements use `ON CONFLICT` or `WHERE NOT EXISTS`
   - Safe to run multiple times

3. **Circular Reference:**
   - `users` and `classrooms` have a circular reference
   - Handled by creating tables first, then adding foreign key constraint

4. **Spring Boot Compatibility:**
   - `schema.sql` is kept for Spring Boot auto-run
   - For manual setup, use `migration.sql` + `seeding.sql`

---

## Troubleshooting

### Error: "relation does not exist"
- **Cause:** Running seeding.sql before migration.sql
- **Solution:** Run migration.sql first to create tables

### Error: "duplicate key value violates unique constraint"
- **Cause:** Data already exists
- **Solution:** This is normal. The ON CONFLICT clauses prevent errors.

### Error: "constraint already exists"
- **Cause:** Foreign key constraints already exist
- **Solution:** This is normal. The DO blocks handle this gracefully.

---

**Last Updated:** Separated database setup into migration.sql (DDL) and seeding.sql (DML) for better organization and manual control.

