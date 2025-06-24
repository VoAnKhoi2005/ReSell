package dto

import "time"

type ConversationStatDTO struct {
	ConversationID string    `json:"conversation_id"`
	SellerID       string    `json:"seller_id"`
	SellerUsername string    `json:"seller_username"`
	SellerAvatar   *string   `json:"seller_avatar"`
	BuyerID        string    `json:"buyer_id"`
	BuyerUsername  string    `json:"buyer_username"`
	BuyerAvatar    *string   `json:"buyer_avatar"`
	PostID         string    `json:"post_id"`
	PostTitle      string    `json:"post_title"`
	PostThumbnail  string    `json:"post_thumbnail"`
	LastMessage    string    `json:"last_message"`
	CreatedAt      time.Time `json:"created_at"`
	LastUpdatedAt  time.Time `json:"last_updated_at"`
}
