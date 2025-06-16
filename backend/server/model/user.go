package model

import "time"

type Admin struct {
	ID        string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Username  string    `json:"username"`
	Email     string    `json:"email"`
	Password  string    `json:"password"`
	CreatedAt time.Time `json:"created_at"`
}

type UserStatus string

const (
	ActiveStatus UserStatus = "active"
	BannedStatus UserStatus = "banned"
)

type User struct {
	ID         string     `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Username   string     `json:"username"`
	Email      string     `json:"email"`
	Phone      string     `json:"phone"`
	Password   string     `json:"password"`
	Fullname   string     `json:"full_name"`
	CitizenId  string     `json:"citizen_id"`
	Status     UserStatus `json:"status"`
	Reputation int        `json:"reputation"`
	BanStart   *time.Time `json:"ban_start,omitempty"`
	BanEnd     *time.Time `json:"ban_end,omitempty"`
	CreatedAt  time.Time  `json:"created_at"`
	UpdatedAt  *time.Time `json:"updated_at,omitempty"`
}

type Follow struct {
	SellerId *string `gorm:"type:uuid;primaryKey" json:"seller_id"`
	BuyerId  *string `gorm:"type:uuid;primaryKey" json:"buyer_id"`

	Seller *User `json:"seller,omitempty"`
	Buyer  *User `json:"buyer,omitempty"`
}
