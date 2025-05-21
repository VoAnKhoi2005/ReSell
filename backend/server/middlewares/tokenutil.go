package middlewares

import (
	"errors"
	"fmt"
	"github.com/golang-jwt/jwt/v5"
	"os"
	"strconv"
	"time"
)

func CreateAccessToken(userID string) (accessToken string, err error) {
	AccessTokenExpiryHour, err := strconv.Atoi(os.Getenv("ACCESS_TOKEN_EXPIRY_HOUR"))
	if err != nil {
		return "", err
	}
	exp := time.Now().Add(time.Hour * time.Duration(AccessTokenExpiryHour)).Unix()

	claims := jwt.MapClaims{
		"user_id": userID,
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

func CreateRefreshToken(userID string) (refreshToken string, err error) {
	RefreshTokenExpiryHour, err := strconv.Atoi(os.Getenv("REFRESH_TOKEN_EXPIRY_HOUR"))
	if err != nil {
		return "", err
	}

	exp := time.Now().Add(time.Hour * time.Duration(RefreshTokenExpiryHour)).Unix()

	claims := jwt.MapClaims{
		"user_id": userID,
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

func IsAuthorized(requestToken string) (bool, error) {
	secret := os.Getenv("ACCESS_TOKEN_SECRET")
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

func ExtractIDFromToken(requestToken string) (uint, error) {
	secret := os.Getenv("REQUEST_TOKEN_SECRET")
	if secret == "" {
		return 0, errors.New("no secret provided")
	}

	token, err := jwt.Parse(requestToken, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(secret), nil
	})

	if err != nil {
		return 0, err
	}

	claims, ok := token.Claims.(jwt.MapClaims)

	if !ok || !token.Valid {
		return 0, fmt.Errorf("invalid Token")
	}

	idFloat, ok := claims["id"].(float64)
	if !ok {
		return 0, fmt.Errorf("id claim not found or not a number")
	}

	return uint(idFloat), nil
}
