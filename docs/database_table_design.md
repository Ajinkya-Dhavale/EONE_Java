## 3. Design

### 3.1 Database Table Designing / Algorithm Specifications

> **Legend:** Length `-` means the data type has no explicit length limit in PostgreSQL (e.g., `TEXT`, `TIMESTAMP`). Default values are included under Constraints. Array types show their literal declaration.

---

#### 1) `roles` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | name | VARCHAR | 50 | NOT NULL, UNIQUE |
| 3 | created_at | TIMESTAMP | - | NULLABLE |
| 4 | updated_at | TIMESTAMP | - | NULLABLE |

---

#### 2) `user_statuses` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | INTEGER | 11 | PRIMARY KEY |
| 2 | name | VARCHAR | 50 | NOT NULL, UNIQUE |
| 3 | label | VARCHAR | 100 | NOT NULL |
| 4 | description | TEXT | - | NULLABLE |
| 5 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 6 | display_order | INTEGER | 11 | NOT NULL |
| 7 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 8 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `display_order`, `is_active`.

---

#### 3) `teacher_types` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | code | VARCHAR | 50 | NOT NULL, UNIQUE |
| 3 | name | VARCHAR | 100 | NOT NULL |
| 4 | description | TEXT | - | NULLABLE |
| 5 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 6 | display_order | INTEGER | 11 | NOT NULL, DEFAULT 0 |
| 7 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 8 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `display_order`, `is_active`.

---

#### 4) `grades` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | code | VARCHAR | 10 | NOT NULL, UNIQUE |
| 3 | name | VARCHAR | 50 | NOT NULL |
| 4 | min_marks | INTEGER | 11 | NULLABLE |
| 5 | max_marks | INTEGER | 11 | NULLABLE |
| 6 | description | TEXT | - | NULLABLE |
| 7 | display_order | INTEGER | 11 | NOT NULL, DEFAULT 0 |
| 8 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 9 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 10 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `display_order`, `is_active`, `code`.

---

#### 5) `file_types` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | extension | VARCHAR | 10 | NOT NULL, UNIQUE |
| 3 | mime_type | VARCHAR | 100 | NOT NULL |
| 4 | category | VARCHAR | 50 | NULLABLE |
| 5 | max_size_mb | INTEGER | 11 | NULLABLE |
| 6 | is_allowed_for_assignments | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 7 | is_allowed_for_notes | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 8 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 9 | display_order | INTEGER | 11 | NOT NULL, DEFAULT 0 |
| 10 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 11 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `category`, `is_active`, `extension`.

---

#### 6) `batch_years` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | name | VARCHAR | 50 | NOT NULL, UNIQUE |
| 3 | code | VARCHAR | 20 | UNIQUE, NULLABLE |
| 4 | display_order | INTEGER | 11 | NOT NULL, DEFAULT 0 |
| 5 | description | TEXT | - | NULLABLE |
| 6 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 7 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 8 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `display_order`, `is_active`.

---

#### 7) `classrooms` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | name | VARCHAR | 255 | NOT NULL |
| 3 | teacher_id | BIGINT | 20 | FOREIGN KEY → `users.id`, NULLABLE |
| 4 | batch_year_id | BIGINT | 20 | FOREIGN KEY → `batch_years.id`, ON DELETE SET NULL |
| 5 | batch | VARCHAR | 50 | NULLABLE |
| 6 | year | VARCHAR | 10 | NULLABLE |
| 7 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 8 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 9 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 10 | uk_classroom_name_batch_year | - | - | UNIQUE(name, batch_year_id, year) |

Index: `teacher_id`.

---

