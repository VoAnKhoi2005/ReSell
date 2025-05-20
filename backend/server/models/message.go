package models

import (
	"time"
)

type Conversation struct {
	ID        uint
	BuyerId   string
	SellerId  string
	PostId    string
	CreatedAt time.Time

	Buyer  *User
	Seller *User
	Post   *Post
}

type Message struct {
	ID             uint
	ConversationId string
	SenderId       string
	Content        string
	CreatedAt      time.Time

	Conversation *Conversation
	Sender       *User
}
