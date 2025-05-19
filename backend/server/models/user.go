package models

import (
	"gorm.io/gorm"
	"time"
)

type Admin struct {
	gorm.Model
	Username string
	Email    string
	Password string
}

type User struct {
	gorm.Model
	Username   string
	Email      string
	Phone      string
	Password   string
	Fullname   string
	CitizenId  string
	Status     string
	Reputation int
	BanStart   time.Time
	BanEnd     time.Time
}

type Follow struct {
	SellerId string `gorm:"primaryKey"`
	BuyerId  string `gorm:"primaryKey"`

	Seller User
	Buyer  User
}
