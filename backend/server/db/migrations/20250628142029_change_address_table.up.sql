ALTER TABLE addresses
    ADD COLUMN fullname VARCHAR(255),
    ADD COLUMN phone VARCHAR(10);

ALTER TABLE follows RENAME COLUMN seller_id TO follower_id;
ALTER TABLE follows RENAME COLUMN buyer_id TO followee_id;