#### 8) `users` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | email | VARCHAR | 255 | NULLABLE |
| 3 | name | VARCHAR | 255 | NULLABLE |
| 4 | password_digest | VARCHAR | 255 | NULLABLE |
| 5 | mobile_number | VARCHAR | 30 | NULLABLE |
| 6 | status | INTEGER | 11 | NULLABLE (0=Pending,1=Approved,2=Rejected,3=Blocked) |
| 7 | date_of_birth | DATE | - | NULLABLE |
| 8 | role_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `roles.id` |
| 9 | classroom_id | BIGINT | 20 | FOREIGN KEY → `classrooms.id`, NULLABLE |
|10 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
|11 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
|12 | avatar | VARCHAR | 255 | NULLABLE |
|13 | teacher_type | VARCHAR | 20 | DEFAULT 'SUBJECT_TEACHER' |

Indexes: `role_id`, `classroom_id`.

---

#### 9) `subjects` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | name | VARCHAR | 255 | NOT NULL |
| 3 | days_list | TEXT[] | - | DEFAULT '{}' |
| 4 | start_time | VARCHAR | 20 | NULLABLE |
| 5 | end_time | VARCHAR | 20 | NULLABLE |
| 6 | teacher_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `users.id` |
| 7 | classroom_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `classrooms.id`, ON DELETE CASCADE |
| 8 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 9 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `classroom_id`, `teacher_id`.

---

#### 10) `chapters` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | subject_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `subjects.id`, ON DELETE CASCADE |
| 3 | name | VARCHAR | 255 | NOT NULL |
| 4 | description | TEXT | - | NULLABLE |
| 5 | chapter_number | INTEGER | 11 | NULLABLE |
| 6 | display_order | INTEGER | 11 | NOT NULL, DEFAULT 0 |
| 7 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 8 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 9 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
|10 | uk_chapter_subject_name | - | - | UNIQUE(subject_id, name) |

Indexes: `subject_id`, `display_order`, `is_active`.

---

#### 11) `topics` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | chapter_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `chapters.id`, ON DELETE CASCADE |
| 3 | name | VARCHAR | 255 | NOT NULL |
| 4 | description | TEXT | - | NULLABLE |
| 5 | display_order | INTEGER | 11 | NOT NULL, DEFAULT 0 |
| 6 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
| 7 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 8 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `chapter_id`, `display_order`, `is_active`.

---

#### 12) `notes` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | chapter_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `chapters.id`, ON DELETE CASCADE |
| 3 | title | VARCHAR | 255 | NOT NULL |
| 4 | description | TEXT | - | NULLABLE |
| 5 | file_url | VARCHAR | 500 | NULLABLE |
| 6 | file_name | VARCHAR | 255 | NULLABLE |
| 7 | file_type | VARCHAR | 50 | NULLABLE |
| 8 | file_size | BIGINT | 20 | NULLABLE |
| 9 | uploaded_by | BIGINT | 20 | NOT NULL, FOREIGN KEY → `users.id`, ON DELETE CASCADE |
|10 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
|11 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
|12 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `chapter_id`, `uploaded_by`, `is_active`.

---

#### 13) `videos` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | chapter_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `chapters.id`, ON DELETE CASCADE |
| 3 | title | VARCHAR | 255 | NOT NULL |
| 4 | description | TEXT | - | NULLABLE |
| 5 | video_url | VARCHAR | 500 | NOT NULL |
| 6 | video_type | VARCHAR | 50 | NULLABLE |
| 7 | thumbnail_url | VARCHAR | 500 | NULLABLE |
| 8 | duration | INTEGER | 11 | NULLABLE |
| 9 | uploaded_by | BIGINT | 20 | NOT NULL, FOREIGN KEY → `users.id`, ON DELETE CASCADE |
|10 | is_active | BOOLEAN | 1 | NOT NULL, DEFAULT TRUE |
|11 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
|12 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `chapter_id`, `uploaded_by`, `is_active`.

---

