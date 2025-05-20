package middleware

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/golang-jwt/jwt/v5"
	"time"
)

func CreateAccessToken(user models.User, secret string, expiryHours int) (accessToken string, err error) {
	exp := time.Now().Add(time.Hour * time.Duration(expiryHours)).Unix()

	claims := jwt.MapClaims{
		"user_id": user.ID,
		"exp":     exp,
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	t, err := token.SignedString([]byte(secret))
	if err != nil {
		return "", err
	}
	return t, err
}
