-- Kích hoạt extension để tạo UUID tự động
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Bảng Admins
CREATE TABLE admins
(
    id         UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),
    username   VARCHAR   NOT NULL,
    email      VARCHAR   NOT NULL,
    password   VARCHAR   NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Enum cho trạng thái user
CREATE TYPE user_status AS ENUM ('active', 'banned');

-- Bảng Users
CREATE TABLE users
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),
    username   VARCHAR     NOT NULL,
    email      VARCHAR     NOT NULL,
    phone      VARCHAR     NOT NULL,
    password_hash   VARCHAR     NOT NULL,
    firebase_uid VARCHAR,
    auth_provider VARCHAR,
    fullname   VARCHAR     NOT NULL,
    status     user_status NOT NULL,
    reputation INT         NOT NULL DEFAULT 0,
    ban_start  TIMESTAMP,
    ban_end    TIMESTAMP,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Follows (mỗi cặp seller-buyer chỉ xuất hiện 1 lần)
CREATE TABLE follows
(
    seller_id UUID NOT NULL,
    buyer_id  UUID NOT NULL,
    PRIMARY KEY (seller_id, buyer_id),
    FOREIGN KEY (seller_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ENUMs
CREATE TYPE community_status AS ENUM ('active', 'banned');
CREATE TYPE community_type AS ENUM ('public', 'private');
CREATE TYPE community_role AS ENUM ('owner', 'participant');

-- TABLE: communities
CREATE TABLE communities
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR          NOT NULL,
    description TEXT,
    status      community_status NOT NULL,
    type        community_type   NOT NULL,
    created_at  TIMESTAMP        NOT NULL
);

-- TABLE: community_participants
CREATE TABLE community_participants
(
    user_id      UUID           NOT NULL,
    community_id UUID           NOT NULL,
    role         community_role NOT NULL,
    created_at   TIMESTAMP      NOT NULL,

    PRIMARY KEY (user_id, community_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (community_id) REFERENCES communities (id)
);

-- TABLE: provinces
CREATE TABLE provinces
(
    id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR NOT NULL
);

-- TABLE: districts
CREATE TABLE districts
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR NOT NULL,
    province_id UUID,
    FOREIGN KEY (province_id) REFERENCES provinces (id)
);

-- TABLE: wards
CREATE TABLE wards
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR NOT NULL,
    district_id UUID,
    FOREIGN KEY (district_id) REFERENCES districts (id)
);

-- TABLE: addresses
CREATE TABLE addresses
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID,
    ward_id    UUID,
    detail     VARCHAR NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (ward_id) REFERENCES wards (id)
);

-- ENUM: post_status
CREATE TYPE post_status AS ENUM (
    'pending',
    'approved',
    'rejected',
    'sold',
    'hidden',
    'deleted'
    );

-- TABLE: categories
CREATE TABLE categories
(
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    parent_category_id UUID,
    name               VARCHAR NOT NULL,

    FOREIGN KEY (parent_category_id) REFERENCES categories (id) ON DELETE CASCADE
);

-- TABLE: posts
CREATE TABLE posts
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID,
    category_id UUID,
    address_id  UUID,
    title       VARCHAR     NOT NULL,
    description TEXT        NOT NULL,
    price       INTEGER     NOT NULL,
    status      post_status NOT NULL,
    sold_at     TIMESTAMP,
    created_at  TIMESTAMP   NOT NULL,
    updated_at  TIMESTAMP   NOT NULL,
    deleted_at  TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (address_id) REFERENCES addresses (id)
);

-- TABLE: post_images
CREATE TABLE post_images
(
    post_id     UUID    NOT NULL,
    image_url   TEXT    NOT NULL,
    image_order INTEGER NOT NULL DEFAULT 0,

    PRIMARY KEY (post_id, image_url),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);

-- TABLE: favorite_posts
CREATE TABLE favorite_posts
(
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,

    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);


-- TABLE: payment_methods
CREATE TABLE payment_methods
(
    id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR NOT NULL
);

-- ENUM: order_status
CREATE TYPE order_status AS ENUM (
    'pending',
    'processing',
    'shipping',
    'completed',
    'cancelled'
    );

