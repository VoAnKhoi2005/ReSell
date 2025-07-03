package model

import (
	"time"
)

type Conversation struct {
	ID        string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	BuyerId   *string   `gorm:"type:uuid" json:"buyer_id"`
	SellerId  *string   `gorm:"type:uuid" json:"seller_id"`
	PostId    *string   `gorm:"type:uuid" json:"post_id"`
	IsSelling bool      `gorm:"type:bool" json:"is_selling"`
	Offer     *int      `gorm:"type:int" json:"offer"`
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
	ACKMessage      SocketMessageType = "ack_message"
	NewMessage      SocketMessageType = "new_message"
	TypingIndicator SocketMessageType = "typing"
	InChatIndicator SocketMessageType = "in_chat"
	ErrorMessage    SocketMessageType = "error"
)

type SocketMessage struct {
	Type SocketMessageType `json:"type"`
	Data interface{}       `json:"data"`
}

type NewMessagePayload struct {
	TempMessageID  string `json:"temp_message_id"`
	ConversationId string `json:"conversation_id"`
	Content        string `json:"content"`
}

type ACKMessagePayload struct {
	TempMessageID string  `json:"temp_message_id"`
	Message       Message `json:"message"`
}

type TypingIndicatorPayload struct {
	ConversationId string `json:"conversation_id"`
	UserId         string `json:"user_id"`
	IsTyping       bool   `json:"is_typing"`
}

type InChatIndicatorPayload struct {
	TempMessageID  string `json:"temp_message_id"`
	ConversationId string `json:"conversation_id"`
	IsInChat       bool   `json:"is_in_chat"`
}

type ErrorPayload struct {
	TempMessageID *string `json:"temp_message_id,omitempty"`
	Error         string  `json:"error"`
}
