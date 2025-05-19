package models

import (
	"gorm.io/gorm"
	"time"
)

type PaymentMethod struct {
	ID   uint
	Name string
}

type Wallet struct {
	gorm.Model
	UserID        uint
	Balance       uint
	FrozenBalance uint
	User          *User
}
type WalletTransactions struct {
	gorm.Model
	WalletId     string
	Type         string
	Amount       int
	BalanceAfter int
	OrderId      string
	CreatedAt    time.Time

	Wallet Wallet
	Order  ShopOrders
}
