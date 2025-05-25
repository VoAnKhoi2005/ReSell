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
	ID              string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	UserId          *string `gorm:"type:uuid"`
	PostId          *string `gorm:"type:uuid"`
	PaymentMethodId *string `gorm:"type:uuid"`
	Status          string
	AddressId       *string `gorm:"type:uuid"`
	Total           int
	CreatedAt       time.Time
	CompletedAt     *time.Time
	CanceledAt      *time.Time

	User          *User
	Post          *Post
	PaymentMethod *PaymentMethod
	Address       *Address
}

type UserReview struct {
	UserId    *string `gorm:"type:uuid;primaryKey"`
	OrderId   *string `gorm:"type:uuid;primaryKey"`
	Rating    int
	Comment   string
	CreatedAt time.Time

	User  *User
	Order *ShopOrder
}
