# Database Setup Guide

## Overview

All database setup scripts have been consolidated into **two main files** in the `src/main/resources/` folder:

1. **`schema.sql`** - Complete database schema and initial data
2. **`data.sql`** - Additional data (currently empty, available for future use)

## File Structure

### Main Files (In Resources Folder)
- **`src/main/resources/schema.sql`** - **MAIN FILE** - Contains all tables, indexes, roles, and admin user
- **`src/main/resources/data.sql`** - Additional data file (empty, available for future use)

### How Spring Boot Uses These Files

Spring Boot automatically runs these files on application startup when:
- `spring.sql.init.mode=always` is set in `application.properties` (currently configured)
- Files are located in `src/main/resources/`
- `schema.sql` runs first, then `data.sql`

### Legacy Files (DELETED)
- ~~`src/main/resources/schema-postgres.sql`~~ - **DELETED** - Merged into `schema.sql`
- ~~`create_admin_user.sql`~~ - **DELETED** - Merged into `schema.sql`
- ~~`simple_admin_creation.sql`~~ - **DELETED** - Merged into `schema.sql`
- ~~`database_setup_complete.sql`~~ - **DELETED** - Merged into `schema.sql`

## Quick Start

### Automatic Setup (Recommended)

1. **Start the Spring Boot application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Spring Boot will automatically:**
   - Run `schema.sql` to create all tables and initial data
   - Run `data.sql` (currently empty)
   - Execute `DataInitializer.java` to create roles and admin user (admin@gmail.com)

3. **Verify setup:**
   - Check that all tables were created
   - Verify admin users were created
   - Test admin login

### Manual Setup (If Needed)

If you need to run the SQL files manually:

```bash
# Connect to PostgreSQL
psql -U your_username -d eone_app

# Run the schema file
\i src/main/resources/schema.sql
```

## What's Included in `schema.sql`

### Section 1: Database Extensions Setup
- Enables required PostgreSQL extensions (plpgsql)

### Section 2: Core Application Tables
- **Roles table** - User roles (ADMIN, Teacher, Student)
- **Users table** - All user accounts with status (0=Pending, 1=Approved, 2=Rejected, 3=Blocked)
- **Classrooms table** - Classroom information
- **Subjects table** - Subject information with schedules
- **Assignments table** - Assignment information
- **Assignment Submissions table** - Student submissions
- **Notifications table** - System notifications
- **Join Requests table** - Student join requests
- All necessary indexes and foreign key constraints

### Section 3: ActiveStorage Tables (Optional)
- ActiveStorage Blobs
- ActiveStorage Attachments
- ActiveStorage Variant Records
- *Note: These are only needed for Rails compatibility*

### Section 4: Initial Data Setup
- **Roles:** Creates ADMIN, Teacher, Student roles
- **Admin User:** Creates admin@eone.com user
  - Email: admin@eone.com
  - Password: admin123
  - Status: Approved (1)

## Default Admin Credentials

The application creates **two admin users**:

1. **From `schema.sql`:**
   - **Email:** admin@eone.com
   - **Password:** admin123
   - **Status:** Approved (1)

2. **From `DataInitializer.java`:**
   - **Email:** admin@gmail.com
   - **Password:** admin123
   - **Status:** Approved (1)

⚠️ **IMPORTANT:** Change the admin passwords in production!

## User Status Values

| Status | Name | Description |
|--------|------|-------------|
| 0 | Pending | User registered but not approved |
| 1 | Approved | User approved and can log in |
| 2 | Rejected | User registration rejected |
| 3 | Blocked | User blocked by admin, cannot log in |

## Database Schema Diagram

```
roles
  └── users (role_id)
        ├── classrooms (teacher_id)
        ├── subjects (teacher_id)
        ├── assignments (teacher_id)
        └── assignment_submissions (user_id)

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

## Important Notes

1. **Hibernate Auto-Update:**
   - The application uses `spring.jpa.hibernate.ddl-auto=update`
   - Hibernate will automatically update table structures if entities change
   - `schema.sql` acts as a backup and initial setup file

2. **Circular Reference:**
   - There's a circular reference between `users` and `classrooms` tables
   - `schema.sql` handles this by creating tables in the correct order
   - The foreign key constraint is added after both tables exist

3. **Data Initialization:**
   - Roles and admin users are created by both `schema.sql` and `DataInitializer.java`
   - This ensures data exists even if one method fails
   - Uses `ON CONFLICT` and `WHERE NOT EXISTS` to prevent duplicates

4. **File Locations:**
   - All SQL files are now in `src/main/resources/`
   - Spring Boot automatically finds and runs files in this location
   - No need to manually run SQL files unless troubleshooting

## Troubleshooting

### Error: "relation already exists"
- **Cause:** Tables already exist from Hibernate or previous setup
- **Solution:** This is normal. The `IF NOT EXISTS` clauses prevent errors.

### Error: "constraint already exists"
- **Cause:** Foreign key constraints already exist
- **Solution:** The DO block in `schema.sql` handles this gracefully.

### Error: "ADMIN role not found"
- **Cause:** Roles table is empty
- **Solution:** Check that `schema.sql` ran successfully. Roles should be created automatically.

### Admin User Not Created
- **Cause:** Multiple possible causes
- **Solution:** 
  1. Check that roles were created first
  2. Check that `DataInitializer.java` ran successfully
  3. Verify database connection in `application.properties`

## Next Steps After Setup

1. ✅ Verify all tables were created
2. ✅ Verify admin users can log in
3. ✅ Create additional users (teachers, students) via the application
4. ✅ Create classrooms
5. ✅ Assign teachers to classrooms
6. ✅ Create subjects
7. ✅ Create assignments

## Support

For issues or questions:
1. Check the comments in `schema.sql`
2. Review `DataInitializer.java` for Java-based initialization
3. Check application logs for errors
4. Verify database connection settings in `application.properties`

---

**Last Updated:** Consolidated all SQL files into `schema.sql` and `data.sql` in the resources folder for easier management and automatic execution by Spring Boot.
