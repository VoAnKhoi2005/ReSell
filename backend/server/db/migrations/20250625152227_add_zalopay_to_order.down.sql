ALTER TABLE shop_orders
    DROP COLUMN zalo_callback_data,
    DROP COLUMN paid_at,
    DROP COLUMN payment_status,
    DROP COLUMN zalo_trans_id,
    DROP COLUMN zalo_app_trans_id;