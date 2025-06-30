-- 1. Drop new foreign key
ALTER TABLE posts DROP CONSTRAINT IF EXISTS posts_ward_id_fkey;

-- 2. Rename the column back
ALTER TABLE posts RENAME COLUMN ward_id TO address_id;

-- 3. Restore old foreign key
ALTER TABLE posts
    ADD CONSTRAINT posts_address_id_fkey
        FOREIGN KEY (address_id) REFERENCES addresses(id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;
