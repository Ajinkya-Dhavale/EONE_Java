-- INSERT INTO roles (name, created_at, updated_at) VALUES
--     ('ADMIN', NOW(), NOW()),
--     ('teacher', NOW(), NOW()),
--     ('student', NOW(), NOW())
-- ON CONFLICT (name) DO UPDATE SET updated_at = EXCLUDED.updated_at;


-- SELECT * FROM roles;

UPDATE users
SET status = 1
WHERE id = 2;

UPDATE users
SET status = 1
WHERE id = 3;