package util

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/gin-gonic/gin"
	"net/http"
)

func GetUserID(c *gin.Context) (string, error) {
	userIDValue, exists := c.Get("x-user-id")
	if !exists {
		c.JSON(http.StatusBadRequest, gin.H{"error": "requester_id not found"})
		return "", errors.New("requester_id not found")
	}

	userID, ok := userIDValue.(string)
	if !ok {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "invalid user ID format"})
		return "", errors.New("invalid user ID format")
	}

	return userID, nil
}

func IsUserOwner(c *gin.Context, expectedID string) bool {

	userID, err := GetUserID(c)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Invalid user Name format"})
		return false
	}

	if userID != expectedID {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "You are not the owner of the user"})
		return false
	}

	return true
}

func IsAdmin(c *gin.Context, expectedID string) bool {
	adminIDValue, exists := c.Get("x-admin-id")
	if !exists {
		c.JSON(http.StatusBadRequest, gin.H{"error": "requester_id not found"})
		return false
	}

	adminID, ok := adminIDValue.(string)
	if !ok {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Invalid admin Name format"})
		return false
	}

	if adminID != expectedID {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "You are not an admin"})
		return false
	}

	return true
}

func GenerateToken(id string, role string) (accessToken string, refreshToken string, err error) {
	accessToken, err = middleware.CreateAccessToken(id, role)
	if err != nil {
		return "", "", err
	}

	refreshToken, err = middleware.CreateRefreshToken(id, role)
	if err != nil {
		return "", "", err
	}

	return accessToken, refreshToken, nil
}
