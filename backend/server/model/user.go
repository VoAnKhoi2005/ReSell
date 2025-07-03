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
	Email            *string          `json:"email,omitempty"`
	IsEmailVerified  bool             `json:"is_email_verified"`
	Phone            *string          `json:"phone,omitempty"`
	IsPhoneVerified  bool             `json:"is_phone_verified"`
	Password         string           `json:"password"`
	FirebaseUID      *string          `json:"firebase_uid,omitempty"`
	AuthProvider     AuthProviderType `json:"auth_provider"`
	Fullname         string           `json:"full_name"`
	Status           UserStatus       `json:"status"`
	Reputation       int              `json:"reputation"`
	BanStart         *time.Time       `json:"ban_start,omitempty"`
	BanEnd           *time.Time       `json:"ban_end,omitempty"`
	BanReason        *string          `json:"ban_reason,omitempty"`
	CreatedAt        time.Time        `json:"created_at"`
	IsSelling        bool             `json:"is_selling"`                  // Có phải người bán không
	StripeAccountID  *string          `json:"stripe_account_id,omitempty"` // Stripe ID nếu có
	IsStripeVerified bool             `json:"is_stripe_verified"`
	AvatarURL        *string          `json:"avatar_url,omitempty"`
	CoverURL         *string          `json:"cover_url,omitempty"`
}

type Follow struct {
	FollowerID *string `gorm:"type:uuid;primaryKey" json:"follower_id"`
	FolloweeID *string `gorm:"type:uuid;primaryKey" json:"followee_id"`

	Follower *User `json:"follower,omitempty"`
	Followee *User `json:"followee,omitempty"`
}

func (u *User) StripeAccountIDValue() string {
	if u.StripeAccountID != nil {
		return *u.StripeAccountID
	}
	return ""
}
