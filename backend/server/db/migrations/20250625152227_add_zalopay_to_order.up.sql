ALTER TABLE shop_orders
    ADD COLUMN zalo_app_trans_id  VARCHAR(255) UNIQUE,
    ADD COLUMN zalo_trans_id      VARCHAR(255),
    ADD COLUMN payment_status     VARCHAR(50) NOT NULL DEFAULT 'pending',
    ADD COLUMN paid_at            TIMESTAMP,
    ADD COLUMN zalo_callback_data TEXT;