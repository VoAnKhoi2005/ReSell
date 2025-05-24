package middleware

import (
	"errors"
	"fmt"
	"os"
	"strconv"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

func CreateAccessToken(ID string, role string) (accessToken string, err error) {
	AccessTokenExpiryHour, err := strconv.Atoi(os.Getenv("ACCESS_TOKEN_EXPIRY_HOUR"))
	if err != nil {
		return "", err
	}
	exp := time.Now().Add(time.Hour * time.Duration(AccessTokenExpiryHour)).Unix()

	claims := jwt.MapClaims{
		"id":   ID,
		"role": role,
		"exp":  exp,
	}

	secret := os.Getenv("ACCESS_TOKEN_SECRET")
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	t, err := token.SignedString([]byte(secret))
	if err != nil {
		return "", err
	}
	return t, err
}

func CreateRefreshToken(ID string, role string) (refreshToken string, err error) {
	RefreshTokenExpiryHour, err := strconv.Atoi(os.Getenv("REFRESH_TOKEN_EXPIRY_HOUR"))
	if err != nil {
		return "", err
	}

	exp := time.Now().Add(time.Hour * time.Duration(RefreshTokenExpiryHour)).Unix()

	claims := jwt.MapClaims{
		"id":   ID,
		"role": role,
		"exp":  exp,
		"type": "refresh",
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

func ExtractIDFromToken(requestToken string) (string, error) {
	secret := os.Getenv("ACCESS_TOKEN_SECRET")
	if secret == "" {
		return "", errors.New("no secret provided")
	}

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

	if !ok || !token.Valid {
		return "", fmt.Errorf("invalid Token")
	}

	ID, ok := claims["id"].(string)
	if !ok {
		return "", fmt.Errorf("id claim is missing or not a string")
	}

	return ID, nil
}

func ExtractRoleFromToken(tokenString string) (string, error) {
	secret := os.Getenv("ACCESS_TOKEN_SECRET")
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(secret), nil
	})
	if err != nil {
		return "", err
	}

	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok || !token.Valid {
		return "", fmt.Errorf("invalid token")
	}

	role, ok := claims["role"].(string)
	if !ok {
		return "", fmt.Errorf("role claim is missing or not a string")
	}

	return role, nil
}
