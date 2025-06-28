package model

import (
	"fmt"
	"time"
)

type OrderStatus string

const (
	OrderStatusPending    OrderStatus = "pending"
	OrderStatusProcessing OrderStatus = "processing"
	OrderStatusShipping   OrderStatus = "shipping"
	OrderStatusCompleted  OrderStatus = "completed"
	OrderStatusCancelled  OrderStatus = "cancelled"
)

const (
	PaymentPending  = "pending"
	PaymentPaid     = "paid"
	PaymentFailed   = "failed"
	PaymentCanceled = "canceled"
)

func ParseOrderStatus(s string) (OrderStatus, error) {
	switch s {
	case string(OrderStatusPending),
		string(OrderStatusProcessing),
		string(OrderStatusShipping),
		string(OrderStatusCompleted),
		string(OrderStatusCancelled):
		return OrderStatus(s), nil
	default:
		return "", fmt.Errorf("invalid order status: %s", s)
	}
}

type ShopOrder struct {
	ID              string      `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserId          *string     `gorm:"type:uuid" json:"user_id"`
	PostId          *string     `gorm:"type:uuid" json:"post_id"`
	PaymentMethodId *string     `gorm:"type:uuid" json:"payment_method_id"`
	Status          OrderStatus `json:"status"`
	AddressId       *string     `gorm:"type:uuid" json:"address_id"`
	Total           int         `json:"total"`
	CreatedAt       time.Time   `json:"created_at"`
	CompletedAt     *time.Time  `json:"completed_at"`
	CanceledAt      *time.Time  `json:"canceled_at"`

	// --- ZaloPay Fields ---
	ZaloAppTransID   *string    `json:"zalo_app_trans_id,omitempty"` // nullable
	ZaloTransID      *string    `json:"zalo_trans_id,omitempty"`     // nullable
	PaymentStatus    string     `json:"payment_status"`
	PaidAt           *time.Time `json:"paid_at,omitempty"`            // nullable
	ZaloCallbackData *string    `json:"zalo_callback_data,omitempty"` // nullable

	User          *User          `json:"user,omitempty"`
	Post          *Post          `json:"post,omitempty"`
	Address       *Address       `json:"address,omitempty"`
	PaymentMethod *PaymentMethod `json:"payment_method,omitempty"`
}

type UserReview struct {
	UserId    *string   `gorm:"type:uuid;primaryKey" json:"user_id"`
	OrderId   *string   `gorm:"type:uuid;primaryKey" json:"order_id"`
	Rating    int       `json:"rating"`
	Comment   string    `json:"comment"`
	CreatedAt time.Time `json:"created_at"`

	User  *User      `json:"user,omitempty"`
	Order *ShopOrder `json:"order,omitempty"`
}
