-- Xóa các bảng phụ thuộc trước roi toi cac bang con lai
DROP TABLE IF EXISTS user_reviews;
DROP TABLE IF EXISTS user_subscriptions;
DROP TABLE IF EXISTS subscription_plans;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS conversations;
DROP TABLE IF EXISTS report_posts;
DROP TABLE IF EXISTS report_users;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS favorite_posts;
DROP TABLE IF EXISTS post_images;
DROP TABLE IF EXISTS shop_orders;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS wards;
DROP TABLE IF EXISTS districts;
DROP TABLE IF EXISTS provinces;
DROP TABLE IF EXISTS community_participants;
DROP TABLE IF EXISTS communities;
DROP TABLE IF EXISTS follows;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS payment_methods;

-- Xóa các ENUM nếu có
DROP TYPE IF EXISTS user_status;
DROP TYPE IF EXISTS post_status;
DROP TYPE IF EXISTS order_status;
DROP TYPE IF EXISTS transaction_status;
DROP TYPE IF EXISTS notification_type;
DROP TYPE IF EXISTS community_status;
DROP TYPE IF EXISTS community_type;
DROP TYPE IF EXISTS community_role;




