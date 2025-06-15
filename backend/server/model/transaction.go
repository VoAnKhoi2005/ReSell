package model

import "time"

type TransactionStatus string

const (
	TransactionStatusPending   TransactionStatus = "pending"
	TransactionStatusCompleted TransactionStatus = "completed"
	TransactionStatusFailed    TransactionStatus = "failed"
)

type Transaction struct {
	ID                    string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	OrderID               string    `gorm:"type:uuid" json:"order_id"`
	UserID                string    `gorm:"type:uuid" json:"user_id"`
	StripePaymentIntentID string    `json:"stripe_payment_intent_id"`
	Amount                int       `json:"amount"`
	Status                string    `json:"status"`
	PaymentMethod         string    `json:"payment_method"`
	ErrorMessage          string    `json:"error_message"`
	CreatedAt             time.Time `json:"created_at"`

	Order *ShopOrder `json:"order"`
	User  *User      `json:"user"`
}
