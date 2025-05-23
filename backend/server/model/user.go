package model

import (
	"gorm.io/gorm"
	"time"
)

type Admin struct {
	ID        string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	Username  string
	Email     string
	Password  string
	CreatedAt time.Time
	UpdatedAt time.Time
	DeletedAt gorm.DeletedAt `gorm:"index"`
}

type User struct {
	ID         string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
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
	CreatedAt  time.Time
	UpdatedAt  time.Time
}

type Follow struct {
	SellerId *string `gorm:"type:uuid;primaryKey"`
	BuyerId  *string `gorm:"type:uuid;primaryKey"`

	Seller *User
	Buyer  *User
}

type Notification struct {
	ID          string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	UserID      *string `gorm:"type:uuid"`
	Title       string
	Description string
	IsRead      bool
	CreatedAt   time.Time

	User *User
}
