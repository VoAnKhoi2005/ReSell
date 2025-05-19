package models

import (
	"gorm.io/gorm"
	"time"
)

type ShopOrder struct {
	gorm.Model
	UserId          string
	PostId          string
	PaymentMethodId string
	Status          string
	AddressId       string
	Total           int
	CreatedAt       time.Time
	CompletedAt     time.Time
	CanceledAt      time.Time

	User          User
	Post          Post
	PaymentMethod PaymentMethod
	Address       Address
}

type UserReview struct {
	UserId    string `gorm:"primaryKey"`
	OrderId   string `gorm:"primaryKey"`
	Rating    int
	Comment   string
	CreatedAt time.Time

	User  User
	Order ShopOrder
}
