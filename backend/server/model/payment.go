package model

import "time"

type TransactionStatus string

const (
	TransactionStatusPending   TransactionStatus = "pending"
	TransactionStatusCompleted TransactionStatus = "completed"
	TransactionStatusFailed    TransactionStatus = "failed"
)

type PaymentMethod struct {
	ID   string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Name string `json:"name"`
}

type Transaction struct {
	ID                    string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	OrderID               string    `gorm:"type:uuid" json:"order_id"`
	UserID                string    `gorm:"type:uuid" json:"user_id"`
	StripePaymentIntentID string    `json:"stripe_payment_intent_id"`
	Amount                int       `json:"amount"`
	Status                string    `json:"status"`
	PaymentMethodID       string    `gorm:"type:uuid" json:"payment_method_id"`
	ErrorMessage          string    `json:"error_message"`
	CreatedAt             time.Time `json:"created_at"`

	Order         *ShopOrder     `json:"order,omitempty"`
	User          *User          `json:"user,omitempty"`
	PaymentMethod *PaymentMethod `json:"payment_method,omitempty"`
}
