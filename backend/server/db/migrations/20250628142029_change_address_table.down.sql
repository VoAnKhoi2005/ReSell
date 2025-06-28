-- Remove the added columns from the addresses table
ALTER TABLE addresses DROP COLUMN fullname;
ALTER TABLE addresses DROP COLUMN phone;

-- Rename the columns in the follows table back to original
ALTER TABLE follows RENAME COLUMN follower_id TO seller_id;
ALTER TABLE follows RENAME COLUMN followee_id TO buyer_id;