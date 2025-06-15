package model

import "time"

type SubscriptionPlan struct {
	ID            string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Name          string `json:"name"`
	Description   string `gorm:"type:text" json:"description"`
	Duration      int    `json:"duration"`
	StripePriceID string `json:"stripe_price_id"`
}

type UserSubscription struct {
	ID                   string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserID               string    `gorm:"type:uuid" json:"user_id"`
	PlanID               string    `gorm:"type:uuid" json:"subscription_id"`
	StartAt              time.Time `json:"start_at"`
	EndAt                time.Time `json:"end_at"`
	IsActive             bool      `json:"is_active"`
	StripeSubscriptionID string    `json:"stripe_subscription_id"`

	User *User             `json:"user"`
	Plan *SubscriptionPlan `json:"plan"`
}
