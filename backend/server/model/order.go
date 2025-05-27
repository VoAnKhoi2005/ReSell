package model

import (
	"time"
)

type OrderStatus string

const (
	OrderStatusOrdered   OrderStatus = "ordered"
	OrderStatusPending   OrderStatus = "pending"
	OrderStatusRejected  OrderStatus = "rejected"
	OrderStatusSold      OrderStatus = "completed"
	OrderStatusShipping  OrderStatus = "shipping"
	OrderStatusCancelled OrderStatus = "cancelled"
	OrderStatusProcessed OrderStatus = "processed"
)

type ShopOrder struct {
	ID              string     `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserId          *string    `gorm:"type:uuid" json:"user_id"`
	PostId          *string    `gorm:"type:uuid" json:"post_id"`
	PaymentMethodId *string    `gorm:"type:uuid" json:"payment_method_id"`
	Status          string     `json:"status"`
	AddressId       *string    `gorm:"type:uuid" json:"address_id"`
	Total           int        `json:"total"`
	CreatedAt       time.Time  `json:"created_at"`
	CompletedAt     *time.Time `json:"completed_at,omitempty"`
	CanceledAt      *time.Time `json:"canceled_at,omitempty"`

	User          *User          `json:"user,omitempty"`
	Post          *Post          `json:"post,omitempty"`
	PaymentMethod *PaymentMethod `json:"payment_method,omitempty"`
	Address       *Address       `json:"address,omitempty"`
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
