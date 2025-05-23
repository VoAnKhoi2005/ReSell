package transaction

import "github.com/VoAnKhoi2005/ReSell/model"

type RegisterRequest struct {
	Username string `json:"username" binding:"required"`
	Email    string `json:"email" binding:"required"`
	Phone    string `json:"phone" binding:"required"`
	Password string `json:"password" binding:"required"`
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
	Admin *model.Admin  `json:"user"`
	Token TokenResponse `json:"token"`
}

type RefreshTokenRequest struct {
	RefreshToken string `json:"refresh_token" binding:"required"`
}

type TokenResponse struct {
	AccessToken  string `json:"access_Token"`
	RefreshToken string `json:"refresh_token"`
}
