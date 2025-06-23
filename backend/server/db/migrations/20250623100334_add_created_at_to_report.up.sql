alter table report_users add column created_at timestamp not null default now();
alter table report_posts add column created_at timestamp not null default now();

ALTER TABLE report_users ALTER COLUMN created_at DROP DEFAULT;
ALTER TABLE report_posts ALTER COLUMN created_at DROP DEFAULT;
