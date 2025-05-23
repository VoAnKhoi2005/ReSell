package model

import (
	"time"
)

type PaymentMethod struct {
	ID   string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	Name string
}

type Wallet struct {
	ID            string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	UserID        *string `gorm:"type:uuid"`
	Balance       uint
	FrozenBalance uint
	CreatedAt     time.Time
	UpdatedAt     time.Time

	User *User
}

type WalletTransaction struct {
	ID           string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	WalletId     *string `gorm:"type:uuid"`
	Type         string
	Amount       int
	BalanceAfter int
	OrderId      *string `gorm:"type:uuid"`
	CreatedAt    time.Time

	Wallet *Wallet
	Order  *ShopOrder
}
