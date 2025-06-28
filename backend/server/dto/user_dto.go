package dto

import "time"

type UserStatDTO struct {
	UserID           string     `json:"user_id"`
	Reputation       int        `json:"reputation"`
	PostNumber       uint       `json:"post_number"`
	BoughtNumber     uint       `json:"bought_number"`
	SaleNumber       uint       `json:"sale_number"`
	ReportNumber     uint       `json:"report_number"`
	TotalRevenue     uint       `json:"total_revenue"`
	LastActivity     *time.Time `json:"last_activity,omitempty"`
	AverageRating    float64    `json:"average_rating"`
	ReviewNumber     uint       `json:"review_number"`
	AveragePostPrice float64    `json:"average_post_price"`
	FollowerCount    uint       `json:"follower_count"`
	FolloweeCount    uint       `json:"followee_count"`
}
