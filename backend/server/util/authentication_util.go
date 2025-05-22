package util

import (
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/gin-gonic/gin"
	"net/http"
)

func IsUserOwner(c *gin.Context, expectedID string) bool {
	userIDValue, exists := c.Get("x-user-id")
	if !exists {
		c.JSON(http.StatusBadRequest, gin.H{"error": "requester_id not found"})
		return false
	}

	userID, ok := userIDValue.(string)
	if !ok {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Invalid user ID format"})
		return false
	}

	if userID != expectedID {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "You are not the owner of the user"})
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
