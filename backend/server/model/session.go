package model

import (
	"github.com/gorilla/websocket"
)

type Session struct {
	UserID     string
	WS         *websocket.Conn
	LastAction int64
	Send       chan any
	Stop       chan any
}
