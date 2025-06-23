package transaction

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
)

type RegisterRequest struct {
	Username string `json:"username" binding:"required"`
	Email    string `json:"email" binding:"required"`
	Phone    string `json:"phone" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type FirebaseAuthRequest struct {
	FirebaseIDToken string  `json:"firebase_id_token" binding:"required"`
	Username        *string `json:"username"`
	Password        *string `json:"password"`
}

type FirebaseAuthResponse struct {
	User           *model.User    `json:"user"`
	Token          *TokenResponse `json:"token"`
	FirstTimeLogin bool           `json:"first_time_login"`
}

type LoginRequest struct {
	Identifier string `json:"identifier" binding:"required"`
	Password   string `json:"password" binding:"required"`
	LoginType  string `json:"login_type" binding:"required"` // expects "email", "phone", "username"
}

type LoginResponse struct {
	User  *model.User   `json:"user"`
	Token TokenResponse `json:"token"`
}

type RegisterAdminRequest struct {
	Username string `json:"username" binding:"required"`
	Email    string `json:"email" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type LoginAdminRequest struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type LoginAdminResponse struct {
	Admin *model.Admin  `json:"admin"`
	Token TokenResponse `json:"token"`
}

type RefreshTokenRequest struct {
	RefreshToken string `json:"refresh_token" binding:"required"`
}

type TokenResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}