-- TABLE: shop_orders
CREATE TABLE shop_orders
(
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id           UUID,
    post_id           UUID,
    payment_method_id UUID,
    status            order_status NOT NULL,
    address_id        UUID,
    total             INTEGER      NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    completed_at      TIMESTAMP,
    canceled_at       TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id),
    FOREIGN KEY (address_id) REFERENCES addresses (id)
);

-- TABLE: user_reviews
CREATE TABLE user_reviews
(
    user_id    UUID      NOT NULL,
    order_id   UUID      NOT NULL,
    rating     INTEGER   NOT NULL,
    comment    TEXT      ,
    created_at TIMESTAMP NOT NULL,

    PRIMARY KEY (user_id, order_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (order_id) REFERENCES shop_orders (id)
);

-- ENUM: transaction_status
CREATE TYPE transaction_status AS ENUM (
    'pending',
    'completed',
    'failed'
    );


-- TABLE: transactions
CREATE TABLE transactions
(
    id                       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id                 UUID               NOT NULL,
    user_id                  UUID               NOT NULL,
    stripe_payment_intent_id TEXT               NOT NULL,
    amount                   INTEGER            NOT NULL,
    status                   transaction_status NOT NULL,
    payment_method_id        UUID               NOT NULL,
    error_message            TEXT,
    created_at               TIMESTAMP          NOT NULL,

    FOREIGN KEY (order_id) REFERENCES shop_orders (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id)
);

-- TABLE: conversations
CREATE TABLE conversations
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    buyer_id   UUID,
    seller_id  UUID,
    post_id    UUID,
    created_at TIMESTAMP NOT NULL,

    FOREIGN KEY (buyer_id) REFERENCES users (id),
    FOREIGN KEY (seller_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id),

    UNIQUE (buyer_id, seller_id, post_id)
);

-- TABLE: messages
CREATE TABLE messages
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID,
    sender_id       UUID,
    content         TEXT      NOT NULL,
    created_at      TIMESTAMP NOT NULL,

    FOREIGN KEY (conversation_id) REFERENCES conversations (id),
    FOREIGN KEY (sender_id) REFERENCES users (id)
);

-- TABLE: report_users
CREATE TABLE report_users
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id UUID NOT NULL,
    reported_id UUID NOT NULL,
    description TEXT NOT NULL,

    FOREIGN KEY (reporter_id) REFERENCES users (id),
    FOREIGN KEY (reported_id) REFERENCES users (id)
);

-- TABLE: report_posts
CREATE TABLE report_posts
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id UUID NOT NULL,
    reported_id UUID NOT NULL,
    description TEXT NOT NULL,

    FOREIGN KEY (reporter_id) REFERENCES users (id),
    FOREIGN KEY (reported_id) REFERENCES posts (id)
);

-- TABLE: notifications
CREATE TABLE notifications
(
    id          UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),
    user_id     UUID,
    title       VARCHAR   NOT NULL,
    description TEXT,
    topic       VARCHAR,
    type        VARCHAR   NOT NULL,
    is_read     BOOLEAN   NOT NULL DEFAULT FALSE,
    is_sent     BOOLEAN   NOT NULL DEFAULT FALSE,
    is_silent   BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL,
    sent_at     TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- TABLE: subscription_plans
CREATE TABLE subscription_plans
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR NOT NULL,
    description     TEXT,
    duration        INT     NOT NULL, -- Thời gian theo đơn vị ngày (tuỳ app định nghĩa)
    stripe_price_id VARCHAR NOT NULL
);

-- TABLE: user_subscriptions
CREATE TABLE user_subscriptions
(
    id                     UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),
    user_id                UUID      NOT NULL,
    plan_id                UUID      NOT NULL,
    start_at               TIMESTAMP NOT NULL,
    end_at                 TIMESTAMP NOT NULL,
    is_active              BOOLEAN   NOT NULL DEFAULT TRUE,
    stripe_subscription_id VARCHAR   NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (plan_id) REFERENCES subscription_plans (id)
);
