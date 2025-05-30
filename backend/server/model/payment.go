package model

import "time"

type PaymentMethod struct {
	ID   string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Name string `json:"name"`
}

type Wallet struct {
	ID            string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserID        *string   `gorm:"type:uuid" json:"user_id"`
	Balance       uint      `json:"balance"`
	FrozenBalance uint      `json:"frozen_balance"`
	CreatedAt     time.Time `json:"created_at"`
	UpdatedAt     time.Time `json:"updated_at"`

	User *User `json:"user,omitempty"`
}

type WalletTransaction struct {
	ID           string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	WalletId     *string   `gorm:"type:uuid" json:"wallet_id"`
	Type         string    `json:"type"`
	Amount       int       `json:"amount"`
	BalanceAfter int       `json:"balance_after"`
	OrderId      *string   `gorm:"type:uuid" json:"order_id"`
	CreatedAt    time.Time `json:"created_at"`

	Wallet *Wallet    `json:"wallet,omitempty"`
	Order  *ShopOrder `json:"order,omitempty"`
}
