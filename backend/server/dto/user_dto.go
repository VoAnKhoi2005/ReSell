package dto

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"time"
)

type UserStatDTO struct {
	UserID                 string     `json:"user_id"`
	Username               string     `json:"username"`
	AvatarURL              string     `json:"avatar_url"`
	CoverURL               string     `json:"cover_url"`
	ChatResponsePercentage *float64   `json:"chat_response_percentage"`
	Reputation             int        `json:"reputation"`
	PostNumber             uint       `json:"post_number"`
	BoughtNumber           uint       `json:"bought_number"`
	SaleNumber             uint       `json:"sale_number"`
	ReportNumber           uint       `json:"report_number"`
	TotalRevenue           uint       `json:"total_revenue"`
	LastActivity           *time.Time `json:"last_activity,omitempty"`
	AverageRating          float64    `json:"average_rating"`
	ReviewNumber           uint       `json:"review_number"`
	AveragePostPrice       float64    `json:"average_post_price"`
	FollowerCount          uint       `json:"follower_count"`
	FolloweeCount          uint       `json:"followee_count"`
	CreatedAt              time.Time  `json:"created_at"`
}

type BuyerProfile struct {
	UserID               string          `json:"user_id"`
	AvgPriceLiked        float64         `json:"avg_price_liked"`
	PreferredCategories  []string        `json:"preferred_categories"`
	Addresses            []model.Address `json:"addresses"`
	LikedPostTitle       []string        `json:"liked_post_title"`
	LikedPostDescription []string        `json:"liked_post_description"`
}
