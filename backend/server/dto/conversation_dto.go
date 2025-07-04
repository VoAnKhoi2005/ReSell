package dto

import "time"

type ConversationStatDTO struct {
	ConversationID string    `json:"conversation_id"`
	SellerID       string    `json:"seller_id"`
	SellerFullName string    `json:"seller_full_name"`
	SellerAvatar   *string   `json:"seller_avatar"`
	BuyerID        string    `json:"buyer_id"`
	BuyerFullName  string    `json:"buyer_full_name"`
	BuyerAvatar    *string   `json:"buyer_avatar"`
	PostID         string    `json:"post_id"`
	PostTitle      string    `json:"post_title"`
	PostThumbnail  string    `json:"post_thumbnail"`
	IsPostSold     bool      `json:"is_post_sold"`
	LastMessage    string    `json:"last_message"`
	CreatedAt      time.Time `json:"created_at"`
	LastUpdatedAt  time.Time `json:"last_updated_at"`
}
