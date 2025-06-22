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

type AuthProviderType string

const (
	GoogleAuth AuthProviderType = "google"
	PhoneAuth  AuthProviderType = "phone"
	LocalAuth  AuthProviderType = "local"
)

type User struct {
	ID               string           `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Username         string           `json:"username"`
	Email            string           `json:"email"`
	IsEmailVerified  bool             `json:"is_email_verified"`
	Phone            string           `json:"phone"`
	IsPhoneVerified  bool             `json:"is_phone_verified"`
	Password         string           `json:"password"`
	FirebaseUID      *string          `json:"firebase_uid"`
	AuthProvider     AuthProviderType `json:"auth_provider"`
	Fullname         string           `json:"full_name"`
	Status           UserStatus       `json:"status"`
	Reputation       int              `json:"reputation"`
	BanStart         *time.Time       `json:"ban_start,omitempty"`
	BanEnd           *time.Time       `json:"ban_end,omitempty"`
	CreatedAt        time.Time        `json:"created_at"`
	IsSelling        bool             `json:"is_selling"`                  // Có phải người bán không
	StripeAccountID  *string          `json:"stripe_account_id,omitempty"` // Stripe ID nếu có
	IsStripeVerified bool             `json:"is_stripe_verified"`
	AvatarURL        *string          `json:"avatar_url"`
}

type Follow struct {
	SellerId *string `gorm:"type:uuid;primaryKey" json:"seller_id"`
	BuyerId  *string `gorm:"type:uuid;primaryKey" json:"buyer_id"`

	Seller *User `json:"seller,omitempty"`
	Buyer  *User `json:"buyer,omitempty"`
}

func (u *User) StripeAccountIDValue() string {
	if u.StripeAccountID != nil {
		return *u.StripeAccountID
	}
	return ""
}
