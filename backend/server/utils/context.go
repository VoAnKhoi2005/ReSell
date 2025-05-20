package utils

import (
	"context"
	"time"
)

const DefaultDBTimeout = 2 * time.Second

func NewDBContext() (context.Context, context.CancelFunc) {
	return context.WithTimeout(context.Background(), DefaultDBTimeout)
}
