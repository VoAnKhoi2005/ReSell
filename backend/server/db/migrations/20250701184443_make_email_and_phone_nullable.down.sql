-- Revert email and phone fields to NOT NULL
UPDATE users SET email = 'unknown@example.com' WHERE email IS NULL;
UPDATE users SET phone = '0000000000' WHERE phone IS NULL;

ALTER TABLE users
    ALTER COLUMN email SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN phone SET NOT NULL;
