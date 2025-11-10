-- Migration script to update classroom uniqueness constraint
-- This allows classrooms with the same name but different batch/year combinations

-- Step 1: Drop the existing unique constraint on name column
-- Note: The constraint name may vary depending on your database
-- For PostgreSQL, you can find the constraint name with:
-- SELECT constraint_name FROM information_schema.table_constraints 
-- WHERE table_name = 'classrooms' AND constraint_type = 'UNIQUE';

-- If the constraint was created automatically, it might be named 'classrooms_name_key'
-- Adjust the constraint name based on your actual database

ALTER TABLE classrooms DROP CONSTRAINT IF EXISTS classrooms_name_key;
ALTER TABLE classrooms DROP CONSTRAINT IF EXISTS classrooms_name_unique;
ALTER TABLE classrooms DROP CONSTRAINT IF EXISTS uk_classroom_name;

-- Step 2: Add the new composite unique constraint on (name, batch, year)
-- This allows the same classroom name with different batch/year combinations
ALTER TABLE classrooms 
ADD CONSTRAINT uk_classroom_name_batch_year UNIQUE (name, batch, year);

-- Note: In PostgreSQL, NULL values in unique constraints are handled specially
-- Multiple rows with NULL in any column of the unique constraint are allowed
-- This means you can have multiple classrooms with the same name if batch or year is NULL

