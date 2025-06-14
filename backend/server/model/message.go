package model

import (
	"encoding/json"
	"time"
)

type Conversation struct {
	ID        string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	BuyerId   *string   `gorm:"type:uuid" json:"buyer_id"`
	SellerId  *string   `gorm:"type:uuid" json:"seller_id"`
	PostId    *string   `gorm:"type:uuid" json:"post_id"`
	CreatedAt time.Time `json:"created_at"`

	Buyer  *User `json:"buyer,omitempty"`
	Seller *User `json:"seller,omitempty"`
	Post   *Post `json:"post,omitempty"`
}

type Message struct {
	ID             string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	ConversationId *string   `gorm:"type:uuid" json:"conversation_id"`
	SenderId       *string   `gorm:"type:uuid" json:"sender_id"`
	Content        string    `json:"content"`
	CreatedAt      time.Time `json:"created_at"`

	Conversation *Conversation `json:"conversation,omitempty"`
	Sender       *User         `json:"sender,omitempty"`
}

type SocketMessageType string

const (
	MessageSend     SocketMessageType = "message_send"
	NewMessage      SocketMessageType = "new_message"
	TypingIndicator SocketMessageType = "typing"
	ErrorMessage    SocketMessageType = "error"
)

type IncomingSocketMessage struct {
	Type SocketMessageType `json:"type"`
	Data json.RawMessage   `json:"data"`
}

type SendMessagePayload struct {
	ConversationId string `json:"conversation_id"`
	Content        string `json:"content"`
}

type TypingPayload struct {
	ConversationId string `json:"conversation_id"`
	IsTyping       bool   `json:"is_typing"`
}
