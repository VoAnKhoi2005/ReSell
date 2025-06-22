alter table users
add column is_selling boolean default false,
add column stripe_account_id varchar,
add column is_stripe_verified boolean default false,
add column avatar_url varchar;