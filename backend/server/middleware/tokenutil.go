package middleware

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/golang-jwt/jwt/v5"
	"os"
	"time"
)

func CreateAccessToken(user *model.User, expiryHours int) (accessToken string, err error) {
	exp := time.Now().Add(time.Hour * time.Duration(expiryHours)).Unix()

	claims := jwt.MapClaims{
		"user_id": user.ID,
		"exp":     exp,
	}

	secret := os.Getenv("ACCESS_TOKEN_SECRET")
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	t, err := token.SignedString([]byte(secret))
	if err != nil {

		return "", err
	}
	return t, err
}

func CreateRefreshToken(user *model.User, expiryHours int) (refreshToken string, err error) {
	exp := time.Now().Add(time.Hour * time.Duration(expiryHours)).Unix()

	claims := jwt.MapClaims{
		"user_id": user.ID,
		"exp":     exp,
		"type":    "refresh",
	}

	secret := os.Getenv("REFRESH_TOKEN_SECRET")
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	t, err := token.SignedString([]byte(secret))
	if err != nil {
		return "", err
	}
	return t, nil
}

func IsAuthorized(requestToken string, secret string) (bool, error) {
	_, err := jwt.Parse(requestToken, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(secret), nil
	})
	if err != nil {
		return false, err
	}
	return true, nil
}

func ExtractIDFromToken(requestToken string, secret string) (string, error) {
	token, err := jwt.Parse(requestToken, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(secret), nil
	})

	if err != nil {
		return "", err
	}

	claims, ok := token.Claims.(jwt.MapClaims)

	if !ok && !token.Valid {
		return "", fmt.Errorf("invalid Token")
	}

	return claims["id"].(string), nil
}
