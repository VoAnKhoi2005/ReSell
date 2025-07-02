CREATE INDEX idx_posts_created_status ON posts (status, created_at DESC);
CREATE INDEX idx_favorite_posts_post_id ON favorite_posts (post_id);
CREATE INDEX idx_post_images_post_id ON post_images (post_id);
CREATE INDEX idx_users_reputation ON users (id, reputation);
CREATE INDEX idx_addresses_user_id ON addresses (user_id);
CREATE INDEX idx_wards_id ON wards (id);
CREATE INDEX idx_districts_id ON districts (id);