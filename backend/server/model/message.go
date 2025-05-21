package model

import (
	"time"
)

type Conversation struct {
	ID        string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	BuyerId   string `gorm:"type:uuid"`
	SellerId  string `gorm:"type:uuid"`
	PostId    string `gorm:"type:uuid"`
	CreatedAt time.Time

	Buyer  *User
	Seller *User
	Post   *Post
}

type Message struct {
	ID             string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	ConversationId string `gorm:"type:uuid"`
	SenderId       string `gorm:"type:uuid"`
	Content        string
	CreatedAt      time.Time

	Conversation *Conversation
	Sender       *User
}