#### 14) `assignments` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | title | VARCHAR | 255 | NOT NULL |
| 3 | description | TEXT | - | NULLABLE |
| 4 | due_date | DATE | - | NULLABLE |
| 5 | file | VARCHAR | 255 | NULLABLE |
| 6 | total_marks | INTEGER | 11 | NULLABLE |
| 7 | subject_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `subjects.id`, ON DELETE CASCADE |
| 8 | teacher_id | BIGINT | 20 | FOREIGN KEY → `users.id`, NULLABLE |
| 9 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
|10 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `subject_id`, `teacher_id`.

---

#### 15) `assignment_submissions` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | assignment_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `assignments.id`, ON DELETE CASCADE |
| 3 | user_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `users.id`, ON DELETE CASCADE |
| 4 | file | VARCHAR | 255 | NULLABLE |
| 5 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 6 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 7 | marks | INTEGER | 11 | NULLABLE |
| 8 | grade | VARCHAR | 10 | NULLABLE |
| 9 | review | TEXT | - | NULLABLE |
|10 | status | VARCHAR | 20 | DEFAULT 'pending' |

Indexes: `assignment_id`, `user_id`.

---

#### 16) `notifications` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | assignment_id | BIGINT | 20 | FOREIGN KEY → `assignments.id`, NULLABLE, ON DELETE SET NULL |
| 3 | teacher_id | BIGINT | 20 | FOREIGN KEY → `users.id`, NULLABLE, ON DELETE SET NULL |
| 4 | user_id | BIGINT | 20 | FOREIGN KEY → `users.id`, NULLABLE, ON DELETE SET NULL |
| 5 | message | TEXT | - | NULLABLE |
| 6 | read_at | TIMESTAMP | - | NULLABLE |
| 7 | created_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 8 | updated_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |

Indexes: `assignment_id`, `teacher_id`, `user_id`.

---

#### 17) `join_requests` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | student_user_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `users.id`, ON DELETE CASCADE |
| 3 | classroom_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `classrooms.id`, ON DELETE CASCADE |
| 4 | status | VARCHAR | 20 | NOT NULL, DEFAULT 'PENDING' |
| 5 | approved_by_teacher_id | BIGINT | 20 | FOREIGN KEY → `users.id`, NULLABLE, ON DELETE SET NULL |
| 6 | requested_at | TIMESTAMP | - | NOT NULL, DEFAULT NOW() |
| 7 | decided_at | TIMESTAMP | - | NULLABLE |

Index: `(student_user_id, classroom_id)`.

---

#### 18) `active_storage_blobs` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | key | VARCHAR | - | NOT NULL |
| 3 | filename | VARCHAR | - | NOT NULL |
| 4 | content_type | VARCHAR | - | NULLABLE |
| 5 | metadata | TEXT | - | NULLABLE |
| 6 | service_name | VARCHAR | - | NOT NULL |
| 7 | byte_size | BIGINT | 20 | NOT NULL |
| 8 | checksum | VARCHAR | - | NULLABLE |
| 9 | created_at | TIMESTAMP | - | NOT NULL |

Unique index: `key`.

---

#### 19) `active_storage_attachments` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | name | VARCHAR | - | NOT NULL |
| 3 | record_type | VARCHAR | - | NOT NULL |
| 4 | record_id | BIGINT | 20 | NOT NULL |
| 5 | blob_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `active_storage_blobs.id` |
| 6 | created_at | TIMESTAMP | - | NOT NULL |

Indexes: `blob_id`, UNIQUE `(record_type, record_id, name, blob_id)`.

---

#### 20) `active_storage_variant_records` Table

| Sr. No | Name       | Type       | Length | Constraints |
|--------|------------|------------|--------|-------------|
| 1 | id | BIGSERIAL | 20 | PRIMARY KEY |
| 2 | blob_id | BIGINT | 20 | NOT NULL, FOREIGN KEY → `active_storage_blobs.id` |
| 3 | variation_digest | VARCHAR | - | NOT NULL |

Unique index: `(blob_id, variation_digest)`.

---

**Verification Hint:** The schema originates from `src/main/resources/schema.sql`. Use `\d table_name` (psql) or database inspector tools to validate column definitions before deployment.

