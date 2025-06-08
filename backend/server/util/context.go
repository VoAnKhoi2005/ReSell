package util

import (
	"context"
	"time"
)

const DefaultDBTimeout = 5 * time.Second
const DefaultRedisTimeout = 2 * time.Second

func NewDBContext() (context.Context, context.CancelFunc) {
	return context.WithTimeout(context.Background(), DefaultDBTimeout)
}

func NewRedisContext() (context.Context, context.CancelFunc) {
	return context.WithTimeout(context.Background(), DefaultRedisTimeout)
}
