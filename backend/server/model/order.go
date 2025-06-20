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
	ID              string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	UserId          *string `gorm:"type:uuid"`
	PostId          *string `gorm:"type:uuid"`
	PaymentMethodId *string `gorm:"type:uuid"`
	Status          OrderStatus
	AddressId       *string `gorm:"type:uuid"`
	Total           int
	CreatedAt       time.Time
	CompletedAt     *time.Time
	CanceledAt      *time.Time

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
