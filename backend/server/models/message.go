package models

import (
	"gorm.io/gorm"
	"time"
)

type Conversation struct {
	gorm.Model
	BuyerId   string
	SellerId  string
	PostId    string
	CreatedAt time.Time

	Buyer  User
	Seller User
	Post   Post
}

type Message struct {
	gorm.Model
	ConversationId string
	SenderId       string
	Content        string
	CreatedAt      time.Time

	Conversation Conversation
	Sender       User
}
