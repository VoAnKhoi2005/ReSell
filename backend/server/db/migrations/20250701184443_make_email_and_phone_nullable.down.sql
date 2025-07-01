-- Revert email and phone fields to NOT NULL

ALTER TABLE users
    ALTER COLUMN email SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN phone SET NOT NULL;
