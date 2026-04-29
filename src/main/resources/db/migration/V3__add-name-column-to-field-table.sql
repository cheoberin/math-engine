ALTER TABLE field
    ADD COLUMN name VARCHAR(255);

UPDATE field
SET name = external_key
WHERE name IS NULL;

ALTER TABLE field
    ALTER COLUMN name SET NOT NULL;
