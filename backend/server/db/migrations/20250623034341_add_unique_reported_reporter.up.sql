-- Add unique constraint to report_users
ALTER TABLE report_users
ADD CONSTRAINT unique_user_report UNIQUE (reporter_id, reported_id);

-- Add unique constraint to report_posts
ALTER TABLE report_posts
ADD CONSTRAINT unique_post_report UNIQUE (reporter_id, reported_id);
