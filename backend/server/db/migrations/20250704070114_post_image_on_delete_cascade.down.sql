ALTER TABLE post_images
    DROP CONSTRAINT IF EXISTS post_images_post_id_fkey;

ALTER TABLE post_images
    ADD CONSTRAINT post_images_post_id_fkey
        FOREIGN KEY (post_id)
            REFERENCES posts(id);
