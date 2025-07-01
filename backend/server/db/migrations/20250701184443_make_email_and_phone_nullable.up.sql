-- Make email and phone fields nullable

ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN phone DROP NOT NULL;
