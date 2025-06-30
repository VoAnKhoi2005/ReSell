ALTER TABLE posts RENAME COLUMN address_id TO ward_id;

ALTER TABLE posts DROP CONSTRAINT IF EXISTS posts_address_id_fkey;

ALTER TABLE posts
    ADD CONSTRAINT posts_ward_id_fkey
        FOREIGN KEY (ward_id) REFERENCES wards(id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;
